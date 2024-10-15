package com.alaeldin.employee_department_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Department
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name",nullable = false)
    private String name;

}
