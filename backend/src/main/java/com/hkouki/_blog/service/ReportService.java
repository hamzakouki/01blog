package com.hkouki._blog.service;

import com.hkouki._blog.dto.ReportRequest;
import com.hkouki._blog.dto.ReportResponse;
import com.hkouki._blog.entity.Report;
import com.hkouki._blog.entity.User;
import com.hkouki._blog.exception.ResourceNotFoundException;
import com.hkouki._blog.repository.ReportRepository;
import com.hkouki._blog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public ReportService(
            UserService userService,
            UserRepository userRepository,
            ReportRepository reportRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }

    public ReportResponse[] getAllReports() {
        return reportRepository.findAll().stream()
                .filter(report -> !report.isHandled())
                .map(report -> converter(report)).toArray(ReportResponse[]::new);
    }


    @Transactional
    public void handleReport(Long reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Report ID is required");
        }
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        report.setHandled(true);
    }

    @Transactional
    public ReportResponse createReport(ReportRequest request) {

        User reporter = userService.getCurrentUser();

        Long reportedUserId = request.getReportedUserId();

        if (reportedUserId == null) {
            throw new IllegalArgumentException("Reported user ID is required");
        }

        User reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (reporter.getId().equals(reportedUser.getId())) {
            throw new IllegalArgumentException("You cannot report yourself");
        }

        Report report = Report.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reason(request.getReason())
                .handled(false)
                .build();
                
                if (report == null) {
                    throw new IllegalStateException("Failed to create report");
                }

        Report savedReport = reportRepository.save(report);

        return converter(savedReport);
    }

    public ReportResponse getReportById(Long reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Report ID is required");
        }
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        return converter(report);
    }

    private ReportResponse converter(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .reporterId(report.getReporter().getId())
                .reporterUsername(report.getReporter().getUsername())
                .reportedUserId(report.getReportedUser().getId())
                .reportedUsername(report.getReportedUser().getUsername())
                .reason(report.getReason())
                .handled(report.isHandled())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
