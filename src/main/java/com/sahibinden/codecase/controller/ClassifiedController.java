package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.dto.ClassifiedDto;
import com.sahibinden.codecase.dto.NewClassifiedDto;
import com.sahibinden.codecase.dto.StatusLogDto;
import com.sahibinden.codecase.dto.StatusUpdateDto;
import com.sahibinden.codecase.entity.Classified;
import com.sahibinden.codecase.mapper.ClassifiedMapper;
import com.sahibinden.codecase.service.ClassifiedService;
import com.sahibinden.codecase.service.StatusLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Tag(name = "Classifieds", description = "Create, read and update classified ads")
@RestController
@RequestMapping("/api/v1/classifieds")
@RequiredArgsConstructor
public class ClassifiedController {

    private final ClassifiedService classifiedService;
    private final StatusLogService statusLogService;
    private final ClassifiedMapper classifiedMapper;

    @Operation(
            summary = "Create a classified",
            description = "Returns 201 Created with the Location header of the new resource.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New classified ad details",
                    content = @Content(schema = @Schema(implementation = NewClassifiedDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
            }
    )
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

    @Operation(
            summary = "Get a classified by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = ClassifiedDto.class))),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ClassifiedDto> getClassified(@PathVariable Long id) {
        ClassifiedDto dto = classifiedMapper.toDto(classifiedService.getClassifiedById(id));
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Update classified status",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New classified status",
                    content = @Content(schema = @Schema(implementation = StatusUpdateDto.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated"),
                    @ApiResponse(responseCode = "400", description = "Bad transition or validation error", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateClassifiedStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDto dto) {
        classifiedService.updateClassifiedStatus(id, dto.getStatus());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get status change history",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = StatusLogDto.class))),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
            }
    )
    @GetMapping("/{id}/history")
    public ResponseEntity<List<StatusLogDto>> getStatusHistory(@PathVariable Long id) {
        List<StatusLogDto> history = statusLogService.getStatusHistory(id);
        return ResponseEntity.ok(history);
    }

}
