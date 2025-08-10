package com.sahibinden.codecase.controller;

import com.sahibinden.codecase.dto.ClassifiedStatusCounts;
import com.sahibinden.codecase.service.ClassifiedsStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ClassifiedsStatisticsService classifiedsStatisticsService;

    @GetMapping("/classifieds/statistics")
    public ClassifiedStatusCounts getClassifiedsStatistics() {
        return classifiedsStatisticsService.getClassifiedsStatistics();
    }
}
