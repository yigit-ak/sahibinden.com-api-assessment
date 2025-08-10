package com.sahibinden.codecase.repository;

import com.sahibinden.codecase.entity.StatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusLogRepository extends JpaRepository<StatusLog, Long> {
    List<StatusLog> findByClassifiedIdOrderByCreatedAtAsc(long classifiedId);
}
