package com.alaeldin.employee_department_api.mapper;

import com.alaeldin.employee_department_api.dto.EmployeeDto;
import com.alaeldin.employee_department_api.model.Employee;


public class MapperEmployment
{
    public static EmployeeDto mapToEmployeeDto(Employee employee)
    {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(employee.getId());
        employeeDto.setFirstName(employee.getFirstName());
        employeeDto.setLastName(employee.getLastName());
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setContactNumber(employee.getContactNumber());
        employeeDto.setDepartmentId(employee.getDepartment().getId());

    return employeeDto;
    }
    public static Employee mapToEmployee(EmployeeDto employeeDto)
    {
        Employee employee = new Employee();
        employee.setId(employeeDto.getId());
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setEmail(employeeDto.getEmail());
        employee.setContactNumber(employeeDto.getContactNumber());

        return employee;
    }
}
