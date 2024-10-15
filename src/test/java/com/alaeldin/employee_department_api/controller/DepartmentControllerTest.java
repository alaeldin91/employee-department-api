package com.alaeldin.employee_department_api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.alaeldin.employee_department_api.dto.DepartmentDto;
import com.alaeldin.employee_department_api.service.impl.DepartmentServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;


@RunWith(SpringRunner.class)
public class DepartmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DepartmentServiceImpl departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void whenSaveDepartment_thenResponseCreated() throws Exception {

        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setName("Human Resources");
        MvcResult result = mockMvc.perform(post("/api/v1/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentDto)))
                .andExpect(status().isCreated())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response content: " + responseContent);

    }

    @Test
    public void whenSearchDepartment_thenReturnPageOfDepartments() throws Exception {
        DepartmentDto departmentDto1 = new DepartmentDto();
        departmentDto1.setName("Human Resources");

        DepartmentDto departmentDto2 = new DepartmentDto();
        departmentDto2.setName("Finance");
        mockMvc.perform(get("/api/v1/department/search")
                        .param("name", "Human")
                        .param("number", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void whenGetAllDepartments_thenReturnPageOfDepartments() throws Exception {

        DepartmentDto departmentDto1 = new DepartmentDto();
        departmentDto1.setName("Human Resources");

        DepartmentDto departmentDto2 = new DepartmentDto();
        departmentDto2.setName("Finance");
        Page<DepartmentDto> page = new PageImpl<>(Arrays.asList(departmentDto1, departmentDto2));
        when(departmentService.getAllDepartment(0, 10)).thenReturn(page);
        mockMvc.perform(get("/api/v1/department/get-all-department")
                        .param("number", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetDepartmentById_thenReturnDepartment() throws Exception {

        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setId(1L);
        departmentDto.setName("Finance");
        when(departmentService.getDepartmentById(1L)).thenReturn(departmentDto);
        MvcResult result = mockMvc.perform(get("/api/v1/department/get_department_by_id/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    public void whenUpdateDepartment_thenReturnSuccessMessage() throws Exception {

        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setName("Updated Department");
        String successMessage = "Department updated successfully!";

        when(departmentService.updateDepartment(any(DepartmentDto.class))).thenReturn(successMessage);
        mockMvc.perform(post("/api/v1/department/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(departmentDto))
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

    @Test
    public void whenDeleteDepartment_thenReturnSuccessMessage() throws Exception {

        doNothing().when(departmentService).deleteDepartment(1L);
        mockMvc.perform(get("/api/v1/department/delete/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}


