package com.alaeldin.employee_department_api.controller;

import com.alaeldin.employee_department_api.dto.EmployeeDto;
import com.alaeldin.employee_department_api.service.impl.EmployeeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class EmploymentControllerTest
{
    private MockMvc mockMvc;

    @Mock
    private EmployeeServiceImpl employeeService;
    @InjectMocks
    private EmploymentController employeeController;
    @Mock
    private PagedResourcesAssembler<EmployeeDto> pagedResourcesAssembler;

    @InjectMocks
    private EmploymentController employmentController;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(employmentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void whenSaveEmployee_thenResponseCreated() throws Exception {

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName("Suliman");
        employeeDto.setLastName("Hassan");
        employeeDto.setEmail("alaeldinmusa96@gmail.com");
        employeeDto.setDepartmentId(1);
        MvcResult result = mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response content: " + responseContent);

    }

    @Test
    public void searchEmployees_whenValidParams_thenReturnsEmployeeDtos() {

        String firstName = "Alaeldin";
        String lastName = "Musa";
        String email = "alaeldinmusa91@gmail.com";
        int page = 0;
        int size = 10;
        List<EmployeeDto> employeeDtos = new ArrayList<>();
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName("Alaeldin");
        employeeDto.setLastName("Musa");
        employeeDto.setEmail("alaeldinmusa91@gmail.com");
        employeeDtos.add(employeeDto);
        Page<EmployeeDto> employeePage = new PageImpl<>(employeeDtos, PageRequest.of(page, size)
                , employeeDtos.size());

        when(employeeService.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                firstName, lastName, email, page, size)).thenReturn(employeePage);

        List<EntityModel<EmployeeDto>> entityModels = employeeDtos.stream()
                .map(EntityModel::of)
                .toList();
        PagedModel<EntityModel<EmployeeDto>> pagedModel = PagedModel.of(entityModels
                , new PagedModel.PageMetadata(size, page, employeeDtos.size()));

        when(pagedResourcesAssembler.toModel(employeePage, EntityModel::of)).thenReturn(pagedModel);
        ResponseEntity<PagedModel<EntityModel<EmployeeDto>>> response = employeeController.searchEmployees(
                firstName, lastName, email, page, size
        );

        System.out.println("Response: " + response);
        System.out.println("Response Body: " + response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void whenGetAllEmployments_thenReturnPageOfEmployments() throws Exception {

        int page = 0;
        int size = 10;
        List<EmployeeDto> employeeDtos = new ArrayList<>();
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName("Alaeldin");
        employeeDto.setLastName("Musa");
        employeeDto.setEmail("alaeldinmusa91@gmail.com");
        employeeDtos.add(employeeDto);
        Page<EmployeeDto> employeePage = new PageImpl<>(employeeDtos, PageRequest.of(page, size), employeeDtos.size());
        when(employeeService.getAllEmployees(
                 page, size)).thenReturn(employeePage);

        try {
            mockMvc.perform(get("/api/v1/employee/get-all-employee")
                            .param("number", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void whenUpdateDepartment_thenReturnSuccessMessage() throws Exception {

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstName("Updated Employee");
        String successMessage = "Employee updated successfully!";
        when(employeeService.updateEmployee(any(EmployeeDto.class))).thenReturn(successMessage);
        mockMvc.perform(post("/api/v1/employee/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(employeeDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        }

        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
