package com.core.core.repository;

import com.core.core.modules.InventoryClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryClass, Long> {

}
