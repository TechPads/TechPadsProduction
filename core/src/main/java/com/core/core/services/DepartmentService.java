package com.core.core.services;

import com.core.core.modules.Department;

import java.util.List;

public interface DepartmentService {

    List<Department> getDepartments();
    Department getDepartment(Long id);
}
