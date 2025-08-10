package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.dto.ClassifiedDto;
import com.sahibinden.codecase.dto.NewClassifiedDto;
import com.sahibinden.codecase.dto.StatusLogDto;
import com.sahibinden.codecase.dto.StatusUpdateDto;
import com.sahibinden.codecase.entity.Classified;
import com.sahibinden.codecase.entity.StatusLog;
import com.sahibinden.codecase.mapper.ClassifiedMapper;
import com.sahibinden.codecase.service.ClassifiedService;
import com.sahibinden.codecase.service.StatusLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/classifieds")
@RequiredArgsConstructor
public class ClassifiedController {

    private final ClassifiedService classifiedService;
    private final StatusLogService statusLogService;
    private final ClassifiedMapper classifiedMapper;

    @PostMapping
    public ResponseEntity<?> createClassified(@Valid @RequestBody NewClassifiedDto dto) {
        Classified saved = classifiedService.createClassified(classifiedMapper.toEntity(dto));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassifiedDto> getClassified(@PathVariable Long id) {
        ClassifiedDto dto = classifiedMapper.toDto(classifiedService.getClassifiedById(id));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateClassifiedStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDto dto) {
        classifiedService.updateClassifiedStatus(id, dto.getStatus());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<StatusLogDto>> getStatusHistory(@PathVariable Long id) {
        List<StatusLogDto> history = statusLogService.getStatusHistory(id);
        return ResponseEntity.ok(history);
    }

}
