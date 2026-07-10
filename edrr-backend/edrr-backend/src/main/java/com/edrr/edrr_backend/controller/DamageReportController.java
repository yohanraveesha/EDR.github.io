package com.edrr.edrr_backend.controller;

import com.edrr.edrr_backend.model.DamageReport;
import com.edrr.edrr_backend.services.DamageReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class DamageReportController {
    public DamageReportController(DamageReportService reportService) {
        this.reportService = reportService;
    }


    private final DamageReportService reportService;

    // ── CREATE REPORT ─────────────────────────────────────────────────────
    // POST http://localhost:8080/api/reports
    @PostMapping
    public ResponseEntity<?> createReport(@Valid @RequestBody DamageReport report) {
        try {
            DamageReport saved = reportService.createReport(report);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── GET ALL REPORTS ───────────────────────────────────────────────────
    // GET http://localhost:8080/api/reports
    @GetMapping
    public ResponseEntity<List<DamageReport>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    // ── GET REPORT BY ID ──────────────────────────────────────────────────
    // GET http://localhost:8080/api/reports/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(reportService.getReportById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── GET REPORTS BY STATUS ─────────────────────────────────────────────
    // GET http://localhost:8080/api/reports/status/PENDING
    @GetMapping("/status/{status}")
    public ResponseEntity<List<DamageReport>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(reportService.getReportsByStatus(status.toUpperCase()));
    }

    // ── GET REPORTS BY OFFICER ────────────────────────────────────────────
    // GET http://localhost:8080/api/reports/officer/{officerId}
    @GetMapping("/officer/{officerId}")
    public ResponseEntity<List<DamageReport>> getByOfficer(@PathVariable String officerId) {
        return ResponseEntity.ok(reportService.getReportsByOfficer(officerId));
    }

    // ── UPDATE STATUS ─────────────────────────────────────────────────────
    // PUT http://localhost:8080/api/reports/{id}/status
    // Body: { "status": "IN_PROGRESS", "adminNotes": "Team dispatched" }
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id,
                                          @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            String notes = body.get("adminNotes");
            return ResponseEntity.ok(reportService.updateStatus(id, status, notes));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── UPDATE RECOVERY PROGRESS ──────────────────────────────────────────
    // PUT http://localhost:8080/api/reports/{id}/progress
    // Body: { "progress": 75 }
    @PutMapping("/{id}/progress")
    public ResponseEntity<?> updateProgress(@PathVariable String id,
                                            @RequestBody Map<String, Integer> body) {
        try {
            int progress = body.get("progress");
            return ResponseEntity.ok(reportService.updateProgress(id, progress));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── DASHBOARD STATS ───────────────────────────────────────────────────
    // GET http://localhost:8080/api/reports/stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(reportService.getDashboardStats());
    }

    // ── DELETE REPORT ─────────────────────────────────────────────────────
    // DELETE http://localhost:8080/api/reports/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable String id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.ok(Map.of("message", "Report deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}