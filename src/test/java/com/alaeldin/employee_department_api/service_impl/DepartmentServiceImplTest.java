package com.alaeldin.employee_department_api.service_impl;

import com.alaeldin.employee_department_api.dto.DepartmentDto;
import com.alaeldin.employee_department_api.exception.DataAlreadyExist;
import com.alaeldin.employee_department_api.exception.ResourceNotFoundException;
import com.alaeldin.employee_department_api.model.Department;
import com.alaeldin.employee_department_api.repository.DepartmentRepository;
import com.alaeldin.employee_department_api.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DepartmentServiceImplTest
{
    @Mock
    private DepartmentRepository departmentRepository;
    @InjectMocks
    private DepartmentServiceImpl departmentService;
    private DepartmentDto departmentDto;
    private Department department;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        departmentDto = new DepartmentDto();  // Now class-level variable
        departmentDto.setName("IT Department");

        department = new Department();  // Now class-level variable
        department.setName("IT Department");
    }

    @Test
    void saveDepartment_ShouldThrowDataAlreadyExist_WhenDepartmentAlreadyExists() {
        when(departmentRepository.findByName(departmentDto.getName()))
                .thenReturn(Optional.of(department));

        DataAlreadyExist exception = assertThrows(DataAlreadyExist.class, () -> {
            departmentService.saveDepartment(departmentDto);
        });

        assertEquals("Department Already is found", exception.getMessage());
        verify(departmentRepository, times(1))
                .findByName(departmentDto.getName());
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    public void testFindByNameContainingIgnoreCase() {
        // Given
        String name = "Finance";
        int pageNumber = 0;
        int pageSize = 10;
        Department department1 = new Department();
        department1.setId(1L);
        department1.setName("Finance Department");
        Department department2 = new Department();
        department2.setId(2L);
        department2.setName("Finance and Budgeting");
        List<Department> departments = Arrays.asList(department1, department2);
        Page<Department> departmentPage = new PageImpl<>(departments);
        when(departmentRepository.findByNameContainingIgnoreCase(name, PageRequest.of(pageNumber, pageSize)))
                .thenReturn(departmentPage);
        Page<DepartmentDto> result = departmentService.findByNameContainingIgnoreCase(name, pageNumber, pageSize);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Finance Department", result.getContent().get(0).getName());
        assertEquals("Finance and Budgeting", result.getContent().get(1).getName());
    }

    @Test
    public void testGetAllDepartment() {
        int pageNumber = 0;
        int pageSize = 10;

        Department department1 = new Department();
        department1.setId(1);
        department1.setName("HR");
        Department department2 = new Department();
        department2.setId(2);
        department2.setName("Finance");
        List<Department> departments = Arrays.asList(department1, department2);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Department> departmentPage = new PageImpl<>(departments, pageable, departments.size());
        when(departmentRepository.findAll(pageable)).thenReturn(departmentPage);
        Page<DepartmentDto> result = departmentService.getAllDepartment(pageNumber, pageSize);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("HR", result.getContent().get(0).getName());
        assertEquals("Finance", result.getContent().get(1).getName());
        verify(departmentRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetDepartmentById_Success() {
        long departmentId = 1L;
        Department department = new Department();
        department.setId(departmentId);
        department.setName("HR");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        DepartmentDto result = departmentService.getDepartmentById(departmentId);
        assertNotNull(result);
        assertEquals(departmentId, result.getId());
        assertEquals("HR", result.getName());
        verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    public void testGetDepartmentById_NotFound() {

        long departmentId = 1L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            departmentService.getDepartmentById(departmentId);
        });

        assertEquals("Department", exception.getResourceName());
        assertEquals("id", exception.getFieldName());
        assertEquals(departmentId, exception.getFieldValue());
        verify(departmentRepository, times(1)).findById(departmentId);
    }

    @Test
    public void testUpdateDepartment_Success() {
        // Arrange
        long departmentId = 1L;
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(departmentId);
        departmentDto.setName("HR");

        Department existingDepartment = new Department();
        existingDepartment.setId(departmentId);
        existingDepartment.setName("Old Name");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));

        String result = departmentService.updateDepartment(departmentDto);

        assertEquals("Department updated successfully!", result);
        assertEquals("HR", existingDepartment.getName());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).save(existingDepartment); // Ensure save was called
    }

    @Test
    public void testUpdateDepartment_NotFound()
    {

        long departmentId = 1L;
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(departmentId);
        departmentDto.setName("HR");
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            departmentService.updateDepartment(departmentDto);
        });

        assertEquals("Department", exception.getResourceName());
        assertEquals("id", exception.getFieldName());
        assertEquals(departmentId, exception.getFieldValue());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, never()).save(any());
    }

    @Test
    public void testDeleteDepartment_Success() {

        long departmentId = 1L;
        Department existingDepartment = new Department();
        existingDepartment.setId(departmentId);
        existingDepartment.setName("HR");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));

        departmentService.deleteDepartment(departmentId);

        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).delete(existingDepartment); // Ensure delete was called
    }

    @Test
    public void testDeleteDepartment_NotFound() {

        long departmentId = 1L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            departmentService.deleteDepartment(departmentId);
        });

        assertEquals("Department", exception.getResourceName());
        assertEquals("id", exception.getFieldName());
        assertEquals(departmentId, exception.getFieldValue());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, never()).delete(any());
    }
}
