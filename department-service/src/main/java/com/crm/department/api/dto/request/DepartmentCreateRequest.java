package com.crm.department.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DepartmentCreateRequest {
    @NotBlank
    private String name;

    private Long parentDepartmentId; // nullable (root)
}


