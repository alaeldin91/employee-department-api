package com.alaeldin.employee_department_api.service;

import com.alaeldin.employee_department_api.dto.EmployeeDto;
import org.springframework.data.domain.Page;

public interface EmployeeService
{
    String saveEmployee(EmployeeDto employeeDto);
    Page<EmployeeDto>  findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String firstName, String lastName, String email, int number, int size);
    Page<EmployeeDto> getAllEmployees(int number
            ,int size);
    EmployeeDto getEmployeeById(long id);
    void deleteEmployee(long id);
    String updateEmployee(EmployeeDto employeeDto);

}
