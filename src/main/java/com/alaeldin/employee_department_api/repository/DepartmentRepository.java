package com.alaeldin.employee_department_api.repository;

import com.alaeldin.employee_department_api.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long>
{
    Page<Department> findByNameContainingIgnoreCase(String name,Pageable pageable);
    Optional<Department> findByName(String name);
}
