package com.edrr.edrr_backend.services;

import com.edrr.edrr_backend.model.DamageReport;
import com.edrr.edrr_backend.repository.DamageReportRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DamageReportService {
    public DamageReportService(DamageReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    private final DamageReportRepository reportRepository;

    // ── CREATE NEW DAMAGE REPORT ─────────────────────────────────────────
    public DamageReport createReport(DamageReport report) {

        // Auto-generate report number: RPT-2024-0001
        String reportNumber = generateReportNumber();
        report.setReportNumber(reportNumber);

        report.setStatus("PENDING");
        report.setRecoveryProgress(0);
        report.setReportedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    // ── GET ALL REPORTS ──────────────────────────────────────────────────
    public List<DamageReport> getAllReports() {
        return reportRepository.findAll();
    }

    // ── GET REPORT BY ID ─────────────────────────────────────────────────
    public DamageReport getReportById(String id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found: " + id));
    }

    // ── GET REPORTS BY STATUS ────────────────────────────────────────────
    public List<DamageReport> getReportsByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    // ── GET REPORTS BY OFFICER ───────────────────────────────────────────
    public List<DamageReport> getReportsByOfficer(String officerId) {
        return reportRepository.findByReportedBy(officerId);
    }

    // ── UPDATE REPORT STATUS ─────────────────────────────────────────────
    public DamageReport updateStatus(String id, String newStatus, String adminNotes) {
        DamageReport report = getReportById(id);
        report.setStatus(newStatus);

        if (adminNotes != null) {
            report.setAdminNotes(adminNotes);
        }

        // If resolved, set the resolved timestamp
        if ("RESOLVED".equals(newStatus)) {
            report.setResolvedAt(LocalDateTime.now());
        }

        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    // ── UPDATE RECOVERY PROGRESS ─────────────────────────────────────────
    public DamageReport updateProgress(String id, int progressPercent) {
        DamageReport report = getReportById(id);
        report.setRecoveryProgress(progressPercent);

        // Auto-resolve if 100% complete
        if (progressPercent >= 100) {
            report.setStatus("RESOLVED");
            report.setResolvedAt(LocalDateTime.now());
        } else if (progressPercent > 0) {
            report.setStatus("IN_PROGRESS");
        }

        report.setUpdatedAt(LocalDateTime.now());
        return reportRepository.save(report);
    }

    // ── GET DASHBOARD STATISTICS ─────────────────────────────────────────
    public java.util.Map<String, Long> getDashboardStats() {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("total", reportRepository.count());
        stats.put("pending", reportRepository.countByStatus("PENDING"));
        stats.put("inProgress", reportRepository.countByStatus("IN_PROGRESS"));
        stats.put("resolved", reportRepository.countByStatus("RESOLVED"));
        stats.put("rejected", reportRepository.countByStatus("REJECTED"));
        return stats;
    }

    // ── DELETE REPORT ────────────────────────────────────────────────────
    public void deleteReport(String id) {
        if (!reportRepository.existsById(id)) {
            throw new RuntimeException("Report not found: " + id);
        }
        reportRepository.deleteById(id);
    }

    // ── PRIVATE HELPER: Generate Report Number ───────────────────────────
    private String generateReportNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        long count = reportRepository.count() + 1;
        return String.format("RPT-%s-%04d", year, count);
    }
}