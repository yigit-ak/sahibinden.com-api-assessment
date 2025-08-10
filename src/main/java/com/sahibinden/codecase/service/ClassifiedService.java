package com.sahibinden.codecase.service;

import com.sahibinden.codecase.domain.StatusMachine;
import com.sahibinden.codecase.entity.Classified;
import com.sahibinden.codecase.entity.Status;
import com.sahibinden.codecase.repository.ClassifiedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassifiedService {

    private final ClassifiedRepository classifiedRepository;

    public Classified createClassified(Classified classified) {
        if (classifiedRepository.existsByDuplicateKey(classified.getDuplicateKey()))
            classified.setStatus(Status.DUPLICATE);

        return classifiedRepository.save(classified);
    }

    public Classified getClassifiedById(Long id) {
        return classifiedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Classified not found with id: " + id));
    }

    public void updateClassifiedStatus(Long id, Status newStatus) {
        Classified classified = getClassifiedById(id);
        StatusMachine.assertTransition(classified.getStatus(), newStatus);
        classified.setStatus(newStatus);
        classifiedRepository.save(classified);
    }
}
