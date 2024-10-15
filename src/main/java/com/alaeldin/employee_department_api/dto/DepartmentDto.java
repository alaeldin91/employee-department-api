package com.alaeldin.employee_department_api.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentDto
{
    private long id;
    @NotBlank(message = "Name is Required")
    private String name;
}
