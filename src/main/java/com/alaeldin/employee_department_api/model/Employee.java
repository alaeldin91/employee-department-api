package com.alaeldin.employee_department_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Employee
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "email", nullable = false,unique = true)
    private String email;
    @Column(name = "contact_number")
    private String contactNumber;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;


}
