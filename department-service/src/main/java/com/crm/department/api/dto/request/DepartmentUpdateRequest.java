package com.crm.department.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentUpdateRequest {

    @NotBlank(message = "Department name cannot be blank")
    private String name;

    private Long parentDepartmentId;
}
