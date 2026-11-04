package com.servicesphere.repository;

import com.servicesphere.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByRequesterId(Long requesterId);

    // --- DASHBOARD METRICS & COUNTS ---
    long countByStatus(String status);

    @Query("SELECT t.status, COUNT(t) FROM Ticket t GROUP BY t.status")
    List<Object[]> getStatusBreakdown();

    @Query("SELECT t.priority, COUNT(t) FROM Ticket t GROUP BY t.priority")
    List<Object[]> getPriorityBreakdown();

    // --- SPECIALIZED QUEUES & WORKLOAD QUERIES ---

    @Query("SELECT t FROM Ticket t WHERE t.assignedAgentName IS NULL OR t.assignedAgentName = ''")
    List<Ticket> findUnassignedTickets();

    List<Ticket> findByAssignedAgentName(String agentName);

    @Query("SELECT t FROM Ticket t WHERE t.priority IN ('High', 'Critical') AND t.status NOT IN ('Closed', 'Resolved', 'ARCHIVED')")
    List<Ticket> findHighPriorityTickets();

    @Query("SELECT t FROM Ticket t WHERE t.resolutionDue < :now AND t.status NOT IN ('Closed', 'Resolved', 'ARCHIVED')")
    List<Ticket> findOverdueTickets(@Param("now") LocalDateTime now);

    List<Ticket> findTop10ByOrderByUpdatedAtDesc();

    @Query("SELECT t.assignedAgentName, COUNT(t) FROM Ticket t WHERE t.assignedAgentName IS NOT NULL AND t.assignedAgentName != '' GROUP BY t.assignedAgentName")
    List<Object[]> getAgentWorkloadCounts();

    // --- ADVANCED FILTERING & PAGINATION ---
    @Query("SELECT t FROM Ticket t WHERE " +
            "(:requesterId IS NULL OR t.requesterId = :requesterId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:category IS NULL OR t.category = :category) AND " +
            "(:assignee IS NULL OR t.assignedAgentName = :assignee) AND " +
            "(:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Ticket> findFilteredTickets(
            @Param("requesterId") Long requesterId,
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("category") String category,
            @Param("assignee") String assignee,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}