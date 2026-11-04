// src/services/apiService.js

const BASE_URL = 'http://localhost:8080/api'; // Update port if your Spring Boot runs elsewhere

/**
 * Helper to build URL query strings for search, filters, and pagination.
 */
const buildQueryString = (params = {}) => {
    const query = new URLSearchParams();
    Object.keys(params).forEach((key) => {
        if (params[key] !== null && params[key] !== undefined && params[key] !== '') {
            query.append(key, params[key]);
        }
    });
    const queryString = query.toString();
    return queryString ? `?${queryString}` : '';
};

/**
 * Handles HTTP response parsing and standardizes backend error payloads.
 */
export const handleApiResponse = async (response) => {
    let data = null;
    const contentType = response.headers.get('content-type');

    if (contentType && contentType.includes('application/json')) {
        data = await response.json().catch(() => null);
    }

    if (!response.ok) {
        // Extract Spring Boot structured message or fallback error message
        const errorMessage =
            (data && data.error) ||
            (data && data.message) ||
            `HTTP ${response.status}: ${response.statusText}`;

        const error = new Error(errorMessage);
        error.status = response.status;
        error.data = data;
        throw error;
    }

    return data;
};

/**
 * Generic API fetch wrapper with JSON header defaults and network error safety.
 */
export const apiRequest = async (endpoint, options = {}) => {
    const { method = 'GET', body, params, headers = {} } = options;

    const queryString = params ? buildQueryString(params) : '';
    const url = `${BASE_URL}${endpoint}${queryString}`;

    const config = {
        method,
        headers: {
            'Content-Type': 'application/json',
            ...headers,
        },
    };

    if (body) {
        config.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(url, config);
        return await handleApiResponse(response);
    } catch (error) {
        if (error.name === 'TypeError' && error.message === 'Failed to fetch') {
            throw new Error('Unable to connect to ServiceSphere server. Please verify your backend server is running.');
        }
        throw error;
    }
};

// Convenient shorthand methods for everyday frontend calls
export const api = {
    get: (endpoint, params, headers) => apiRequest(endpoint, { method: 'GET', params, headers }),
    post: (endpoint, body, params, headers) => apiRequest(endpoint, { method: 'POST', body, params, headers }),
    put: (endpoint, body, params, headers) => apiRequest(endpoint, { method: 'PUT', body, params, headers }),
    patch: (endpoint, body, params, headers) => apiRequest(endpoint, { method: 'PATCH', body, params, headers }),
    delete: (endpoint, params, headers) => apiRequest(endpoint, { method: 'DELETE', params, headers }),
};

export default api;