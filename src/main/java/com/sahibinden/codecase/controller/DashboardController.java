package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.dto.ClassifiedStatusCounts;
import com.sahibinden.codecase.service.ClassifiedsStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "Aggregated statistics")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ClassifiedsStatisticsService classifiedsStatisticsService;

    @Operation(
            summary = "Get counts per classified status",
            responses = @ApiResponse(
                    responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ClassifiedStatusCounts.class))
            )
    )
    @GetMapping("/classifieds/statistics")
    public ClassifiedStatusCounts getClassifiedsStatistics() {
        return classifiedsStatisticsService.getClassifiedsStatistics();
    }
}
