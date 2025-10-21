package com.core.core.controllers;

import com.core.core.modules.Department;
import com.core.core.modules.InventoryClass;
import com.core.core.services.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/depment")
public class DepartmentController {

    @Autowired
    @Lazy
    private DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<Department>> getDepartments() {
        List<Department> list = departmentService.getDepartments();
        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartment(@PathVariable Long id) {
        Department department = departmentService.getDepartment(id);
        if(department==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el departamento con el id: " + id);
        }
        return ResponseEntity.ok(department);
    }
}
