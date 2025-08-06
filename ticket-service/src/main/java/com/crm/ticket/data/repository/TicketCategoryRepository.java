package com.crm.ticket.data.repository;

import com.crm.ticket.data.entity.TicketCategory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TicketCategoryRepository extends ReactiveCrudRepository<TicketCategory, Long> {

    /**
     * Finds an active category by its unique key.
     * This will be the main method for routing tickets.
     * @param categoryKey The unique key sent from the API (e.g., "BILLING").
     * @return A Mono emitting the found category, or empty if not found.
     */
    Mono<TicketCategory> findByCategoryKeyAndIsActive(String categoryKey, boolean isActive);

    /**
     * Finds all categories by their active status.
     * Used to populate the category dropdown in the UI.
     * @param isActive The active status to filter by.
     * @return A Flux emitting all matching categories.
     */
    Flux<TicketCategory> findAllByIsActive(boolean isActive);
}