package com.crm.ticket.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private Long id;
    private String key;
    private String displayName;
    private Long targetDepartmentId;
    private Boolean isActive;
}
