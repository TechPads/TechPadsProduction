package com.core.core.services;

import com.core.core.modules.Department;
import com.core.core.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService{

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<Department> getDepartments(){
        return departmentRepository.findAll();
    }

    @Override
    public Department getDepartment(Long id){
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado " + id));
    }
}
