package com.servicesphere.entity;

import com.servicesphere.listener.TicketHistoryListener;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@EntityListeners(TicketHistoryListener.class)
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ticketNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String category; // Hardware, Software, Network, Account/Access, Email, Security, Other

    @Column(nullable = false)
    private String priority; // Low, Medium, High, Critical

    @Column(nullable = false)
    private String status; // OPEN, IN PROGRESS, WAITING FOR REQUESTER, ESCALATED, RESOLVED, CLOSED, ARCHIVED

    @Column(nullable = false)
    private Long requesterId;

    private String assignedAgentName;

    @Column(nullable = false)
    private String department;

    private Long assetId;

    private LocalDateTime responseDue;
    private LocalDateTime resolutionDue;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "OPEN";
        }
        if (department == null) {
            department = "General IT";
        }
        // Auto-set default SLA target (e.g., 48 hours for resolution if not specified)
        if (resolutionDue == null) {
            resolutionDue = LocalDateTime.now().plusHours(48);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Ticket() {}

    public Ticket(String title, String description, String category, String priority, Long requesterId, String department) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.requesterId = requesterId;
        this.department = department;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }

    public String getAssignedAgentName() { return assignedAgentName; }
    public void setAssignedAgentName(String assignedAgentName) { this.assignedAgentName = assignedAgentName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public LocalDateTime getResponseDue() { return responseDue; }
    public void setResponseDue(LocalDateTime responseDue) { this.responseDue = responseDue; }

    public LocalDateTime getResolutionDue() { return resolutionDue; }
    public void setResolutionDue(LocalDateTime resolutionDue) { this.resolutionDue = resolutionDue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}