package com.concertbooking.controller;

import com.concertbooking.dto.response.AnalyticsDashboardResponse;
import com.concertbooking.dto.response.ApiResponse;
import com.concertbooking.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Endpoints for system-wide dashboard metrics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    @Operation(summary = "Get system dashboard metrics", description = "ADMIN or VIEWER roles only")
    public ResponseEntity<ApiResponse<AnalyticsDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getDashboardMetrics()));
    }
}
