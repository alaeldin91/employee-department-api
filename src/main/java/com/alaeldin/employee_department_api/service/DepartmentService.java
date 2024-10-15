package com.alaeldin.employee_department_api.service;

import com.alaeldin.employee_department_api.dto.DepartmentDto;
import org.springframework.data.domain.Page;
public interface DepartmentService
{
    String saveDepartment(DepartmentDto departmentDto);
    Page<DepartmentDto> findByNameContainingIgnoreCase(String name
                                                     ,int number
                                                     , int size);
    Page<DepartmentDto> getAllDepartment(int number
                                         ,int size);
    DepartmentDto getDepartmentById(long id);
    void deleteDepartment(long id);
    String updateDepartment(DepartmentDto departmentDto);

}
