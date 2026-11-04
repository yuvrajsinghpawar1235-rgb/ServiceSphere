package com.servicesphere.controller;

import com.servicesphere.repository.AssetRepository;
import com.servicesphere.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AssetRepository assetRepository;

    @GetMapping("/tickets/summary")
    public TicketSummaryRecord getTicketSummary() {
        long totalTickets = ticketRepository.count();
        long openTickets = ticketRepository.countByStatus("OPEN");
        long resolvedTickets = ticketRepository.countByStatus("RESOLVED");

        return new TicketSummaryRecord(totalTickets, openTickets, resolvedTickets);
    }

    @GetMapping("/sla")
    public SlaMetricsRecord getSlaMetrics() {
        int breachedCount = ticketRepository.findOverdueTickets(LocalDateTime.now()).size();
        long total = ticketRepository.count();
        double complianceRate = total > 0 ? ((double)(total - breachedCount) / total) * 100 : 100.0;

        return new SlaMetricsRecord(String.format("%.1f%%", complianceRate), breachedCount);
    }

    @GetMapping("/assets/summary")
    public AssetSummaryRecord getAssetSummary() {
        long totalAssets = assetRepository.count();
        long assignedAssets = assetRepository.findByStatus("Assigned").size();
        long availableAssets = assetRepository.findByStatus("In Stock").size();

        return new AssetSummaryRecord(totalAssets, assignedAssets, availableAssets);
    }

    @GetMapping("/agent-workload")
    public AgentWorkloadRecord[] getAgentWorkload() {
        List<Object[]> rawWorkloads = ticketRepository.getAgentWorkloadCounts();
        AgentWorkloadRecord[] records = new AgentWorkloadRecord[rawWorkloads.size()];

        for (int i = 0; i < rawWorkloads.size(); i++) {
            Object[] row = rawWorkloads.get(i);
            String agentName = row[0] != null ? row[0].toString() : "Unassigned";
            long activeCount = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            records[i] = new AgentWorkloadRecord(agentName, activeCount);
        }

        return records;
    }
}

// Dedicated records with zero angle brackets to prevent any parsing/stripping errors
record TicketSummaryRecord(long totalTickets, long openTickets, long resolvedTickets) {}
record SlaMetricsRecord(String complianceRate, int breachedCount) {}
record AssetSummaryRecord(long totalAssets, long assignedAssets, long availableAssets) {}
record AgentWorkloadRecord(String agentName, long activeTicketCount) {}