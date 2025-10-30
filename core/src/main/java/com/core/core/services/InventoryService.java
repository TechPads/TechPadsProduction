package com.core.core.services;

import com.core.core.modules.InventoryClass;

import java.util.List;

public interface InventoryService {

    List<InventoryClass> getAllInventory();
    InventoryClass getInventory(Long id);
    InventoryClass createInventory(InventoryClass inventoryClass);
    InventoryClass updateInventory(Long code, InventoryClass inventoryClass);
    boolean deactivateInventory(Long code);

}
