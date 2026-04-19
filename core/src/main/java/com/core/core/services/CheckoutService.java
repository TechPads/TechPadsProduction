package com.core.core.services;

import com.core.core.dto.*;
import com.core.core.modules.*;
import com.core.core.repository.*;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CheckoutService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final BillRepository billRepository;
    private final UserRepository userRepository;

    // Configuración de reintentos
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MILLIS = 100;
    private static final double BACKOFF_MULTIPLIER = 2.0;

    public CheckoutService(EntityManager entityManager, OrderRepository orderRepository,
                          OrderDetailRepository orderDetailRepository, BillRepository billRepository,
                          UserRepository userRepository) {
        this.entityManager = entityManager;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.billRepository = billRepository;
        this.userRepository = userRepository;
    }

    /**
     * Procesar compra utilizando el procedimiento almacenado PL/SQL
     * Con reintentos automáticos para errores de serialización de transacciones
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 30)
    public CheckoutResponse procesarCompra(CheckoutRequest request) {
        return procesarCompraConReintentos(request, 0);
    }

    /**
     * Método recursivo con lógica de reintentos para ORA-08177
     */
    private CheckoutResponse procesarCompraConReintentos(CheckoutRequest request, int intentoActual) {
        try {
            log.info("Procesando compra para usuario: {} (intento {}/{})",
                    request.getUserID(), intentoActual + 1, MAX_RETRIES);

            // Verificar usuario
            User user = userRepository.findById(request.getUserID())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // IMPORTANTE: limpiar contexto JPA
            entityManager.flush();
            entityManager.clear();

            // ==============================
            // LLAMADA A POSTGRES FUNCTION
            // ==============================
            Query query = entityManager.createNativeQuery(
                    "SELECT procesar_compra(:userId, :paymentType)"
            );

            query.setParameter("userId", request.getUserID());
            query.setParameter("paymentType", request.getPaymentType());

            Long ordID = ((Number) query.getSingleResult()).longValue();

            log.info("Orden creada exitosamente con ID: {}", ordID);

            // ==============================
            // CONSULTAR ORDEN
            // ==============================
            Order order = orderRepository.findById(ordID)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            // ==============================
            // CONSULTAR FACTURA
            // ==============================
            Bill bill = billRepository.findByOrderId(ordID)
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

            // ==============================
            // RESPONSE
            // ==============================
            return CheckoutResponse.builder()
                    .ordID(ordID)
                    .ordState(order.getOrdState())
                    .ordDate(order.getOrdDate())
                    .totalBill(bill.getTotalBill())
                    .paymentType(bill.getPaymentType())
                    .billCode(bill.getBillCode())
                    .message("Compra procesada exitosamente")
                    .build();

        } catch (Exception e) {

            String errorMsg = e.getMessage();

            boolean retry = errorMsg != null &&
                    (errorMsg.contains("serialization") ||
                            errorMsg.contains("deadlock"));

            if (retry && intentoActual < MAX_RETRIES) {

                long backoffMs = (long) (INITIAL_BACKOFF_MILLIS *
                        Math.pow(BACKOFF_MULTIPLIER, intentoActual));

                log.warn("Error concurrente. Reintentando en {}ms... ({}/{})",
                        backoffMs, intentoActual + 1, MAX_RETRIES);

                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                entityManager.clear();

                return procesarCompraConReintentos(request, intentoActual + 1);
            }

            log.error("Error en compra (intento {}/{}): {}",
                    intentoActual + 1, MAX_RETRIES, e.getMessage(), e);

            throw new RuntimeException("Error al procesar la compra: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<OrderSummaryDTO> getOrdersWithFilters(String state) {
        try {
            log.info("Obteniendo órdenes con filtros - estado: {}", state);

            List<Order> orders;
            if (state != null && !state.equals("all")) {
                orders = orderRepository.findByOrdStateOrderByOrdDateDesc(state);
            } else {
                orders = orderRepository.findAllOrderByOrdDateDesc();
            }

            return orders.stream().map(order -> {
                OrderSummaryDTO dto = new OrderSummaryDTO();
                dto.setOrdID(order.getOrdID());
                dto.setOrdState(order.getOrdState());
                dto.setOrdDate(order.getOrdDate());

                billRepository.findByOrderId(order.getOrdID()).ifPresent(bill -> {
                    dto.setTotal(bill.getTotalBill());
                    dto.setPaymentType(bill.getPaymentType());
                });

                dto.setItemCount(order.getOrderDetails() != null ? order.getOrderDetails().size() : 0);

                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error al obtener órdenes con filtros: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener órdenes con filtros: " + e.getMessage());
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 10)
    public void updateOrderStatus(Long ordID, String newState) {
        try {
            log.info("Actualizando estado de orden {} a {}", ordID, newState);

            // Limpiar sesión para evitar conflictos
            entityManager.clear();

            Order order = orderRepository.findById(ordID)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            // Validar que el estado sea válido
            List<String> validStates = List.of("Pending", "Processing", "Shipped", "Delivered", "Cancelled");
            if (!validStates.contains(newState)) {
                throw new RuntimeException("Estado inválido: " + newState);
            }

            // Validar transiciones de estado (opcional, para mayor control)
            if (!isValidStateTransition(order.getOrdState(), newState)) {
                throw new RuntimeException("Transición de estado no permitida: " +
                        order.getOrdState() + " -> " + newState);
            }

            order.setOrdState(newState);
            orderRepository.save(order);

            log.info("Estado de orden {} actualizado exitosamente a {}", ordID, newState);

        } catch (Exception e) {
            log.error("Error al actualizar estado de orden: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar estado de orden: " + e.getMessage());
        }
    }

    private boolean isValidStateTransition(String currentState, String newState) {
        // Definir transiciones permitidas
        if (currentState.equals("Cancelled") || currentState.equals("Delivered")) {
            return false; // No se puede cambiar desde cancelado o entregado
        }

        if (currentState.equals("Pending") &&
                !List.of("Processing", "Cancelled").contains(newState)) {
            return false;
        }

        if (currentState.equals("Processing") &&
                !List.of("Shipped", "Cancelled").contains(newState)) {
            return false;
        }

        if (currentState.equals("Shipped") && !newState.equals("Delivered")) {
            return false;
        }

        return true;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<OrderSummaryDTO> getAllOrders() {
        try {
            log.info("Obteniendo todas las órdenes del sistema");

            List<Order> orders = orderRepository.findAllOrderByOrdDateDesc();

            return orders.stream().map(order -> {
                OrderSummaryDTO dto = new OrderSummaryDTO();
                dto.setOrdID(order.getOrdID());
                dto.setOrdState(order.getOrdState());
                dto.setOrdDate(order.getOrdDate());

                // Obtener información de la factura
                billRepository.findByOrderId(order.getOrdID()).ifPresent(bill -> {
                    dto.setTotal(bill.getTotalBill());
                    dto.setPaymentType(bill.getPaymentType());
                });

                // Contar items
                dto.setItemCount(order.getOrderDetails() != null ? order.getOrderDetails().size() : 0);

                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error al obtener todas las órdenes: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener todas las órdenes: " + e.getMessage());
        }
    }

    /**
     * Obtener historial de órdenes del usuario
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<OrderSummaryDTO> getOrderHistory(Long userID) {
        try {
            log.info("Obteniendo historial de órdenes para usuario: {}", userID);

            List<Order> orders = orderRepository.findByUserIdOrderByOrdDateDesc(userID);

            return orders.stream().map(order -> {
                OrderSummaryDTO dto = new OrderSummaryDTO();
                dto.setOrdID(order.getOrdID());
                dto.setOrdState(order.getOrdState());
                dto.setOrdDate(order.getOrdDate());

                // Obtener información de la factura
                billRepository.findByOrderId(order.getOrdID()).ifPresent(bill -> {
                    dto.setTotal(bill.getTotalBill());
                    dto.setPaymentType(bill.getPaymentType());
                });

                // Contar items
                dto.setItemCount(order.getOrderDetails() != null ? order.getOrderDetails().size() : 0);

                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error al obtener historial de órdenes: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener historial de órdenes: " + e.getMessage());
        }
    }

    /**
     * Obtener detalle completo de una orden
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public OrderDetailDTO getOrderDetail(Long ordID) {
        try {
            log.info("Obteniendo detalle de orden: {}", ordID);

            // Obtener orden con sus detalles
            Order order = orderRepository.findByIdWithDetails(ordID)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            // Obtener factura
            Bill bill = billRepository.findByOrderId(ordID).orElse(null);

            // Construir DTO
            OrderDetailDTO dto = new OrderDetailDTO();
            dto.setOrdID(order.getOrdID());
            dto.setOrdState(order.getOrdState());
            dto.setOrdDate(order.getOrdDate());

            if (bill != null) {
                dto.setTotal(bill.getTotalBill());
                dto.setPaymentType(bill.getPaymentType());
                dto.setBillCode(bill.getBillCode());
            }

            // Mapear items
            List<OrderItemDTO> items = order.getOrderDetails().stream()
                    .map(detail -> OrderItemDTO.builder()
                            .proCode(detail.getProduct().getProCode())
                            .proName(detail.getProduct().getProName())
                            .proImg(detail.getProduct().getProImg())
                            .proMark(detail.getProduct().getProMark())
                            .quantity(detail.getQuantity())
                            .unitPrice(detail.getUnitPrice())
                            .subtotal(detail.getSubtotal())
                            .build())
                    .collect(Collectors.toList());

            dto.setItems(items);

            // Información del usuario
            User user = order.getUser();
            UserInfoDTO userInfo = UserInfoDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .build();

            // Agregar detalles del cliente si existen
            if (user.getClientDetail() != null) {
                ClientDetail client = user.getClientDetail();
                userInfo.setFullName(
                        client.getFirstName() + " " +
                                (client.getSecondName() != null ? client.getSecondName() + " " : "") +
                                client.getFirstLastName() + " " +
                                (client.getSecondLastName() != null ? client.getSecondLastName() : ""));
                userInfo.setAddress(client.getAddress());

                if (client.getCity() != null) {
                    userInfo.setCity(client.getCity().getCityName());
                }

                if (client.getDepartment() != null) {
                    userInfo.setDepartment(client.getDepartment().getDepName());
                }
            }

            dto.setUser(userInfo);

            return dto;

        } catch (Exception e) {
            log.error("Error al obtener detalle de orden: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener detalle de orden: " + e.getMessage());
        }
    }

    /**
     * Cancelar una orden usando el procedimiento almacenado
     * Con reintentos para errores de serialización
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 30)
    public void cancelarOrden(Long ordID, String reason) {
        cancelarOrdenConReintentos(ordID, reason, 0);
    }

    private void cancelarOrdenConReintentos(Long ordID, String reason, int intentoActual) {
        try {
            log.info("Cancelando orden: {} - razón: {} (intento {}/{})",
                    ordID, reason, intentoActual + 1, MAX_RETRIES);

            // =========================
            // VALIDACIÓN LOCAL
            // =========================
            Order order = orderRepository.findById(ordID)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            if ("Delivered".equals(order.getOrdState()) ||
                    "Cancelled".equals(order.getOrdState())) {
                throw new RuntimeException("No se puede cancelar una orden entregada o ya cancelada");
            }

            entityManager.clear();

            // =========================
            // LLAMADA POSTGRES FUNCTION
            // =========================
            Query query = entityManager.createNativeQuery(
                    "SELECT cancelar_orden(:ordId, :reason)"
            );

            query.setParameter("ordId", ordID);
            query.setParameter("reason", reason);

            query.getSingleResult();

            log.info("Orden {} cancelada exitosamente", ordID);

        } catch (Exception e) {

            String msg = e.getMessage();

            boolean retry = msg != null &&
                    (msg.contains("serialization") ||
                            msg.contains("deadlock"));

            if (retry && intentoActual < MAX_RETRIES) {

                long backoffMs = (long) (INITIAL_BACKOFF_MILLIS *
                        Math.pow(BACKOFF_MULTIPLIER, intentoActual));

                log.warn("Reintento cancelación en {}ms ({}/{})",
                        backoffMs, intentoActual + 1, MAX_RETRIES);

                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                entityManager.clear();

                cancelarOrdenConReintentos(ordID, reason, intentoActual + 1);
                return;
            }

            throw new RuntimeException("Error cancelando orden: " + e.getMessage());
        }
    }

    /**
     * Cambiar estado de una orden (para administradores)
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 10)
    public void cambiarEstadoOrden(Long ordID, String newState) {
        try {
            log.info("Cambiando estado de orden {} a {}", ordID, newState);

            entityManager.clear();

            Order order = orderRepository.findById(ordID)
                    .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

            // Validar que el estado sea válido
            List<String> validStates = List.of("Pending", "Processing", "Shipped", "Delivered", "Cancelled");
            if (!validStates.contains(newState)) {
                throw new RuntimeException("Estado inválido: " + newState);
            }

            order.setOrdState(newState);
            orderRepository.save(order);

            log.info("Estado de orden {} actualizado a {}", ordID, newState);

        } catch (Exception e) {
            log.error("Error al cambiar estado de orden: {}", e.getMessage(), e);
            throw new RuntimeException("Error al cambiar estado de orden: " + e.getMessage());
        }
    }

    /**
     * Obtener órdenes pendientes de un usuario
     */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<OrderSummaryDTO> getPendingOrders(Long userID) {
        try {
            List<Order> orders = orderRepository.findPendingOrdersByUser(userID);

            return orders.stream().map(order -> {
                OrderSummaryDTO dto = new OrderSummaryDTO();
                dto.setOrdID(order.getOrdID());
                dto.setOrdState(order.getOrdState());
                dto.setOrdDate(order.getOrdDate());

                billRepository.findByOrderId(order.getOrdID()).ifPresent(bill -> {
                    dto.setTotal(bill.getTotalBill());
                    dto.setPaymentType(bill.getPaymentType());
                });

                dto.setItemCount(order.getOrderDetails() != null ? order.getOrderDetails().size() : 0);

                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error al obtener órdenes pendientes: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener órdenes pendientes: " + e.getMessage());
        }
    }
}