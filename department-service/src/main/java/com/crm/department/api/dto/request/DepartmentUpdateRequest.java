package com.crm.department.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentUpdateRequest {
    @NotBlank
    private String name;

    private Long parentDepartmentId; // nullable
}


