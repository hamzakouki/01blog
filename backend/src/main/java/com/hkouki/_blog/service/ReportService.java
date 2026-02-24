package com.hkouki._blog.service;

import com.hkouki._blog.dto.ReportRequest;
import com.hkouki._blog.dto.ReportResponse;
import com.hkouki._blog.entity.Report;
import com.hkouki._blog.entity.User;
import com.hkouki._blog.exception.ResourceNotFoundException;
import com.hkouki._blog.repository.PostRepository;
import com.hkouki._blog.repository.ReportRepository;
import com.hkouki._blog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hkouki._blog.entity.Post;

@Service
public class ReportService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final PostRepository postRepository;

    public ReportService(
            UserService userService,
            UserRepository userRepository,
            ReportRepository reportRepository,
            PostRepository postRepository) { // add this
        this.userService = userService;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.postRepository = postRepository;
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

        boolean hasUser = request.getReportedUserId() != null;
        boolean hasPost = request.getPostId() != null;

        if (hasUser == hasPost) {
            throw new IllegalArgumentException(
                    "Report must target either a user or a post");
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setReason(request.getReason());
        report.setHandled(false);

        // ===== USER REPORT =====
        if (hasUser) {
            User reportedUser = userRepository.findById(request.getReportedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (reporter.getId().equals(reportedUser.getId())) {
                throw new IllegalArgumentException("You cannot report yourself");
            }

            report.setReportedUser(reportedUser);
        }

        // ===== POST REPORT =====
        if (hasPost) {
            // You will add PostRepository later
            Post post = postRepository.findById(request.getPostId())
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
            report.setReportedPost(post);
        }

        Report savedReport = reportRepository.save(report);
        return converter(savedReport);
    }

    // this for get one report by id

    public ReportResponse getReportById(Long reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Report ID is required");
        }
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        return converter(report);
    }

    // Converter method to convert Report entity to ReportResponse DTO
    private ReportResponse converter(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .reporterId(report.getReporter().getId())
                .reporterUsername(report.getReporter().getUsername())
                .reportedUserId(report.getReportedUser() != null ? report.getReportedUser().getId() : null)
                .reportedUsername(report.getReportedUser() != null ? report.getReportedUser().getUsername() : null)
                .reason(report.getReason())
                .handled(report.isHandled())
                .createdAt(report.getCreatedAt())
                .build();
    }

}
