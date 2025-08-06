package com.crm.ticket.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String categoryKey;

    @NotBlank
    @Size(min = 3, max = 255)
    private String displayName;

    @NotNull
    private Long targetDepartmentId;
}
