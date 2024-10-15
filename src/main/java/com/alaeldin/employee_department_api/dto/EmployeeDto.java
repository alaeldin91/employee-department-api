package com.alaeldin.employee_department_api.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmployeeDto
{

    private long id;
    @NotBlank(message = "First Name Is Required")
    private String firstName;
    @NotBlank(message = "Last Name Is Required")
    private String lastName;
    @NotBlank(message = "Email Is Required")
    @Email(message = "Email should be valid")
    private String email;
    private String contactNumber;
    private long departmentId;

}
