package com.alaeldin.employee_department_api.mapper;

import com.alaeldin.employee_department_api.dto.DepartmentDto;
import com.alaeldin.employee_department_api.model.Department;

public class MapperDepartment
{
    public static DepartmentDto mapToDepartmentDto(Department department){

        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(department.getId());
        departmentDto.setName(department.getName());

        return departmentDto;
    }

    public static Department mapToDepartment(DepartmentDto departmentDto){

        Department department = new Department();
        department.setId(departmentDto.getId());
        department.setName(departmentDto.getName());

        return department;
    }
}
