// Backend API Integration for Coffee Machine Monitoring System
// Connects React frontend to Spring Boot backend APIs

import { ENV_CONFIG } from "@/config";

const API_BASE_URL = ENV_CONFIG.API_BASE_URL || "http://localhost:8080/api";

class BackendAPI {
  constructor() {
    this.baseURL = API_BASE_URL;
    this.token = null;
  }

  // Set authentication token
  setToken(token) {
    this.token = token;
  }

  // Get authentication headers
  getHeaders() {
    const headers = {
      'Content-Type': 'application/json',
    };
    
    if (this.token) {
      headers['Authorization'] = `Bearer ${this.token}`;
    }
    
    return headers;
  }

  // Generic API request method
  async request(endpoint, options = {}) {
    try {
      const url = `${this.baseURL}${endpoint}`;
      const config = {
        headers: this.getHeaders(),
        ...options,
      };

      const response = await fetch(url, config);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error(`API request failed for ${endpoint}:`, error);
      throw error;
    }
  }

  // Authentication APIs
  async login(credentials) {
    return this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    });
  }

  async register(userData) {
    return this.request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData),
    });
  }

  async refreshToken(refreshToken) {
    return this.request('/auth/refresh', {
      method: 'POST',
      body: JSON.stringify({ refreshToken }),
    });
  }

  // Machine Management APIs
  async getMachines() {
    return this.request('/machine');
  }

  async getMachineById(machineId) {
    return this.request(`/machine/${machineId}`);
  }

  async createMachine(machineData) {
    return this.request('/machine', {
      method: 'POST',
      body: JSON.stringify(machineData),
    });
  }

  async updateMachine(machineId, machineData) {
    return this.request(`/machine/${machineId}`, {
      method: 'PUT',
      body: JSON.stringify(machineData),
    });
  }

  async deleteMachine(machineId) {
    return this.request(`/machine/${machineId}`, {
      method: 'DELETE',
    });
  }

  async getMachineStatus(machineId) {
    return this.request(`/machine/${machineId}/status`);
  }

  async getMachineLevels(machineId) {
    return this.request(`/machine/${machineId}/levels`);
  }

  async getMachineHistory(machineId) {
    return this.request(`/machine/${machineId}/history`);
  }

  async brewCoffee(machineId, brewData) {
    return this.request(`/machine/${machineId}/brew`, {
      method: 'POST',
      body: JSON.stringify(brewData),
    });
  }

  // Admin APIs
  async getUsers() {
    return this.request('/admin/users');
  }

  async createUser(userData) {
    return this.request('/admin/users', {
      method: 'POST',
      body: JSON.stringify(userData),
    });
  }

  async updateUser(userId, userData) {
    return this.request(`/admin/users/${userId}`, {
      method: 'PUT',
      body: JSON.stringify(userData),
    });
  }

  async deleteUser(userId) {
    return this.request(`/admin/users/${userId}`, {
      method: 'DELETE',
    });
  }

  async getSystemStats() {
    return this.request('/admin/stats');
  }

  // Facility APIs
  async getFacilityMachines(facilityId) {
    return this.request(`/facility/${facilityId}/machines`);
  }

  async getFacilityStats(facilityId) {
    return this.request(`/facility/${facilityId}/stats`);
  }

  // Health Check APIs
  async getHealthStatus() {
    return this.request('/health');
  }

  async getDetailedHealth() {
    return this.request('/health/detailed');
  }

  // MQTT Simulator APIs
  async getSimulatorStats() {
    return this.request('/simulator/stats');
  }

  async triggerDataGeneration() {
    return this.request('/simulator/trigger', {
      method: 'POST',
    });
  }

  async resetMachineStates() {
    return this.request('/simulator/reset', {
      method: 'POST',
    });
  }

  async getSimulatorHealth() {
    return this.request('/simulator/health');
  }

  async getMachineDetails(machineId) {
    return this.request(`/simulator/machine/${machineId}`);
  }

  // Utility methods
  async checkConnection() {
    try {
      const response = await fetch(`${this.baseURL}/health`);
      return response.ok;
    } catch {
      return false;
    }
  }

  getApiStatus() {
    return {
      baseURL: this.baseURL,
      hasToken: !!this.token,
      isConnected: this.checkConnection(),
    };
  }
}

// Create singleton instance
export const backendAPI = new BackendAPI();

// Export for use in components
export default backendAPI;