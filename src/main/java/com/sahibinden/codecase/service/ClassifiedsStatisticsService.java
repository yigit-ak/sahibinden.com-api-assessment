package com.sahibinden.codecase.service;

import com.sahibinden.codecase.dto.ClassifiedStatusCounts;
import com.sahibinden.codecase.entity.Status;
import com.sahibinden.codecase.repository.ClassifiedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassifiedsStatisticsService {

    private final ClassifiedRepository classifiedRepository;

    public ClassifiedStatusCounts getClassifiedsStatistics() {
        long activeClassifieds = classifiedRepository.countByStatus(Status.ACTIVE);
        long inactiveClassifieds = classifiedRepository.countByStatus(Status.DEACTIVATED);
        long duplicateClassifieds = classifiedRepository.countByStatus(Status.DUPLICATE);
        long pendingClassifieds = classifiedRepository.countByStatus(Status.PENDING_APPROVAL);

        return ClassifiedStatusCounts.builder()
                .active(activeClassifieds)
                .deactivated(inactiveClassifieds)
                .duplicate(duplicateClassifieds)
                .pending(pendingClassifieds)
                .build();
    }
}
