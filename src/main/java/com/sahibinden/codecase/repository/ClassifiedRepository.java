package com.sahibinden.codecase.repository;

import com.sahibinden.codecase.entity.Classified;
import com.sahibinden.codecase.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassifiedRepository extends JpaRepository<Classified, Long> {
    Optional<Classified> findById(Long id);
    Boolean existsByDuplicateKey(String duplicateKey);
    Long countByStatus(Status status);
}
