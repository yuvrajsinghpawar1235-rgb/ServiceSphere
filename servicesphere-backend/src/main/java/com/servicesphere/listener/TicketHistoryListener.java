package com.servicesphere.listener;

import com.servicesphere.entity.Ticket;
import com.servicesphere.entity.TicketHistory;
import com.servicesphere.repository.TicketHistoryRepository;
import com.servicesphere.util.BeanUtil;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

public class TicketHistoryListener {

    @PostPersist
    public void afterCreate(Ticket ticket) {
        TicketHistoryRepository repo = BeanUtil.getBean(TicketHistoryRepository.class);
        if (repo != null) {
            TicketHistory history = new TicketHistory();
            history.setTicketId(ticket.getId());
            history.setActorName("System");
            history.setEventType("CREATED");
            history.setNewValue(ticket.getStatus());
            // timestamp is handled automatically by TicketHistory's @PrePersist
            repo.save(history);
        }
    }

    @PostUpdate
    public void afterUpdate(Ticket ticket) {
        TicketHistoryRepository repo = BeanUtil.getBean(TicketHistoryRepository.class);
        if (repo != null) {
            TicketHistory history = new TicketHistory();
            history.setTicketId(ticket.getId());
            history.setActorName("System");
            history.setEventType("UPDATED");
            history.setNewValue(ticket.getStatus());
            repo.save(history);
        }
    }
}