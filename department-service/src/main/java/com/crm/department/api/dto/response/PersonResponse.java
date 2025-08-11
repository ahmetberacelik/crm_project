package com.crm.department.api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonResponse {
    private Long personId;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private Long departmentId;
}


