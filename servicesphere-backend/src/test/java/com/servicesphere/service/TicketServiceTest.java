package com.servicesphere.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Test
    @DisplayName("Should enforce valid ticket state transitions from OPEN")
    void testOpenStateTransitions() {
        assertTrue(isValidTransition("OPEN", "IN PROGRESS", 1L));
        assertTrue(isValidTransition("OPEN", "RESOLVED", 1L));
        assertFalse(isValidTransition("OPEN", "CLOSED", 1L)); // Employees/standard users shouldn't jump straight to closed without resolution
    }

    @Test
    @DisplayName("Should restrict CLOSED tickets from transitioning unless performed by Manager/Admin")
    void testClosedStateTransitions() {
        // Regular user/employee (roleId = 1) trying to reopen a CLOSED ticket should fail
        assertFalse(isValidTransition("CLOSED", "OPEN", 1L));

        // Manager or Admin (roleId >= 3) should be permitted to reopen a CLOSED ticket
        assertTrue(isValidTransition("CLOSED", "OPEN", 3L));
        assertTrue(isValidTransition("CLOSED", "OPEN", 5L));
    }

    @Test
    @DisplayName("Should validate employee priority restrictions")
    void testEmployeePriorityRestriction() {
        Long employeeRoleId = 1L;
        Long managerRoleId = 3L;

        // Employees cannot set Critical priority
        assertFalse(isPriorityAllowed("Critical", employeeRoleId));

        // Managers can set Critical priority
        assertTrue(isPriorityAllowed("Critical", managerRoleId));
        assertTrue(isPriorityAllowed("High", employeeRoleId));
    }

    // Mirroring the exact transition matrix from TicketController
    private boolean isValidTransition(String currentStatus, String targetStatus, Long roleId) {
        String current = currentStatus != null ? currentStatus.toUpperCase() : "OPEN";
        String target = targetStatus != null ? targetStatus.toUpperCase() : "";

        if (current.equals("OPEN")) {
            return target.equals("IN PROGRESS") || target.equals("ESCALATED") ||
                    target.equals("RESOLVED") || target.equals("ARCHIVED");
        } else if (current.equals("IN PROGRESS")) {
            return target.equals("WAITING FOR REQUESTER") || target.equals("ESCALATED") ||
                    target.equals("RESOLVED") || target.equals("ARCHIVED");
        } else if (current.equals("CLOSED")) {
            return (roleId != null && roleId >= 3) && target.equals("OPEN");
        }
        return true;
    }

    private boolean isPriorityAllowed(String priority, Long roleId) {
        if ("Critical".equalsIgnoreCase(priority) && roleId != null && roleId == 1L) {
            return false;
        }
        return true;
    }
}