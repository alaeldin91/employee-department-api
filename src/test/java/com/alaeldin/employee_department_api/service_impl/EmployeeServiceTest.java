package com.alaeldin.employee_department_api.service_impl;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import com.alaeldin.employee_department_api.dto.EmployeeDto;
import com.alaeldin.employee_department_api.exception.DataAlreadyExist;
import com.alaeldin.employee_department_api.exception.ResourceNotFoundException;
import com.alaeldin.employee_department_api.model.Department;
import com.alaeldin.employee_department_api.model.Employee;
import com.alaeldin.employee_department_api.repository.DepartmentRepository;
import com.alaeldin.employee_department_api.repository.EmployeeRepository;
import com.alaeldin.employee_department_api.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void whenSaveEmployee_thenReturnSuccessMessage() {

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmail("test@example.com");
        employeeDto.setDepartmentId(1L);
        Department department = new Department();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        String result = employeeService.saveEmployee(employeeDto);
        Assertions.assertEquals("Employee saved successfully", result);

        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void whenSaveEmployeeWithExistingEmail_thenThrowDataAlreadyExistException() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmail("test@example.com");

        when(employeeRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Employee()));

        Exception exception = assertThrows(DataAlreadyExist.class, () -> {
            employeeService.saveEmployee(employeeDto);
        });

        Assertions.assertEquals("Email is already found", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    public void whenSaveEmployeeWithNonExistingDepartment_thenThrowResourceNotFoundException() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmail("test@example.com");
        employeeDto.setDepartmentId(1L);

        when(employeeRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.saveEmployee(employeeDto);
        });

        Assertions.assertEquals("Employee not found with DepartmentId: '1'", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    public void whenFindEmployees_thenReturnPageOfEmployeeDto() {
        // Arrange
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        int pageNumber = 0;
        int pageSize = 5;

        Department department = new Department();
        department.setId(1L);
        department.setName("IT");
        Employee employee1 = new Employee();
        employee1.setFirstName("John");
        employee1.setLastName("Smith");
        employee1.setEmail("john.smith@example.com");
        employee1.setDepartment(department); // Associate the department
        Employee employee2 = new Employee();
        employee2.setFirstName("Jane");
        employee2.setLastName("Doe");
        employee2.setEmail("jane.doe@example.com");
        employee2.setDepartment(department); // Associate the department
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee1);
        employeeList.add(employee2);
        Page<Employee> employeePage = new PageImpl<>(employeeList, PageRequest.of(pageNumber, pageSize), employeeList.size());

        when(employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                firstName, lastName, email, PageRequest.of(pageNumber, pageSize)
        )).thenReturn(employeePage);

        Page<EmployeeDto> result = employeeService.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                firstName, lastName, email, pageNumber, pageSize
        );

        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals(1, result.getTotalPages());
        Assertions.assertEquals("John", result.getContent().get(0).getFirstName());
        Assertions.assertEquals("Jane", result.getContent().get(1).getFirstName());

        verify(employeeRepository).findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                firstName, lastName, email, PageRequest.of(pageNumber, pageSize)
        );
    }

    @Test
    public void testGetAllEmployment() {

        int pageNumber = 0;
        int pageSize = 10;
        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");
        Employee employee1 = new Employee();
        employee1.setId(1);
        employee1.setFirstName("Alaeldin");
        employee1.setLastName("Musa");
        employee1.setEmail("alaeldinmusa91@gmail.com");
        employee1.setDepartment(department);

        Employee employee2 = new Employee();
        employee2.setFirstName("Hassan");
        employee2.setLastName("Suliman");
        employee2.setEmail("alaeldinmusa92@gmail.com");
        employee2.setDepartment(department);

        List<Employee> employees = Arrays.asList(employee1, employee2);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Employee> employeePage = new PageImpl<>(employees, pageable, employees.size());

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        Page<EmployeeDto> result = employeeService.getAllEmployees(pageNumber, pageSize);

        assertNotNull(result);
        Assertions.assertEquals(2, result.getTotalElements());
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals("Alaeldin", result.getContent().get(0).getFirstName());
        Assertions.assertEquals("Hassan", result.getContent().get(1).getFirstName());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetEmployeeById_Success() {

        long employId = 1L;
        Employee employee = new Employee();
        employee.setId(employId);
        employee.setFirstName("Alaeldin");
        employee.setLastName("Musa");
        employee.setEmail("alaeldinmusa91@gmail.com");
        Department department = new Department();
        long departmentId = 1L;
        department.setName("Hr");
        employee.setDepartment(department);


        when(employeeRepository.findById(departmentId)).thenReturn(Optional.of(employee));
        EmployeeDto result = employeeService.getEmployeeById(employId);
        assertNotNull(result);
        Assertions.assertEquals(employId, result.getId());
        Assertions.assertEquals("Alaeldin", result.getFirstName());
        verify(employeeRepository, times(1)).findById(employId);
    }

    @Test
    public void testGetDepartmentById_NotFound() {
        // Arrange
        long employId = 1L;

        when(employeeRepository.findById(employId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.getEmployeeById(employId);
        });

        Assertions.assertEquals("Employee", exception.getResourceName());
        Assertions.assertEquals("id", exception.getFieldName());
        Assertions.assertEquals(employId, exception.getFieldValue());

        verify(employeeRepository, times(1)).findById(employId);
    }

    @Test
    public void testDeleteEmployee_Success() {

        long employeeId = 1L;
        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setFirstName("Alaeldin");
        existingEmployee.setLastName("Musa");
        existingEmployee.setEmail("alaeldinmusa91@gmail.com");
        Department department = new Department();
        department.setName("Hr");
        existingEmployee.setDepartment(department);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        employeeService.deleteEmployee(employeeId);

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).delete(existingEmployee);
    }

    @Test
    public void testDeleteDepartment_NotFound() {

        long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.deleteEmployee(employeeId);
        });

        Assertions.assertEquals("employee", exception.getResourceName());
        Assertions.assertEquals("id", exception.getFieldName());
        Assertions.assertEquals(employeeId, exception.getFieldValue());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).delete(any());
    }
    @Test
    public void testUpdateEmployee_Success() {

        long employeeId = 1L;
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(employeeId);
        employeeDto.setFirstName("Ahamed");
        employeeDto.setLastName("Ali");
        employeeDto.setEmail("ahamed91@gmail.com");
        employeeDto.setDepartmentId(1);

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setFirstName("Old First Name");
        existingEmployee.setLastName("Old Last Name");
        existingEmployee.setLastName("old91@gmail.com");
        Department department = new Department();
        department.setId(1);
        department.setName("Hr");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        String result = employeeService.updateEmployee(employeeDto);

        Assertions.assertEquals("Employee updated successfully!", result);
        Assertions.assertEquals("Ahamed", existingEmployee.getFirstName());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).save(existingEmployee); // Ensure save was called
    }

    @Test
    public void testUpdateDepartment_NotFound()
    {

        long employeeId = 1L;
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setId(employeeId);
        employeeDto.setFirstName("Ahmed");
        employeeDto.setLastName("Ali");
        employeeDto.setEmail("ahamed91@gmail.com");
        employeeDto.setDepartmentId(1);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.updateEmployee(employeeDto);
        });

        assertEquals("Employee", exception.getResourceName());
        assertEquals("id", exception.getFieldName());
        assertEquals(employeeId, exception.getFieldValue());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).save(any());
    }
}

