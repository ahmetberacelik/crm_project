package com.crm.department.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentResponse {
    private Long departmentId;
    private String name;
    private Long parentDepartmentId;
}


