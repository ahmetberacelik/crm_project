package com.crm.ticket.data.repository;

import com.crm.ticket.data.entity.Pool;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoolRepository extends ReactiveCrudRepository<Pool, Long> {
} 