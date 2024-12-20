package com.example.ecommerce.controller;

import com.example.ecommerce.dto.SalesStatisticsResponse;
import com.example.ecommerce.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")  // 只有管理员可以访问统计功能
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesStatisticsResponse> getSalesStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(statisticsService.getSalesStatistics(start, end));
    }

    @GetMapping("/sales/daily")
    public ResponseEntity<SalesStatisticsResponse> getDailyStatistics() {
        return ResponseEntity.ok(statisticsService.getDailyStatistics());
    }

    @GetMapping("/sales/monthly")
    public ResponseEntity<SalesStatisticsResponse> getMonthlyStatistics() {
        return ResponseEntity.ok(statisticsService.getMonthlyStatistics());
    }

    @GetMapping("/sales/yearly")
    public ResponseEntity<SalesStatisticsResponse> getYearlyStatistics() {
        return ResponseEntity.ok(statisticsService.getYearlyStatistics());
    }
} 