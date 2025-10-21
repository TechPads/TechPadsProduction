package com.core.core.controllers;

import com.core.core.modules.InventoryClass;
import com.core.core.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<?> createInventory(@RequestBody InventoryClass inventory) {
        InventoryClass newInventory = inventoryService.createInventory(inventory);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{code}")
                .buildAndExpand(newInventory.getInvCode())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{code}")
    public ResponseEntity<?> updateInventory(@PathVariable Long code, @RequestBody InventoryClass inventory) {
        InventoryClass newInventory = inventoryService.updateInventory(code, inventory);
        if(newInventory == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontro el inventario con el codigo: " + code);
        }
        return ResponseEntity.ok(newInventory);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long code) {
        boolean deleted = inventoryService.deleteInventory(code);
        if(!deleted){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontro el inventario con el codigo: " + code);
        }
        return ResponseEntity.noContent().build();
    }
}
