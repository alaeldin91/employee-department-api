package com.alaeldin.employee_department_api.controller;

import com.alaeldin.employee_department_api.dto.EmployeeDto;
import com.alaeldin.employee_department_api.service.impl.EmployeeServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmploymentController
{
    private final EmployeeServiceImpl employeeService;
    private final PagedResourcesAssembler<EmployeeDto> pagedResourcesAssembler;

    @PostMapping()
    public ResponseEntity<String> saveEmployement(@Valid @RequestBody EmployeeDto
                                                         employeeDto)
    {
        return new ResponseEntity<>(employeeService.saveEmployee(employeeDto),
                HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<EmployeeDto>>> searchEmployees(
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<EmployeeDto> employeePage = employeeService.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                firstName, lastName, email, page, size);

        // Wrap EmployeeDto in EntityModel
        PagedModel<EntityModel<EmployeeDto>> pagedModel = pagedResourcesAssembler.toModel(employeePage, EntityModel::of
        );

        return new ResponseEntity<>(pagedModel, HttpStatus.OK);
    }

    @GetMapping("/get-all-employee")
    public ResponseEntity<PagedModel<EntityModel<EmployeeDto>>> getAllEmployees(@RequestParam(value = "number", defaultValue = "0") int number,
                                                               @RequestParam(value = "size", defaultValue = "10") int size){

        Page<EmployeeDto> employeePage  = employeeService.getAllEmployees(number,size);
        PagedModel<EntityModel<EmployeeDto>> pagedModel = pagedResourcesAssembler.toModel(employeePage, EntityModel::of);

        return new ResponseEntity<>(pagedModel,HttpStatus.OK);
    }

    @GetMapping("/get_employee_by_id/{id}")
    public ResponseEntity<EmployeeDto> getAllEmployeeById(@PathVariable("id") long id){

        return new ResponseEntity<>(employeeService.getEmployeeById(id),HttpStatus.OK);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Map<String, String>> deleteEmployment(@PathVariable("id") long id) {

        employeeService.deleteEmployee(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Delete Employee Successfully");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @PostMapping("update/{id}")
    public ResponseEntity<String> updateEmployee(@RequestBody EmployeeDto employeeDto
            ,@PathVariable("id") long id){

        employeeDto.setId(id);

        return new ResponseEntity<>(employeeService.updateEmployee(employeeDto),HttpStatus.OK);
    }
}
