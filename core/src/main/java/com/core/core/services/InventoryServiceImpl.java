package com.core.core.services;

import com.core.core.modules.InventoryClass;
import com.core.core.repository.InventoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<InventoryClass> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public InventoryClass getInventory(Long code) {
        return inventoryRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado " + code));
    }

    @Override
    public InventoryClass createInventory(InventoryClass inventoryClass) {
        return inventoryRepository.save(inventoryClass);
    }

    @Override
    public InventoryClass updateInventory(Long code, InventoryClass inventoryClass) {
        InventoryClass inventory = inventoryRepository.findById(code)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado " + code));
        inventory.setInvStock(inventoryClass.getInvStock());
        inventory.setSellingPrice(inventoryClass.getSellingPrice());
        inventory.setInvDate(inventoryClass.getInvDate());

        return inventoryRepository.save(inventory);
    }

    @Override
    public boolean deactivateInventory(Long code) {
        Optional<InventoryClass> inventoryOpt = inventoryRepository.findById(code);

        if (inventoryOpt.isPresent()) {
            InventoryClass inventory = inventoryOpt.get();
            inventory.setStatus("I");
            inventoryRepository.save(inventory);
            return true;
        }

        return false;
    }

}
