package com.alaeldin.employee_department_api.repository;

import com.alaeldin.employee_department_api.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee,Long>
{
        Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                String firstName, String lastName, String email, Pageable pageable
        );
        Optional<Employee> findByEmail(String email);
}
