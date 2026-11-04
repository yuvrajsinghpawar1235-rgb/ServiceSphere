import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'https://servicesphere-rjrp.onrender.com/api';

const handleRequest = async (apiCall) => {
    try {
        const response = await apiCall();
        return response.data;
    } catch (error) {
        const message = error.response?.data?.error || error.response?.data?.message || error.message;
        throw new Error(message);
    }
};

// --- Authentication ---
export const loginUser = (credentials) => handleRequest(() => axios.post(`${API_BASE_URL}/login`, credentials));

// --- Tickets ---
export const getTickets = (params) => handleRequest(() => axios.get(`${API_BASE_URL}/tickets`, { params }));
export const createTicket = (ticketData, roleId) => handleRequest(() => axios.post(`${API_BASE_URL}/tickets`, ticketData, { params: { roleId } }));
export const updateTicketStatus = (id, status, roleId) => handleRequest(() => axios.patch(`${API_BASE_URL}/tickets/${id}/status`, null, { params: { status, roleId } }));
export const updateTicketPriority = (id, priority, roleId) => handleRequest(() => axios.patch(`${API_BASE_URL}/tickets/${id}/priority`, null, { params: { priority, roleId } }));
export const assignTicketAgent = (id, agentName, roleId) => handleRequest(() => axios.patch(`${API_BASE_URL}/tickets/${id}/assign`, null, { params: { agent: agentName, roleId } }));
export const escalateTicket = (id, roleId) => handleRequest(() => axios.post(`${API_BASE_URL}/tickets/${id}/escalate`, null, { params: { roleId } }));
export const archiveTicket = (id, roleId) => handleRequest(() => axios.post(`${API_BASE_URL}/tickets/${id}/archive`, null, { params: { roleId } }));

// Comments & Audit History
export const getTicketComments = (ticketId) => handleRequest(() => axios.get(`${API_BASE_URL}/tickets/${ticketId}/comments`));
export const addTicketComment = (ticketId, commentData) => handleRequest(() => axios.post(`${API_BASE_URL}/tickets/${ticketId}/comments`, commentData));
export const getTicketHistory = (ticketId) => handleRequest(() => axios.get(`${API_BASE_URL}/tickets/${ticketId}/history`));

// --- Assets ---
export const getAssets = () => handleRequest(() => axios.get(`${API_BASE_URL}/assets`));
export const createAsset = (assetData, roleId) => handleRequest(() => axios.post(`${API_BASE_URL}/assets`, assetData, { params: { roleId } }));
export const updateAssetStatus = (id, status) => handleRequest(() => axios.patch(`${API_BASE_URL}/assets/${id}/status`, null, { params: { status } }));

// --- Knowledge Base ---
export const getKnowledgeArticles = (params) => handleRequest(() => axios.get(`${API_BASE_URL}/kb`, { params }));

// --- Live Reports ---
export const reportService = {
    getTicketSummary: () => handleRequest(() => axios.get(`${API_BASE_URL}/reports/tickets/summary`)),
    getSlaMetrics: () => handleRequest(() => axios.get(`${API_BASE_URL}/reports/sla`)),
    getAssetSummary: () => handleRequest(() => axios.get(`${API_BASE_URL}/reports/assets/summary`)),
    getAgentWorkload: () => handleRequest(() => axios.get(`${API_BASE_URL}/reports/agent-workload`))
};