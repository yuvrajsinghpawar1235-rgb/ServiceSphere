package com.servicesphere.controller;

import com.servicesphere.entity.Ticket;
import com.servicesphere.entity.TicketComment;
import com.servicesphere.entity.TicketHistory;
import com.servicesphere.repository.TicketCommentRepository;
import com.servicesphere.repository.TicketHistoryRepository;
import com.servicesphere.repository.TicketRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "IT Incident Management", description = "Endpoints for creating, filtering, assigning, escalating, and managing IT tickets.")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketCommentRepository commentRepository;

    @Autowired
    private TicketHistoryRepository historyRepository;

    private static final Set<String> ALLOWED_CATEGORIES = Set.of(
            "Hardware", "Software", "Network", "Account/Access", "Email", "Security", "Other"
    );

    private static final Set<String> ALLOWED_PRIORITIES = Set.of(
            "Low", "Medium", "High", "Critical"
    );

    @Operation(summary = "Get filtered tickets", description = "Retrieves paginated tickets with optional role-based filtering, status, priority, category, assignee, and keyword search.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved ticket page")
    })
    @GetMapping
    public ResponseEntity<Page<Ticket>> getFilteredTickets(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Long targetRequesterId = ("EMPLOYEE".equalsIgnoreCase(role)) ? userId : null;

        Page<Ticket> tickets = ticketRepository.findFilteredTickets(
                targetRequesterId, status, priority, category, assignee, keyword, pageable
        );

        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Create a new ticket", description = "Submits a new IT incident or request. Validates priority constraints for employee roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category or priority parameter"),
            @ApiResponse(responseCode = "403", description = "Forbidden: Employees cannot create Critical priority tickets")
    })
    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Ticket ticket, @RequestParam(required = false) Long roleId) {
        if (ticket.getTicketNumber() == null || ticket.getTicketNumber().isEmpty()) {
            ticket.setTicketNumber("INC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (ticket.getStatus() == null) ticket.setStatus("OPEN");
        if (ticket.getRequesterId() == null) ticket.setRequesterId(1L);

        String category = ticket.getCategory();
        if (category == null || category.isEmpty()) {
            ticket.setCategory("Other");
        } else {
            boolean valid = false;
            for (String c : ALLOWED_CATEGORIES) {
                if (c.equalsIgnoreCase(category)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid category. Allowed: Hardware, Software, Network, Account/Access, Email, Security, Other"));
            }
        }

        String priority = ticket.getPriority();
        if (priority == null || priority.isEmpty()) {
            ticket.setPriority("Medium");
        } else {
            boolean valid = false;
            for (String p : ALLOWED_PRIORITIES) {
                if (p.equalsIgnoreCase(priority)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid priority. Allowed: Low, Medium, High, Critical"));
            }

            if ("Critical".equalsIgnoreCase(priority) && roleId != null && roleId == 1L) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access Denied: Employees cannot create Critical priority tickets."));
            }
        }

        if (ticket.getDepartment() == null || ticket.getDepartment().isEmpty()) {
            ticket.setDepartment("General IT");
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        return ResponseEntity.ok(savedTicket);
    }

    @Operation(summary = "Update ticket status", description = "Enforces valid state transition matrix for tickets.")
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTicketStatus(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) Long roleId) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
        String currentStatus = ticket.getStatus() != null ? ticket.getStatus().toUpperCase() : "OPEN";
        String targetStatus = status.toUpperCase();

        boolean isValidTransition = false;

        if (currentStatus.equals("OPEN")) {
            isValidTransition = targetStatus.equals("IN PROGRESS") || targetStatus.equals("ESCALATED") || targetStatus.equals("RESOLVED") || targetStatus.equals("CLOSED") || targetStatus.equals("ARCHIVED");
        } else if (currentStatus.equals("IN PROGRESS")) {
            isValidTransition = targetStatus.equals("WAITING FOR REQUESTER") || targetStatus.equals("ESCALATED") || targetStatus.equals("RESOLVED") || targetStatus.equals("CLOSED") || targetStatus.equals("ARCHIVED");
        } else if (currentStatus.equals("WAITING FOR REQUESTER")) {
            isValidTransition = targetStatus.equals("IN PROGRESS") || targetStatus.equals("RESOLVED") || targetStatus.equals("CLOSED") || targetStatus.equals("ARCHIVED");
        } else if (currentStatus.equals("ESCALATED")) {
            isValidTransition = targetStatus.equals("IN PROGRESS") || targetStatus.equals("RESOLVED") || targetStatus.equals("CLOSED") || targetStatus.equals("ARCHIVED");
        } else if (currentStatus.equals("RESOLVED")) {
            isValidTransition = targetStatus.equals("CLOSED") || targetStatus.equals("OPEN") || targetStatus.equals("ARCHIVED");
        } else if (currentStatus.equals("CLOSED")) {
            isValidTransition = (roleId != null && roleId >= 3) && targetStatus.equals("OPEN");
        } else {
            isValidTransition = true;
        }

        if (!isValidTransition) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid status transition from " + currentStatus + " to " + targetStatus));
        }

        ticket.setStatus(targetStatus);
        return ResponseEntity.ok(ticketRepository.save(ticket));
    }

    @Operation(summary = "Update ticket priority", description = "Updates priority with role authorization rules.")
    @PatchMapping("/{id}/priority")
    public ResponseEntity<?> updateTicketPriority(@PathVariable Long id, @RequestParam String priority, @RequestParam(required = false) Long roleId) {
        boolean valid = false;
        for (String p : ALLOWED_PRIORITIES) {
            if (p.equalsIgnoreCase(priority)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid priority value"));
        }

        if ("Critical".equalsIgnoreCase(priority) && (roleId == null || roleId == 1L)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Only Support Agents and Managers can escalate tickets to Critical."));
        }

        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setPriority(priority);
        return ResponseEntity.ok(ticketRepository.save(ticket));
    }

    @Operation(summary = "Assign agent to ticket", description = "Assigns or reassigns an agent to a specific incident.")
    @PatchMapping("/{id}/assign")
    public ResponseEntity<?> assignTicketAgent(@PathVariable Long id, @RequestParam String agent, @RequestParam(required = false) Long roleId) {
        if (roleId != null && roleId == 1L) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Employees cannot assign tickets."));
        }

        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setAssignedAgentName(agent);
        return ResponseEntity.ok(ticketRepository.save(ticket));
    }

    @Operation(summary = "Archive ticket", description = "Restricted to Managers and Administrators.")
    @PatchMapping("/{id}/archive")
    public ResponseEntity<?> archiveTicket(@PathVariable Long id, @RequestParam Long roleId) {
        if (roleId == null || (roleId != 3 && roleId != 5)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Only Managers and Administrators can archive tickets."));
        }

        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setStatus("ARCHIVED");
        Ticket saved = ticketRepository.save(ticket);
        return ResponseEntity.ok(Map.of("message", "Ticket successfully archived.", "ticket", saved));
    }

    @Operation(summary = "Delete ticket permanently", description = "Restricted to Administrators.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long id, @RequestParam Long roleId) {
        if (roleId == null || roleId != 5) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Only Administrators can permanently delete tickets."));
        }

        if (!ticketRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Ticket not found"));
        }

        ticketRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Ticket permanently deleted from enterprise records."));
    }

    // --- COMMENTS & HISTORY ENDPOINTS (Added for full API Contract compliance) ---

    @Operation(summary = "Get comments for a ticket", description = "Retrieves all comments associated with an incident.")
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TicketComment>> getTicketComments(@PathVariable Long id) {
        return ResponseEntity.ok(commentRepository.findByTicketId(id));
    }

    @Operation(summary = "Add a comment to a ticket", description = "Posts a new public reply or internal staff note.")
    @PostMapping("/{id}/comments")
    public ResponseEntity<TicketComment> addTicketComment(@PathVariable Long id, @RequestBody TicketComment comment) {
        comment.setTicketId(id);
        TicketComment saved = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Get ticket history and audit trail", description = "Retrieves chronological state transition history for a ticket.")
    @GetMapping("/{id}/history")
    public ResponseEntity<List<TicketHistory>> getTicketHistory(@PathVariable Long id) {
        return ResponseEntity.ok(historyRepository.findByTicketId(id));
    }

    // --------------------------------------------------------------------------

    @Operation(summary = "Get SLA calculation status", description = "Calculates remaining resolution time and flags BREACHED, AT_RISK, or ON_TRACK states.")
    @GetMapping("/{id}/sla-status")
    public ResponseEntity<?> getTicketSlaStatus(@PathVariable Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resDue = ticket.getResolutionDue();
        String status = ticket.getStatus();

        if (resDue == null) {
            return ResponseEntity.ok(Map.of("slaState", "UNKNOWN", "message", "No SLA due date set"));
        }

        if (status != null && (status.equalsIgnoreCase("RESOLVED") || status.equalsIgnoreCase("CLOSED") || status.equalsIgnoreCase("ARCHIVED"))) {
            return ResponseEntity.ok(Map.of("slaState", "MET", "resolutionDue", resDue));
        }

        String slaState;
        if (now.isAfter(resDue)) {
            slaState = "BREACHED";
        } else {
            long hoursRemaining = Duration.between(now, resDue).toHours();
            slaState = (hoursRemaining <= 4) ? "AT_RISK" : "ON_TRACK";
        }

        return ResponseEntity.ok(Map.of(
                "ticketId", ticket.getId(),
                "ticketNumber", ticket.getTicketNumber(),
                "resolutionDue", resDue,
                "slaState", slaState,
                "hoursRemaining", Duration.between(now, resDue).toHours()
        ));
    }

    @Operation(summary = "Get unassigned ticket queue")
    @GetMapping("/queues/unassigned")
    public ResponseEntity<List<Ticket>> getUnassignedQueue() {
        return ResponseEntity.ok(ticketRepository.findUnassignedTickets());
    }

    @Operation(summary = "Get tickets assigned to agent")
    @GetMapping("/queues/assigned-to-me")
    public ResponseEntity<List<Ticket>> getAssignedToMeQueue(@RequestParam String agentName) {
        return ResponseEntity.ok(ticketRepository.findByAssignedAgentName(agentName));
    }

    @Operation(summary = "Get high and critical priority ticket queue")
    @GetMapping("/queues/high-priority")
    public ResponseEntity<List<Ticket>> getHighPriorityQueue() {
        return ResponseEntity.ok(ticketRepository.findHighPriorityTickets());
    }

    @Operation(summary = "Get SLA overdue ticket queue")
    @GetMapping("/queues/overdue")
    public ResponseEntity<List<Ticket>> getOverdueQueue() {
        return ResponseEntity.ok(ticketRepository.findOverdueTickets(LocalDateTime.now()));
    }

    @Operation(summary = "Get recently updated tickets queue")
    @GetMapping("/queues/recently-updated")
    public ResponseEntity<List<Ticket>> getRecentlyUpdatedQueue() {
        return ResponseEntity.ok(ticketRepository.findTop10ByOrderByUpdatedAtDesc());
    }

    @Operation(summary = "Get active ticket count per agent workload")
    @GetMapping("/operations/workload")
    public ResponseEntity<List> getAgentWorkload() {
        List<Object[]> workloads = ticketRepository.getAgentWorkloadCounts();
        List result = new ArrayList<>();

        for (Object[] row : workloads) {
            Map<String, Object> map = new HashMap<>();
            map.put("agentName", row[0]);
            map.put("activeTicketCount", row[1]);
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Escalate ticket priority with audit reason")
    @PatchMapping("/{id}/escalate")
    public ResponseEntity<?> escalateTicket(
            @PathVariable Long id,
            @RequestParam String newPriority,
            @RequestParam String reason,
            @RequestParam Long roleId) {

        if (roleId == null || (roleId != 2 && roleId != 3 && roleId != 5)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access Denied: Only Support Agents and Managers can escalate tickets."));
        }

        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
        String oldPriority = ticket.getPriority();

        ticket.setPriority(newPriority);
        ticket.setStatus("ESCALATED");
        Ticket saved = ticketRepository.save(ticket);

        return ResponseEntity.ok(Map.of(
                "message", "Ticket successfully escalated.",
                "ticket", saved,
                "escalationReason", reason,
                "previousPriority", oldPriority,
                "newPriority", newPriority
        ));
    }
}