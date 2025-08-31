// Data persistence management for coffee machines (JavaScript)
// Handles ALL data synchronization with the backend.

import backendAPI from './backendApi';
import { SUPPLY_TYPES } from '@/config';


// Normalize supplies to ensure both coffeeBeans (UI) and coffee (backend) are in sync
const normalizeSupplies = (supplies) => {
  const coffeeBeans = (supplies && (supplies.coffeeBeans ?? supplies.coffee)) ?? 0;
  return {
    [SUPPLY_TYPES.WATER]: supplies?.[SUPPLY_TYPES.WATER] ?? 0,
    [SUPPLY_TYPES.MILK]: supplies?.[SUPPLY_TYPES.MILK] ?? 0,
    [SUPPLY_TYPES.SUGAR]: supplies?.[SUPPLY_TYPES.SUGAR] ?? 0,
    [SUPPLY_TYPES.COFFEE_BEANS]: coffeeBeans,
    [SUPPLY_TYPES.COFFEE]: coffeeBeans,
  };
};

const normalizeMachine = (machine) => {
  if (!machine) return machine;
  return {
    ...machine,
    supplies: normalizeSupplies(machine.supplies || {}),
    usage: {
      dailyCups: machine?.usage?.dailyCups ?? 0,
      weeklyCups: machine?.usage?.weeklyCups ?? 0,
    },
  };
};

export const dataManager = {
  saveMachine: async (machine) => {
    const normalized = normalizeMachine(machine);
    return await backendAPI.createMachine(normalized);
  },

  getAllMachines: async () => {
    try {
      const machines = await backendAPI.getMachines();
      return machines.map(normalizeMachine);
    } catch (error) {
      console.error('Failed to load machines from backend:', error);
      return [];
    }
  },

  getMachine: async (id) => {
    try {
      const machine = await backendAPI.getMachineById(id);
      return normalizeMachine(machine);
    } catch (error) {
      console.error(`Failed to load machine ${id} from backend:`, error);
      return null;
    }
  },

  updateMachineSupplies: async (id, supplies) => {
    const machine = await dataManager.getMachine(id);
    if (machine) {
      const mergedSupplies = normalizeSupplies({
        ...machine.supplies,
        ...supplies,
      });
      const updates = { supplies: mergedSupplies };
      return await backendAPI.updateMachine(id, updates);
    }
  },

  updateMachine: async (id, updates) => {
    const normalizedUpdates = normalizeMachine(updates);
    return await backendAPI.updateMachine(id, normalizedUpdates);
  },

  removeMachine: async (id) => {
    return await backendAPI.deleteMachine(id);
  },

  mapBackendToFrontend: (backendData) => {
    const normalizedSupplies = normalizeSupplies(backendData.supplies || {});
    return {
      ...backendData,
      supplies: normalizedSupplies,
      electricityStatus: backendData.electricityStatus || 'available',
      recentRefills: backendData.recentRefills || [],
      alerts: backendData.alerts || [],
    };
  },

  mapFrontendToBackend: (frontendData) => {
    const supplies = normalizeSupplies(frontendData.supplies || {});
    return {
      ...frontendData,
      supplies: {
        [SUPPLY_TYPES.WATER]: supplies[SUPPLY_TYPES.WATER],
        [SUPPLY_TYPES.MILK]: supplies[SUPPLY_TYPES.MILK],
        [SUPPLY_TYPES.SUGAR]: supplies[SUPPLY_TYPES.SUGAR],
        [SUPPLY_TYPES.COFFEE]: supplies[SUPPLY_TYPES.COFFEE],
      },
      electricityStatus: frontendData.electricityStatus || 'available',
      recentRefills: frontendData.recentRefills || [],
      alerts: frontendData.alerts || [],
    };
  },

  initialize: () => {
    console.log('🚀 DataManager: Initializing data persistence system...');
    console.log('✅ DataManager: Initialization complete. All data is now managed by the backend.');
  },
};
