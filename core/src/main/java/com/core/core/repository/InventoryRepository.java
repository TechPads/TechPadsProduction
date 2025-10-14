package com.core.core.repository;

import com.core.core.module.InventoryClass;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<InventoryClass, String> {

}
