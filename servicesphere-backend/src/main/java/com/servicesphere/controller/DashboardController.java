package com.servicesphere.controller;

import com.servicesphere.repository.AssetRepository;
import com.servicesphere.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AssetRepository assetRepository;

    @GetMapping("/tickets")
    public ResponseEntity<?> getTicketDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("openTicketsCount", ticketRepository.countByStatus("OPEN"));
        metrics.put("slaBreachesCount", ticketRepository.findOverdueTickets(LocalDateTime.now()).size());
        metrics.put("unassignedTicketsCount", ticketRepository.findUnassignedTickets().size());
        metrics.put("statusBreakdown", ticketRepository.getStatusBreakdown());
        metrics.put("priorityBreakdown", ticketRepository.getPriorityBreakdown());
        metrics.put("agentWorkload", ticketRepository.getAgentWorkloadCounts());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/assets")
    public ResponseEntity<?> getAssetDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalAssets", assetRepository.count());
        metrics.put("inStockCount", assetRepository.findByStatus("In Stock").size());
        metrics.put("assignedCount", assetRepository.findByStatus("Assigned").size());
        metrics.put("inRepairCount", assetRepository.findByStatus("In Repair").size());
        metrics.put("retiredCount", assetRepository.findByStatus("Retired").size());
        metrics.put("expiringWarrantiesCount", assetRepository.findAssetsWithExpiringWarranty(LocalDate.now().plusDays(30)).size());
        return ResponseEntity.ok(metrics);
    }
}