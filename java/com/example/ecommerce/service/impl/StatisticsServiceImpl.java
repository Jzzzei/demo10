package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.SalesStatisticsResponse;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.OrderStatus;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.StatisticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {
    private final OrderRepository orderRepository;

    public StatisticsServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public SalesStatisticsResponse getSalesStatistics(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);
        return calculateStatistics(orders);
    }

    @Override
    public SalesStatisticsResponse getDailyStatistics() {
        LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusDays(1);
        return getSalesStatistics(start, end);
    }

    @Override
    public SalesStatisticsResponse getMonthlyStatistics() {
        LocalDateTime start = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusMonths(1);
        return getSalesStatistics(start, end);
    }

    @Override
    public SalesStatisticsResponse getYearlyStatistics() {
        LocalDateTime start = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = start.plusYears(1);
        return getSalesStatistics(start, end);
    }

    private SalesStatisticsResponse calculateStatistics(List<Order> orders) {
        SalesStatisticsResponse response = new SalesStatisticsResponse();

        // 计算基本统计数据
        BigDecimal totalRevenue = orders.stream()
            .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = (int) orders.stream()
            .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
            .count();

        Set<Long> uniqueProducts = orders.stream()
            .flatMap(order -> order.getItems().stream())
            .map(item -> item.getProduct().getId())
            .collect(Collectors.toSet());

        Double averageOrderValue = totalOrders > 0 
            ? totalRevenue.doubleValue() / totalOrders 
            : 0.0;

        response.setTotalRevenue(totalRevenue);
        response.setTotalOrders(totalOrders);
        response.setTotalProducts(uniqueProducts.size());
        response.setAverageOrderValue(averageOrderValue);

        // 计算畅销产品
        Map<Long, SalesStatisticsResponse.TopProductDTO> productStats = new HashMap<>();
        orders.stream()
            .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
            .flatMap(order -> order.getItems().stream())
            .forEach(item -> {
                Long productId = item.getProduct().getId();
                productStats.computeIfAbsent(productId, k -> {
                    SalesStatisticsResponse.TopProductDTO dto = new SalesStatisticsResponse.TopProductDTO();
                    dto.setProductId(productId);
                    dto.setProductName(item.getProduct().getName());
                    dto.setQuantitySold(0);
                    dto.setRevenue(BigDecimal.ZERO);
                    return dto;
                });
                
                SalesStatisticsResponse.TopProductDTO stats = productStats.get(productId);
                stats.setQuantitySold(stats.getQuantitySold() + item.getQuantity());
                stats.setRevenue(stats.getRevenue().add(
                    item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ));
            });

        List<SalesStatisticsResponse.TopProductDTO> topProducts = productStats.values().stream()
            .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue()))
            .limit(10)
            .collect(Collectors.toList());
        response.setTopProducts(topProducts);

        // 按类别统计收入
        Map<String, BigDecimal> revenueByCategory = orders.stream()
            .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
            .flatMap(order -> order.getItems().stream())
            .collect(Collectors.groupingBy(
                item -> item.getProduct().getCategory(),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                    BigDecimal::add
                )
            ));
        response.setRevenueByCategory(revenueByCategory);

        // 按订单状态统计
        Map<String, Integer> ordersByStatus = orders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getStatus().name(),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));
        response.setOrdersByStatus(ordersByStatus);

        return response;
    }
} 