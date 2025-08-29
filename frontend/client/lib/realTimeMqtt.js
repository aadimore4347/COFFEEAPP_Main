// Real-time MQTT Integration for Coffee Machine Monitoring
// Connects to actual MQTT worker service and displays real sensor data

import { ENV_CONFIG } from "@/config";

class RealTimeMQTT {
  constructor() {
    this.ws = null;
    this.handlers = new Map();
    this.isConnected = false;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectDelay = 1000;
    this.heartbeatInterval = null;
    this.connectionStatus = 'disconnected';
    
    // MQTT topics for coffee machine monitoring
    this.topics = {
      temperature: 'coffeeMachine/+/temperature',
      waterLevel: 'coffeeMachine/+/waterLevel',
      milkLevel: 'coffeeMachine/+/milkLevel',
      beansLevel: 'coffeeMachine/+/beansLevel',
      status: 'coffeeMachine/+/status',
      usage: 'coffeeMachine/+/usage',
    };
  }

  // Connect to MQTT worker via WebSocket
  connect() {
    try {
      // For now, we'll use HTTP polling to the MQTT worker endpoints
      // In production, you'd set up a WebSocket connection to the MQTT worker
      this.connectionStatus = 'connecting';
      this.startPolling();
      this.isConnected = true;
      this.connectionStatus = 'connected';
      
      console.log('🔌 Real-time MQTT connected via HTTP polling');
      this.emit('connection', { status: 'connected' });
      
    } catch (error) {
      console.error('Failed to connect to MQTT:', error);
      this.connectionStatus = 'error';
      this.emit('connection', { status: 'error', error: error.message });
    }
  }

  // Start polling MQTT worker endpoints
  startPolling() {
    // Poll simulator stats every 30 seconds to match MQTT simulator
    this.statsInterval = setInterval(async () => {
      try {
        const stats = await this.getSimulatorStats();
        this.processSimulatorData(stats);
      } catch (error) {
        console.warn('Failed to fetch simulator stats:', error);
      }
    }, 30000); // Changed from 5000 to 30000 to match simulator

    // Poll individual machine data every 30 seconds to match simulator
    this.machineInterval = setInterval(async () => {
      try {
        await this.pollMachineData();
      } catch (error) {
        console.warn('Failed to poll machine data:', error);
      }
    }, 30000); // Changed from 3000 to 30000 to match simulator
  }

  // Get simulator statistics from MQTT worker
  async getSimulatorStats() {
    try {
      const response = await fetch('http://localhost:8081/api/simulator/stats');
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      throw new Error(`Failed to fetch simulator stats: ${error.message}`);
    }
  }

  // Poll data for individual machines
  async pollMachineData() {
    try {
      const stats = await this.getSimulatorStats();
      const machines = stats.machines || {};
      
      // Process each machine's data
      Object.entries(machines).forEach(([machineKey, machineData]) => {
        const machineId = machineKey.replace('Machine_', '');
        this.processMachineData(machineId, machineData);
      });
    } catch (error) {
      console.warn('Failed to poll machine data:', error);
    }
  }

  // Process simulator data and emit events
  processSimulatorData(stats) {
    if (!stats || !stats.machines) return;

    // Emit overall simulator status
    this.emit('simulator:stats', {
      enabled: stats.enabled,
      intervalMs: stats.intervalMs,
      numberOfMachines: stats.numberOfMachines,
      totalMessagesSent: stats.totalMessagesSent,
      lastUpdate: stats.lastUpdate,
    });

    // Process individual machine data
    Object.entries(stats.machines).forEach(([machineKey, machineData]) => {
      const machineId = machineKey.replace('Machine_', '');
      this.processMachineData(machineId, machineData);
    });
  }

  // Process individual machine data
  processMachineData(machineId, machineData) {
    // Emit temperature updates
    if (machineData.temperature !== undefined) {
      this.emit(`coffeeMachine/${machineId}/temperature`, {
        machineId,
        temperature: machineData.temperature,
        unit: 'Celsius',
        timestamp: new Date().toISOString(),
      });
    }

    // Emit water level updates
    if (machineData.waterLevel !== undefined) {
      this.emit(`coffeeMachine/${machineId}/waterLevel`, {
        machineId,
        waterLevel: machineData.waterLevel,
        unit: 'percent',
        timestamp: new Date().toISOString(),
      });
    }

    // Emit milk level updates
    if (machineData.milkLevel !== undefined) {
      this.emit(`coffeeMachine/${machineId}/milkLevel`, {
        machineId,
        milkLevel: machineData.milkLevel,
        unit: 'percent',
        timestamp: new Date().toISOString(),
      });
    }

    // Emit beans level updates
    if (machineData.beansLevel !== undefined) {
      this.emit(`coffeeMachine/${machineId}/beansLevel`, {
        machineId,
        beansLevel: machineData.beansLevel,
        unit: 'percent',
        timestamp: new Date().toISOString(),
      });
    }

    // Emit status updates
    if (machineData.status !== undefined) {
      this.emit(`coffeeMachine/${machineId}/status`, {
        machineId,
        status: machineData.status,
        timestamp: new Date().toISOString(),
      });
    }

    // Emit usage updates
    if (machineData.usageCount !== undefined) {
      this.emit(`coffeeMachine/${machineId}/usage`, {
        machineId,
        usageCount: machineData.usageCount,
        lastUsage: machineData.lastUsage,
        timestamp: new Date().toISOString(),
      });
    }

    // Emit comprehensive machine update
    this.emit(`coffeeMachine/${machineId}/update`, {
      machineId,
      ...machineData,
      timestamp: new Date().toISOString(),
    });
  }

  // Subscribe to MQTT topics
  subscribe(topic, handler) {
    if (!this.handlers.has(topic)) {
      this.handlers.set(topic, []);
    }
    this.handlers.get(topic).push(handler);
    
    console.log(`📡 Subscribed to topic: ${topic}`);
  }

  // Unsubscribe from MQTT topics
  unsubscribe(topic, handler) {
    const handlers = this.handlers.get(topic);
    if (!handlers) return;
    
    const index = handlers.indexOf(handler);
    if (index > -1) {
      handlers.splice(index, 1);
    }
    
    if (handlers.length === 0) {
      this.handlers.delete(topic);
    }
    
    console.log(`📡 Unsubscribed from topic: ${topic}`);
  }

  // Emit events to handlers
  emit(topic, data) {
    const handlers = this.handlers.get(topic) || [];
    
    // Support wildcard topics (e.g., coffeeMachine/+/temperature)
    const wildcardHandlers = [];
    for (const [pattern, hs] of this.handlers.entries()) {
      if (pattern.includes('+')) {
        const regex = new RegExp('^' + pattern.replaceAll('+', '[^/]+') + '$');
        if (regex.test(topic)) {
          wildcardHandlers.push(...hs);
        }
      }
    }

    // Call all matching handlers
    [...handlers, ...wildcardHandlers].forEach(handler => {
      try {
        handler(data);
      } catch (error) {
        console.error(`Error in MQTT handler for ${topic}:`, error);
      }
    });
  }

  // Disconnect from MQTT
  disconnect() {
    this.isConnected = false;
    this.connectionStatus = 'disconnected';
    
    if (this.statsInterval) {
      clearInterval(this.statsInterval);
      this.statsInterval = null;
    }
    
    if (this.machineInterval) {
      clearInterval(this.machineInterval);
      this.machineInterval = null;
    }
    
    console.log('🔌 Real-time MQTT disconnected');
    this.emit('connection', { status: 'disconnected' });
  }

  // Get connection status
  getConnectionStatus() {
    return {
      isConnected: this.isConnected,
      status: this.connectionStatus,
      reconnectAttempts: this.reconnectAttempts,
    };
  }

  // Manual trigger for data generation (for testing)
  async triggerDataGeneration() {
    try {
      const response = await fetch('http://localhost:8081/api/simulator/trigger', {
        method: 'POST',
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      
      const result = await response.json();
      console.log('🚀 Triggered data generation:', result);
      return result;
    } catch (error) {
      console.error('Failed to trigger data generation:', error);
      throw error;
    }
  }

  // Reset machine states (for testing)
  async resetMachineStates() {
    try {
      const response = await fetch('http://localhost:8081/api/simulator/reset', {
        method: 'POST',
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      
      const result = await response.json();
      console.log('🔄 Reset machine states:', result);
      return result;
    } catch (error) {
      console.error('Failed to reset machine states:', error);
      throw error;
    }
  }

  // Get health status
  async getHealth() {
    try {
      const response = await fetch('http://localhost:8081/api/simulator/health');
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      return await response.json();
    } catch (error) {
      throw new Error(`Failed to get simulator health: ${error.message}`);
    }
  }
}

// Create singleton instance
export const realTimeMQTT = new RealTimeMQTT();

// Export for use in components
export default realTimeMQTT;