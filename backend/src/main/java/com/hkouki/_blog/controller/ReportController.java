package com.hkouki._blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hkouki._blog.service.ReportService;
import com.hkouki._blog.dto.ApiResponse;
import com.hkouki._blog.dto.ReportRequest;
import com.hkouki._blog.dto.ReportResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // this for get one report by id
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ReportResponse>> getReportById(@PathVariable Long reportId) {
        ReportResponse report = reportService.getReportById(reportId);
        return ResponseEntity.ok(new ApiResponse<>("success", report, "Fetched report successfully"));
    }

    // this for get all reports
    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<ReportResponse[]>> getAllReports() {
        ReportResponse[] allReports = reportService.getAllReports();
        return ResponseEntity.ok(new ApiResponse<>("success", allReports, "Fetched reports feed successfully"));
    }

    // Controller methods for handling reports will go here
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(@Valid @RequestBody ReportRequest request) {
        ReportResponse caseReport = reportService.createReport(request);
        return ResponseEntity.ok(new ApiResponse<>("success", caseReport, "Report created successfully"));
    }

    @PutMapping("/handle/{reportId}")
    public ResponseEntity<ApiResponse<String>> handleReport(@PathVariable Long reportId) {
        reportService.handleReport(reportId);
        return ResponseEntity.ok(new ApiResponse<>("success", null, "Report handled successfully"));
    }
}
