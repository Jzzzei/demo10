package com.example.ecommerce.service;

import com.example.ecommerce.dto.SalesStatisticsResponse;
import java.time.LocalDateTime;

public interface StatisticsService {
    SalesStatisticsResponse getSalesStatistics(LocalDateTime start, LocalDateTime end);
    SalesStatisticsResponse getDailyStatistics();
    SalesStatisticsResponse getMonthlyStatistics();
    SalesStatisticsResponse getYearlyStatistics();
} 