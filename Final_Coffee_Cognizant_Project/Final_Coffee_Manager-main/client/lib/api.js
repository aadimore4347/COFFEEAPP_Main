// API Configuration and JWT Token Management (JavaScript)

import { ENV_CONFIG, STORAGE_KEYS, API_ENDPOINTS, ALERT_THRESHOLDS, USER_ROLES } from "@/config";
import { generateDemoMachines } from "@/config/machines";

const API_BASE_URL = ENV_CONFIG.API_BASE_URL;
const STANDALONE_MODE = ENV_CONFIG.STANDALONE_MODE;
const DEBUG = ENV_CONFIG.DEBUG;

export const tokenManager = {
  getToken: () => localStorage.getItem(STORAGE_KEYS.AUTH_TOKEN),
  setToken: (token) => localStorage.setItem(STORAGE_KEYS.AUTH_TOKEN, token),
  removeToken: () => {
    localStorage.removeItem(STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(STORAGE_KEYS.AUTH_USER);
  },
  isTokenExpired: (token) => {
    try {
      const payload = JSON.parse(atob(token.split(".")[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp < currentTime;
    } catch {
      return true;
    }
  },
};

const SHARED_MACHINES_KEY = STORAGE_KEYS.SHARED_MACHINES;
const getDefaultMachineData = () => generateDemoMachines();

// Local normalizers to avoid circular imports
const normalizeSupplies = (supplies = {}) => {
  const coffeeLevel = supplies.coffeeBeans ?? supplies.coffee ?? 0;
  return {
    water: supplies.water ?? 0,
    milk: supplies.milk ?? 0,
    sugar: supplies.sugar ?? 0,
    coffeeBeans: coffeeLevel,
    coffee: coffeeLevel,
  };
};
const normalizeMachine = (m) => ({
  ...m,
  supplies: normalizeSupplies(m?.supplies || {}),
  usage: {
    dailyCups: m?.usage?.dailyCups ?? 0,
    weeklyCups: m?.usage?.weeklyCups ?? 0,
  },
});

const sharedDataManager = {
  getMachines: () => {
    try {
      const stored = localStorage.getItem(SHARED_MACHINES_KEY);
      if (!stored) {
        const defaultData = getDefaultMachineData().map(normalizeMachine);
        console.log('🔧 Initializing shared machine data for standalone mode');
        localStorage.setItem(SHARED_MACHINES_KEY, JSON.stringify(defaultData));
        return defaultData;
      }
      const parsed = JSON.parse(stored);
      return Array.isArray(parsed) ? parsed.map(normalizeMachine) : [];
    } catch (error) {
      console.warn('Failed to load shared machines data:', error);
      return getDefaultMachineData().map(normalizeMachine);
    }
  },
  saveMachines: (machines) => {
    try {
      const normalized = (machines || []).map(normalizeMachine);
      localStorage.setItem(SHARED_MACHINES_KEY, JSON.stringify(normalized));
      console.log('💾 Shared data: Saved machines to shared storage');
    } catch (error) {
      console.error('Failed to save machines to shared storage:', error);
    }
  },
  updateMachine: (id, updateData) => {
    const machines = sharedDataManager.getMachines();
    const machineIndex = machines.findIndex((m) => m.id === id || m.machineId === id);
    if (machineIndex !== -1) {
      machines[machineIndex] = normalizeMachine({ ...machines[machineIndex], ...updateData });
      sharedDataManager.saveMachines(machines);
      console.log(`💾 Shared data: Updated machine ${id} in shared storage`);
      return machines[machineIndex];
    }
    throw new Error('Machine not found in shared storage');
  },
  updateMachineSupplies: (id, supplies) => {
    const machines = sharedDataManager.getMachines();
    const machineIndex = machines.findIndex((m) => m.id === id || m.machineId === id);
    if (machineIndex !== -1) {
      machines[machineIndex].supplies = normalizeSupplies({ ...machines[machineIndex].supplies, ...supplies });
      machines[machineIndex] = normalizeMachine(machines[machineIndex]);
      sharedDataManager.saveMachines(machines);
      console.log(`💾 Shared data: Updated supplies for machine ${id} in shared storage`);
      return machines[machineIndex];
    }
    throw new Error('Machine not found in shared storage');
  },
  deleteMachine: (id) => {
    const machines = sharedDataManager.getMachines();
    const filteredMachines = machines.filter((m) => m.id !== id && m.machineId !== id);
    if (filteredMachines.length < machines.length) {
      sharedDataManager.saveMachines(filteredMachines);
      console.log(`💾 Shared data: Deleted machine ${id} from shared storage`);
      return true;
    }
    throw new Error('Machine not found in shared storage');
  }
};

class ApiClient {
  constructor(baseURL) {
    this.baseURL = baseURL;
  }

  async makeRequest(endpoint, options = {}) {
    if (STANDALONE_MODE) {
      return this.handleStandaloneRequest(endpoint, options);
    }

    const url = `${this.baseURL}${endpoint}`;
    const token = tokenManager.getToken();
    const headers = {
      "Content-Type": "application/json",
      ...options.headers,
    };

    if (token && !tokenManager.isTokenExpired(token)) {
      headers.Authorization = `Bearer ${token}`;
    }

    try {
      const response = await fetch(url, {
        ...options,
        headers,
        signal: AbortSignal.timeout(ENV_CONFIG.REQUEST_TIMEOUT),
      });

      if (response.status === 401) {
        tokenManager.removeToken();
        throw new Error("Authentication required");
      }

      if (response.status === 501) {
        console.log(`🔄 API endpoint ${endpoint} not implemented, using standalone mode`);
        return this.handleStandaloneRequest(endpoint, options);
      }

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
      }

      return await response.json();
    } catch (error) {
      if (error instanceof TypeError && error.message.includes("fetch")) {
        console.log(`🔄 Backend unavailable for ${endpoint}, using standalone mode`);
        return this.handleStandaloneRequest(endpoint, options);
      }

      if (error instanceof Error && error.name === "AbortError") {
        console.log(`⏱️ Request timeout for ${endpoint}, using standalone mode`);
        return this.handleStandaloneRequest(endpoint, options);
      }

      throw error;
    }
  }

  async handleStandaloneRequest(endpoint, options = {}) {
    await new Promise(resolve => setTimeout(resolve, Math.random() * 500 + 100));

    const method = options.method || 'GET';

    switch (true) {
      case endpoint === API_ENDPOINTS.MACHINES && method === 'GET': {
        const allMachines = sharedDataManager.getMachines();
        console.log('🔄 Shared data: Retrieved all machines from shared storage');
        return allMachines;
      }

      case endpoint.startsWith('/machines/machine/') && method === 'GET': {
        const machineId = endpoint.split('/').pop();
        const machines = sharedDataManager.getMachines();
        const machine = machines.find((m) => m.id === machineId || m.machineId === machineId);
        if (!machine) throw new Error('Machine not found');
        console.log(`🔄 Shared data: Retrieved machine ${machineId} from shared storage`);
        return machine;
      }

      case endpoint.startsWith('/machines/') && method === 'PUT': {
        const updateId = endpoint.split('/')[2];
        const updateData = JSON.parse(options.body);
        try {
          const updatedMachine = sharedDataManager.updateMachine(updateId, updateData);
          console.log(`✅ Shared data: Machine ${updateId} updated in shared storage`);
          return updatedMachine;
        } catch (error) {
          throw new Error('Machine not found');
        }
      }

      case endpoint.includes('/supplies') && method === 'PUT': {
        const supplyId = endpoint.split('/')[2];
        const supplyData = JSON.parse(options.body);
        try {
          sharedDataManager.updateMachineSupplies(supplyId, supplyData.supplies);
          console.log(`✅ Shared data: Supplies updated for machine ${supplyId} in shared storage`);
          return { message: "Supplies updated successfully" };
        } catch (error) {
          throw new Error('Machine not found');
        }
      }

      case endpoint.startsWith('/machines/') && method === 'DELETE': {
        const deleteId = endpoint.split('/')[2];
        try {
          sharedDataManager.deleteMachine(deleteId);
          console.log(`✅ Shared data: Machine ${deleteId} deleted from shared storage`);
          return { message: "Machine deleted successfully" };
        } catch (error) {
          throw new Error('Machine not found');
        }
      }

      case endpoint === API_ENDPOINTS.SIGNIN && method === 'POST': {
        const { username, password } = JSON.parse(options.body);
        if (username && password) {
          const token = btoa(JSON.stringify({
            username,
            exp: Date.now() / 1000 + (ENV_CONFIG.TOKEN_EXPIRY_HOURS * 3600)
          }));
          return {
            accessToken: token,
            tokenType: "Bearer",
            id: 1,
            username,
            name: username.charAt(0).toUpperCase() + username.slice(1),
            role: username.includes('tech') ? USER_ROLES.TECHNICIAN : USER_ROLES.ADMIN,
            authorities: []
          };
        }
        throw new Error('Invalid credentials');
      }

      case endpoint === API_ENDPOINTS.SIGNUP && method === 'POST':
        return { message: "User registered successfully" };

      case endpoint === API_ENDPOINTS.SIGNOUT && method === 'POST':
        return { message: "Signed out successfully" };

      default:
        throw new Error(`Endpoint ${endpoint} not implemented in standalone mode`);
    }
  }

  async login(username, password) {
    return this.makeRequest(API_ENDPOINTS.SIGNIN, {
      method: "POST",
      body: JSON.stringify({ username, password }),
    });
  }

  async signup(username, name, password, officeName) {
    return this.makeRequest(API_ENDPOINTS.SIGNUP, {
      method: "POST",
      body: JSON.stringify({ username, name, password, officeName }),
    });
  }

  async logout() {
    return this.makeRequest(API_ENDPOINTS.SIGNOUT, { method: "POST" });
  }

  async getMachines() {
    return this.makeRequest(API_ENDPOINTS.MACHINES);
  }

  async createMachine(machineData) {
    return this.makeRequest(API_ENDPOINTS.MACHINES, {
      method: "POST",
      body: JSON.stringify(machineData),
    });
  }

  async getMachine(id) {
    return this.makeRequest(API_ENDPOINTS.MACHINE_BY_ID(id));
  }

  async getMachineByMachineId(machineId) {
    return this.makeRequest(API_ENDPOINTS.MACHINE_BY_MACHINE_ID(machineId));
  }

  async updateMachine(id, data) {
    return this.makeRequest(API_ENDPOINTS.MACHINE_BY_ID(id), {
      method: "PUT",
      body: JSON.stringify(data),
    });
  }

  async updateSupplies(id, supplies) {
    return this.makeRequest(API_ENDPOINTS.MACHINE_SUPPLIES(id), {
      method: "PUT",
      body: JSON.stringify(supplies),
    });
  }

  async deleteMachine(id) {
    return this.makeRequest(API_ENDPOINTS.DELETE_MACHINE(id), { method: "DELETE" });
  }

  async getLocations() {
    const machines = sharedDataManager.getMachines();
    const uniqueLocations = [...new Set(machines.map((m) => m.office))];
    return this.makeRequest(API_ENDPOINTS.LOCATIONS).catch(() => uniqueLocations);
  }

  async getOffices(location) {
    const machines = sharedDataManager.getMachines();
    const offices = machines.filter((m) => m.office === location).map((m) => m.office);
    return this.makeRequest(`${API_ENDPOINTS.OFFICES}?location=${encodeURIComponent(location)}`)
      .catch(() => [...new Set(offices)]);
  }

  async getFloors(location, office) {
    const machines = sharedDataManager.getMachines();
    const floors = machines.filter((m) => m.office === office).map((m) => m.floor);
    return this.makeRequest(`${API_ENDPOINTS.FLOORS}?location=${encodeURIComponent(location)}&office=${encodeURIComponent(office)}`)
      .catch(() => [...new Set(floors)]);
  }

  async getMachinesByLocationOfficeFloor(location, office, floor) {
    const machines = sharedDataManager.getMachines();
    const filteredMachines = machines.filter((m) => m.office === office && m.floor === floor);
    return this.makeRequest(`${API_ENDPOINTS.MACHINES_BY_LOCATION}?location=${encodeURIComponent(location)}&office=${encodeURIComponent(office)}&floor=${encodeURIComponent(floor)}`)
      .catch(() => filteredMachines);
  }

  async getLowSupplyMachines(threshold = ALERT_THRESHOLDS.LOW_SUPPLY_WARNING) {
    const machines = sharedDataManager.getMachines();
    const lowSupplyMachines = machines.filter((m) => Object.values(m.supplies).some((supply) => supply < threshold));
    return this.makeRequest(`${API_ENDPOINTS.LOW_SUPPLY_MACHINES}?threshold=${threshold}`)
      .catch(() => lowSupplyMachines);
  }

  async getMaintenanceNeededMachines() {
    const machines = sharedDataManager.getMachines();
    const maintenanceMachines = machines.filter((m) =>
      m.maintenance.filterStatus === 'needs_replacement' ||
      m.maintenance.cleaningStatus === 'needs_cleaning'
    );
    return this.makeRequest(API_ENDPOINTS.MAINTENANCE_NEEDED)
      .catch(() => maintenanceMachines);
  }
}

export const apiClient = new ApiClient(API_BASE_URL);
export const isStandaloneMode = () => STANDALONE_MODE;
export const setStandaloneMode = (enabled) => {
  localStorage.setItem('VITE_STANDALONE_MODE', enabled.toString());
  window.location.reload();
};
