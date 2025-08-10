package com.sahibinden.codecase.service;

import com.sahibinden.codecase.dto.StatusLogDto;
import com.sahibinden.codecase.entity.Classified;
import com.sahibinden.codecase.entity.StatusLog;
import com.sahibinden.codecase.repository.StatusLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusLogService {

    private final StatusLogRepository statusLogRepository;

    public void saveStatusChange(StatusLog statusLog) {
        statusLogRepository.save(statusLog);
    }

    public void saveStatusChange(Classified classified) {
        StatusLog statusLog = StatusLog.builder()
                .classified(classified)
                .status(classified.getStatus())
                .build();
        saveStatusChange(statusLog);
    }

    public List<StatusLogDto> getStatusHistory(Long classifiedId) {
        return statusLogRepository.findByClassifiedIdOrderByCreatedAtAsc(classifiedId)
                .stream()
                .map(sc -> new StatusLogDto(sc.getStatus(), sc.getCreatedAt()))
                .collect(Collectors.toList());
    }

}
