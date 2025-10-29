package com.core.core.controllers;

import com.core.core.modules.InventoryClass;
import com.core.core.services.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    @Lazy
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryClass>> getAllInventory() {
        List<InventoryClass> list = inventoryService.getAllInventory();
        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getInventoryByCode(@PathVariable Long code) {
        InventoryClass inventory = inventoryService.getInventory(code);
        if(inventory == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el inventario con el código: " + code);
        }
        return ResponseEntity.ok(inventory);
    }

    @PostMapping
    public ResponseEntity<?> createInventory(@Valid @RequestBody InventoryClass inventory,
                                             BindingResult result) {
        if(result.hasErrors()){
            // Retorna todos los errores de validación
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        InventoryClass newInventory = inventoryService.createInventory(inventory);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(newInventory.getInvCode())
                .toUri();
        return ResponseEntity.created(location).body(newInventory);
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> updateInventory(@PathVariable Long code,
                                             @Valid @RequestBody InventoryClass inventory,
                                             BindingResult result) {
        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + ": " + e.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        InventoryClass newInventory = inventoryService.updateInventory(code, inventory);
        if(newInventory == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el inventario con el código: " + code);
        }
        return ResponseEntity.ok(newInventory);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long code) {
        boolean deleted = inventoryService.deleteInventory(code);
        if(!deleted){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el inventario con el código: " + code);
        }
        return ResponseEntity.noContent().build();
    }
}
