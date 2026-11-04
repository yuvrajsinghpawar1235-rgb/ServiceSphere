const API_BASE_URL = 'http://localhost:8080/api';

// Helper for handling fetch responses
async function handleResponse(response) {
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.error || 'Something went wrong with the request');
    }
    return response.json();
}

export const ticketService = {
    // 1. Get filtered and paginated tickets (supports role-based filtering)
    async getTickets(params = {}) {
        const query = new URLSearchParams(params).toString();
        const response = await fetch(`${API_BASE_URL}/tickets?${query}`);
        return handleResponse(response);
    },

    // 2. Create a new ticket
    async createTicket(ticketData, roleId) {
        const response = await fetch(`${API_BASE_URL}/tickets?roleId=${roleId}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(ticketData)
        });
        return handleResponse(response);
    },

    // 3. Update ticket status
    async updateStatus(ticketId, status, roleId) {
        const response = await fetch(`${API_BASE_URL}/tickets/${ticketId}/status?status=${encodeURIComponent(status)}&roleId=${roleId}`, {
            method: 'PATCH'
        });
        return handleResponse(response);
    },

    // 4. Get comments for a ticket
    async getComments(ticketId) {
        const response = await fetch(`${API_BASE_URL}/tickets/${ticketId}/comments`);
        return handleResponse(response);
    },

    // 5. Add a comment or internal note
    async addComment(ticketId, commentData) {
        const response = await fetch(`${API_BASE_URL}/tickets/${ticketId}/comments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(commentData)
        });
        return handleResponse(response);
    },

    // 6. Get audit history/timeline for a ticket
    async getHistory(ticketId) {
        const response = await fetch(`${API_BASE_URL}/tickets/${ticketId}/history`);
        return handleResponse(response);
    }
};

export const reportService = {
    async getTicketSummary() {
        const response = await fetch(`${API_BASE_URL}/reports/tickets/summary`);
        return handleResponse(response);
    },

    async getSlaMetrics() {
        const response = await fetch(`${API_BASE_URL}/reports/sla`);
        return handleResponse(response);
    },

    async getAssetSummary() {
        const response = await fetch(`${API_BASE_URL}/reports/assets/summary`);
        return handleResponse(response);
    },

    async getAgentWorkload() {
        const response = await fetch(`${API_BASE_URL}/reports/agent-workload`);
        return handleResponse(response);
    }
};