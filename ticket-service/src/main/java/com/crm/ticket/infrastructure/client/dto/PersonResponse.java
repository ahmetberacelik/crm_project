package com.crm.ticket.infrastructure.client.dto;

import lombok.Data;

@Data
public class PersonResponse {
    private Long personId;
    private String name;
    private String surname;
    private String email;
}
