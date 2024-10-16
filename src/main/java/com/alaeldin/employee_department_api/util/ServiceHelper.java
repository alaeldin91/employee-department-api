package com.alaeldin.employee_department_api.util;

import com.alaeldin.employee_department_api.dto.DepartmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;

public class ServiceHelper
{
    private final PagedResourcesAssembler<DepartmentDto> pagedResourcesAssembler;

    public ServiceHelper(PagedResourcesAssembler<DepartmentDto> pagedResourcesAssembler) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    public PagedModel<EntityModel<DepartmentDto>> convertToPagedModel(List<DepartmentDto> departmentDtos,
                                                                      int pageNumber,
                                                                      int pageSize,
                                                                      long totalElements) {
        // Create a Page object
        Page<DepartmentDto> departmentPage = new PageImpl<>(departmentDtos, PageRequest.of(pageNumber, pageSize), totalElements);

        // Use the PagedResourcesAssembler to convert the Page to PagedModel
        return pagedResourcesAssembler.toModel(departmentPage, EntityModel::of);
    }


}
