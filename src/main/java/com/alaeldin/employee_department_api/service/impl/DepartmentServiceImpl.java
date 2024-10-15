package com.alaeldin.employee_department_api.service.impl;

import com.alaeldin.employee_department_api.dto.DepartmentDto;
import com.alaeldin.employee_department_api.exception.DataAlreadyExist;
import com.alaeldin.employee_department_api.exception.DataSaveException;
import com.alaeldin.employee_department_api.exception.ResourceNotFoundException;
import com.alaeldin.employee_department_api.mapper.MapperDepartment;
import com.alaeldin.employee_department_api.model.Department;
import com.alaeldin.employee_department_api.repository.DepartmentRepository;
import com.alaeldin.employee_department_api.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService
{
    private final DepartmentRepository departmentRepository;
    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Override
    public String saveDepartment(DepartmentDto departmentDto) {

        Optional<Department> existDepartment
                = departmentRepository
                .findByName(departmentDto.getName());
        if (existDepartment.isPresent()) {

            String message = "Department Already is found";
            throw new DataAlreadyExist(message);
        }

        try
        {
            Department department = MapperDepartment.mapToDepartment(departmentDto);
            departmentRepository.save(department);

            return "Department saved successfully";
        }
        catch (DataIntegrityViolationException e)
        {
            logger.error("Data integrity violation while saving department: "
                    + e.getMessage(), e);
            throw new DataSaveException("Department data is invalid: "
                    + e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("Failed to save department: " + e.getMessage(), e);
            throw new DataSaveException("Failed to save department: "
                    + e.getMessage());
        }
    }
    @Override
    public Page<DepartmentDto> findByNameContainingIgnoreCase(String name
                                                              , int number
                                                              , int size) {
        Pageable pageable = PageRequest.of(number,size);
       Page<Department> departments = departmentRepository
               .findByNameContainingIgnoreCase(name,pageable);
        System.out.println("Departments found: " + departments.getContent());

        return departments.map(MapperDepartment::mapToDepartmentDto);
    }

    @Override
    public Page<DepartmentDto> getAllDepartment(int number, int size) {

        Pageable pageable = PageRequest.of(number,size);
        Page<Department> departments = departmentRepository
                .findAll(pageable);

        return departments.map(MapperDepartment::mapToDepartmentDto);
    }
    @Override
    public DepartmentDto getDepartmentById(long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Department"
                        ,"id"
                        ,id));

        return MapperDepartment.mapToDepartmentDto(department);
    }
    @Override
    public void deleteDepartment(long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Department","id",id));
        departmentRepository.delete(department);
    }
    @Override
    public String updateDepartment(DepartmentDto departmentDto) {

        Department departmentExist =  departmentRepository.
                findById(departmentDto.getId())
                .orElseThrow(()-> new ResourceNotFoundException("Department"
                        ,"id",departmentDto.getId()));
        departmentExist.setName(departmentDto.getName());
        departmentRepository.save(departmentExist);

        return "Department updated successfully!";

    }
}
