package com.core.core.repository;

<<<<<<< HEAD

import com.core.core.module.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, String> {

=======
import com.core.core.modules.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, Long> {
>>>>>>> e2adeea2e3b41ec2624064ee8905a99ae0db3319

}
