// MQTT Client for Real-time Communication (JavaScript)
// Simulated MQTT with periodic updates for demo

class MQTTClient {
  constructor() {
    this.handlers = new Map();
    this._isConnected = false;
    this.simulationInterval = null;
  }

  connect() {
    return new Promise((resolve) => {
      setTimeout(() => {
        this._isConnected = true;
        this.startSimulation();
        resolve();
      }, 1000);
    });
  }

  disconnect() {
    this._isConnected = false;
    if (this.simulationInterval) clearInterval(this.simulationInterval);
  }

  subscribe(topic, handler) {
    if (!this.handlers.has(topic)) this.handlers.set(topic, []);
    this.handlers.get(topic).push(handler);
  }

  unsubscribe(topic, handler) {
    const handlers = this.handlers.get(topic);
    if (!handlers) return;
    const index = handlers.indexOf(handler);
    if (index > -1) handlers.splice(index, 1);
    if (handlers.length === 0) this.handlers.delete(topic);
  }

  publish(topic, payload) {
    if (!this._isConnected) return;
    const message = { topic, payload, timestamp: Date.now() };
    this.deliverMessage(message);
  }

  deliverMessage(message) {
    const directHandlers = this.handlers.get(message.topic) || [];

    // Support "+" wildcard for simple topic matching used in demo
    const wildcardHandlers = [];
    for (const [topic, hs] of this.handlers.entries()) {
      if (topic.includes("+")) {
        const regex = new RegExp("^" + topic.replaceAll("+", "[^/]+") + "$");
        if (regex.test(message.topic)) wildcardHandlers.push(...hs);
      }
    }

    [...directHandlers, ...wildcardHandlers].forEach((handler) => {
      try { handler(message); } catch { /* ignore */ }
    });
  }

  startSimulation() {
    this.simulationInterval = setInterval(() => this.simulateRealTimeUpdates(), 3000);
  }

  simulateRealTimeUpdates() {
    const machines = ["A-001", "A-002", "B-001"];
    machines.forEach((machineId) => {
      const statusUpdate = {
        machineId,
        status: Math.random() > 0.9 ? "maintenance" : "operational",
        temperature: 88 + Math.random() * 8,
        pressure: 13 + Math.random() * 4,
        waterLevel: Math.max(0, 50 + Math.random() * 50),
        milkLevel: Math.max(0, 30 + Math.random() * 70),
        coffeeBeansLevel: Math.max(0, 40 + Math.random() * 60),
        sugarLevel: Math.max(0, 60 + Math.random() * 40),
        currentOrder: Math.random() > 0.7 ? ["Espresso", "Latte", "Cappuccino"][Math.floor(Math.random() * 3)] : undefined,
        queueLength: Math.floor(Math.random() * 5),
      };
      this.publish(`coffee/machines/${machineId}/status`, statusUpdate);

      if (Math.random() > 0.8) {
        const usageUpdate = {
          machineId,
          cupsToday: Math.floor(100 + Math.random() * 50),
          revenue: Math.floor(300 + Math.random() * 200),
          lastActivity: "Just now",
        };
        this.publish(`coffee/machines/${machineId}/usage`, usageUpdate);
      }
    });

    if (Math.random() > 0.95) {
      this.publish("coffee/alerts", {
        type: "low_supply",
        machineId: machines[Math.floor(Math.random() * machines.length)],
        supply: ["water", "milk", "coffee_beans", "sugar"][Math.floor(Math.random() * 4)],
        level: Math.floor(Math.random() * 20),
        message: "Supply level is critically low",
      });
    }
  }

  isConnectedToBroker() { return this._isConnected; }
}

export const mqttClient = new MQTTClient();

import React from "react";
export const useMQTTSubscription = (topic, handler) => {
  React.useEffect(() => {
    mqttClient.subscribe(topic, handler);
    return () => mqttClient.unsubscribe(topic, handler);
  }, [topic, handler]);
};

export const initializeMQTT = async () => {
  try { await mqttClient.connect(); return true; } catch { return false; }
};
