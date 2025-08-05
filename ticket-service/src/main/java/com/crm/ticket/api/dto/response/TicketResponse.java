package com.crm.ticket.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TicketResponse {
    private Long id;
    private String issue;
    private String priority;
    private Boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime closedDate;
    private CreatorInfo creator;
    private List<AssignmentInfo> assignments;

    @Data
    @Builder
    public static class CreatorInfo {
        private Long id;
        private String name;
        private String surname;
        private String email;
        private String type; // "EMPLOYEE" or "CUSTOMER"
    }

    @Data
    @Builder
    public static class AssignmentInfo {
        private Long assignmentId;
        private String status;
        private LocalDateTime assignedDate;
        private LocalDateTime completedDate;
        private AssigneeInfo assignee;
    }

    @Data
    @Builder
    public static class AssigneeInfo {
        private Long id;
        private String name; // Department or Person name
        private String type; // "DEPARTMENT_POOL" or "PERSON"
    }
}
