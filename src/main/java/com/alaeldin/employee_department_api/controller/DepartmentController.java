package com.alaeldin.employee_department_api.controller;

import com.alaeldin.employee_department_api.dto.DepartmentDto;
import com.alaeldin.employee_department_api.dto.EmployeeDto;
import com.alaeldin.employee_department_api.service.impl.DepartmentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api/v1/department")
@RequiredArgsConstructor
public class DepartmentController
{
  private final DepartmentServiceImpl departmentService;
    private final PagedResourcesAssembler<DepartmentDto> pagedResourcesAssembler;

    @PostMapping()
    public ResponseEntity<String> saveDepartment(@Valid @RequestBody DepartmentDto
                                                           departmentDto)
  {
       return new ResponseEntity<>(departmentService.saveDepartment(departmentDto),
               HttpStatus.CREATED);
  }

  @GetMapping(value = "/search" ,produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PagedModel<EntityModel<DepartmentDto>>>searchDepartment(@RequestParam String name
                                                               ,@RequestParam(defaultValue = "0")
                                                                int number
                                                               ,@RequestParam(defaultValue = "10")
                                                                int size)
  {

      Page<DepartmentDto> departmentPage=departmentService.findByNameContainingIgnoreCase(name
              ,number,size);
      PagedModel<EntityModel<DepartmentDto>> pagedModel =  pagedResourcesAssembler
                          .toModel(departmentPage, EntityModel::of);

      return new ResponseEntity<>(pagedModel,HttpStatus.OK);
  }

  @GetMapping("/get-all-department")
  public ResponseEntity<PagedModel<EntityModel<DepartmentDto>>> getAllDepartments(@RequestParam(defaultValue = "0")
                                                                   int number
                                                                   ,@RequestParam(defaultValue = "10")
                                                                    int size)
  {

      Page<DepartmentDto> departmentPage=departmentService.getAllDepartment(
              number,size);
      PagedModel<EntityModel<DepartmentDto>> pagedModel =  pagedResourcesAssembler
              .toModel(departmentPage, EntityModel::of);

      return new ResponseEntity<>(pagedModel,HttpStatus.OK);
  }
  @GetMapping("/get_department_by_id/{id}")
    public ResponseEntity<DepartmentDto> getAllDepartmentById(@PathVariable("id") long id){

        return new ResponseEntity<>(departmentService.getDepartmentById(id),HttpStatus.OK);
    }
    @PostMapping("update/{id}")
    public ResponseEntity<String> updateDepartment(@RequestBody DepartmentDto departmentDto
            ,@PathVariable("id") long id){

      departmentDto.setId(id);

        return new ResponseEntity<>(departmentService
                .updateDepartment(departmentDto),HttpStatus.OK);
    }
    @GetMapping("delete/{id}")
    public ResponseEntity<Map<String, String>> deleteDepartment(@PathVariable("id") long id) {

        departmentService.deleteDepartment(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Delete Department Successfully");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
