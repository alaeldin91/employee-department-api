package com.alaeldin.employee_department_api.service.impl;

import com.alaeldin.employee_department_api.dto.EmployeeDto;
import com.alaeldin.employee_department_api.exception.DataAlreadyExist;
import com.alaeldin.employee_department_api.exception.ResourceNotFoundException;
import com.alaeldin.employee_department_api.mapper.MapperEmployment;
import com.alaeldin.employee_department_api.model.Department;
import com.alaeldin.employee_department_api.model.Employee;
import com.alaeldin.employee_department_api.repository.DepartmentRepository;
import com.alaeldin.employee_department_api.repository.EmployeeRepository;
import com.alaeldin.employee_department_api.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Override
    public String saveEmployee(EmployeeDto employeeDto) {
        try {

            // Check if the email already exists
            Optional<Employee> existEmployment = employeeRepository.findByEmail(employeeDto.getEmail());
            if (existEmployment.isPresent()) {
                String message = "Email is already found";
                throw new DataAlreadyExist(message);
            }

            Department department = departmentRepository.findById(employeeDto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", "DepartmentId", employeeDto.getDepartmentId()));
            Employee employee = MapperEmployment.mapToEmployee(employeeDto);
            employee.setDepartment(department);
            employeeRepository.save(employee);

            return "Employee saved successfully";
        }

        catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation while saving Employee: " + e.getMessage(), e);
            throw new DataAlreadyExist("Employment data is invalid: " + e.getMessage());
        }
    }

    @Override
    public Page<EmployeeDto> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String firstName, String lastName, String email, int number, int size) {

        Pageable pageable = PageRequest.of(number, size);
        Page<Employee> employees = employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                firstName, lastName, email, pageable
        );

        return employees.map(MapperEmployment::mapToEmployeeDto);
    }


    @Override
    public Page<EmployeeDto> getAllEmployees(int number, int size) {

        Pageable pageable = PageRequest.of(number,size);
        Page<Employee> departments = employeeRepository
                .findAll(pageable);

        return departments.map(MapperEmployment::mapToEmployeeDto);
    }

    @Override
    public EmployeeDto getEmployeeById(long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Employee"
                        ,"id"
                        ,id));

        return MapperEmployment.mapToEmployeeDto(employee);
    }

    @Override
    public void deleteEmployee(long id) {

        Employee employee = employeeRepository.findById(id)
                    .orElseThrow(()-> new ResourceNotFoundException(
                            "employee","id",id));
            employeeRepository.delete(employee);
    }

    @Override
    public String updateEmployee(EmployeeDto employeeDto) {

        Employee employeeExist =  employeeRepository.
                    findById(employeeDto.getId())
                    .orElseThrow(()-> new ResourceNotFoundException("Employee"
                            ,"id",employeeDto.getId()));
            employeeExist.setFirstName(employeeDto.getFirstName());
            employeeRepository.save(employeeExist);

            return "Employee updated successfully!";

        }
    }

