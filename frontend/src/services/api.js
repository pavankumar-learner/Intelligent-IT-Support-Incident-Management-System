import axios from 'axios';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Handle 401 Unauthorized
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// Auth Endpoints
export const loginApi = (email, password) => api.post('/auth/login', { email, password });
export const registerApi = (data) => api.post('/auth/register', data);
export const getCurrentUserApi = () => api.get('/auth/me');

// Incident Endpoints
export const createIncidentApi = (data) => api.post('/incidents', data);
export const getIncidentsApi = (params) => api.get('/incidents', { params });
export const getIncidentByIdApi = (id) => api.get(`/incidents/${id}`);
export const assignAgentApi = (id, agentId) => api.put(`/incidents/${id}/assign`, { agentId });
export const updateStatusApi = (id, status, comment) => api.put(`/incidents/${id}/status`, { status, comment });
export const updatePriorityApi = (id, priority, comment) => api.put(`/incidents/${id}/priority`, { priority, comment });
export const addCommentApi = (id, comment) => api.post(`/incidents/${id}/comments`, { comment });
export const getCommentsApi = (id) => api.get(`/incidents/${id}/comments`);
export const getHistoryApi = (id) => api.get(`/incidents/${id}/history`);

// AI Endpoints
export const predictCategoryApi = (title, description) => api.post('/ai/predict-category', { title, description });
export const predictPriorityApi = (title, description, category) => api.post('/ai/predict-priority', { title, description, category });
export const checkDuplicateApi = (title, description, threshold = 0.65) => api.post('/ai/check-duplicate', { title, description, threshold });

// User Endpoints (Admin)
export const getUsersApi = () => api.get('/users');
export const getAgentsApi = () => api.get('/users/agents');
export const createUserApi = (data) => api.post('/users', data);
export const updateUserApi = (id, data) => api.put(`/users/${id}`, data);
export const toggleUserStatusApi = (id) => api.put(`/users/${id}/toggle-status`);
export const deleteUserApi = (id) => api.delete(`/users/${id}`);

// Dashboard Analytics
export const getDashboardStatsApi = () => api.get('/dashboard/statistics');

export default api;
