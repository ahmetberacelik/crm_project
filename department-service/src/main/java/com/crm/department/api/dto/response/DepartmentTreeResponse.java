package com.crm.department.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentTreeResponse {
    private Long departmentId;
    private String name;
    private List<DepartmentTreeResponse> children = new ArrayList<>();
}
