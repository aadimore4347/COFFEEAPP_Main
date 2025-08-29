// Data persistence management for coffee machines (JavaScript)
// Handles localStorage persistence and data synchronization

import { STORAGE_KEYS, SUPPLY_TYPES } from "@/config";
import { generateDemoMachines } from "@/config/machines";

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

if (typeof window !== 'undefined') {
  window.debugCoffeeMachines = () => {
    const dm = dataManager;
    dm.debugStorageState();
    return {
      local: dm.getAllMachines(),
      shared: dm.getAllMachinesFromSharedStorage(),
      refresh: dm.refreshSharedStorage
    };
  };
  console.log('🔧 DEBUG: Use window.debugCoffeeMachines() to debug storage state');
  console.log('🧪 TESTING: Persistence test utilities available');
}

export const dataManager = {
  saveMachine: (machine) => {
    const existingMachines = dataManager.getAllMachines();
    const updatedMachines = existingMachines.filter(m => {
      const matchesId = m.id === machine.id;
      const matchesMachineId = machine.machineId && m.machineId === machine.machineId;
      return !matchesId && !matchesMachineId;
    });

    const normalized = normalizeMachine(machine);
    updatedMachines.push(normalized);

    console.log(`💾 DataManager: Saving machine ${machine.id}${machine.machineId ? ` (${machine.machineId})` : ''} to localStorage and shared storage`);

    localStorage.setItem(STORAGE_KEYS.MACHINES, JSON.stringify(updatedMachines));
    localStorage.setItem(STORAGE_KEYS.LAST_SYNC, new Date().toISOString());

    dataManager.syncToSharedStorage(normalized);
  },

  getAllMachines: () => {
    try {
      const stored = localStorage.getItem(STORAGE_KEYS.MACHINES);
      const parsed = stored ? JSON.parse(stored) : [];
      return Array.isArray(parsed) ? parsed.map(normalizeMachine) : [];
    } catch (error) {
      console.warn('Failed to load machines from localStorage:', error);
      return [];
    }
  },

  getMachine: (id) => {
    const sharedMachine = dataManager.getMachineFromSharedStorage(id);
    if (sharedMachine) {
      console.log(`✅ DataManager: Found machine "${id}" in shared storage - status: ${sharedMachine.status}`);
      return sharedMachine;
    }

    const machines = dataManager.getAllMachines();
    const localMachine = machines.find(m => m.id === id || m.machineId === id);
    if (localMachine) {
      const normalized = normalizeMachine(localMachine);
      console.log(`📋 DataManager: Found machine "${id}" in local storage, will sync to shared storage`);
      dataManager.syncToSharedStorage(normalized);
      return normalized;
    }

    console.log(`❌ DataManager: Machine "${id}" not found in any storage`);
    return null;
  },

  updateMachineSupplies: (id, supplies) => {
    const machines = dataManager.getAllMachines();
    const machineIndex = machines.findIndex(m => m.id === id || m.machineId === id);

    if (machineIndex !== -1) {
      const mergedSupplies = normalizeSupplies({
        ...machines[machineIndex].supplies,
        ...supplies,
      });
      machines[machineIndex].supplies = mergedSupplies;
      localStorage.setItem(STORAGE_KEYS.MACHINES, JSON.stringify(machines));

      dataManager.syncToSharedStorage(machines[machineIndex]);
      console.log(`🔄 SUPPLY REFILL: Machine ${id} supplies synced to shared storage`);
    }
  },

  updateMachine: (id, updates) => {
    let currentMachine = dataManager.getMachineFromSharedStorage(id);
    if (!currentMachine) {
      const machines = dataManager.getAllMachines();
      currentMachine = machines.find(m => m.id === id || m.machineId === id);
    }

    if (currentMachine) {
      const updated = normalizeMachine({
        ...currentMachine,
        ...updates,
        lastUpdated: new Date().toISOString(),
      });

      console.log(`🔄 MACHINE UPDATE: Updating machine ${id} - status: ${currentMachine.status} → ${updated.status}`);

      const machines = dataManager.getAllMachines();
      const machineIndex = machines.findIndex(m => m.id === id || m.machineId === id);
      if (machineIndex !== -1) {
        machines[machineIndex] = updated;
        localStorage.setItem(STORAGE_KEYS.MACHINES, JSON.stringify(machines));
      }

      dataManager.syncToSharedStorage(updated);
      console.log(`✅ MACHINE UPDATE: Machine ${id} successfully updated and synced`);
    } else {
      console.error(`❌ MACHINE UPDATE: Machine ${id} not found for update`);
    }
  },

  removeMachine: (id) => {
    const machines = dataManager.getAllMachines();
    const filteredMachines = machines.filter(m => m.id !== id && m.machineId !== id);
    localStorage.setItem(STORAGE_KEYS.MACHINES, JSON.stringify(filteredMachines));

    const sharedMachines = JSON.parse(localStorage.getItem(STORAGE_KEYS.SHARED_MACHINES) || '[]');
    const filteredSharedMachines = sharedMachines.filter((m) => m.id !== id && m.machineId !== id);
    localStorage.setItem(STORAGE_KEYS.SHARED_MACHINES, JSON.stringify(filteredSharedMachines));
  },

  clearAll: () => {
    localStorage.removeItem(STORAGE_KEYS.MACHINES);
    localStorage.removeItem(STORAGE_KEYS.LAST_SYNC);
  },

  getLastSync: () => {
    const stored = localStorage.getItem(STORAGE_KEYS.LAST_SYNC);
    return stored ? new Date(stored) : null;
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

  syncToSharedStorage: (machine) => {
    try {
      const stored = localStorage.getItem(STORAGE_KEYS.SHARED_MACHINES);
      let sharedMachines = stored ? JSON.parse(stored) : [];
      if (!Array.isArray(sharedMachines)) {
        sharedMachines = [];
      }

      const beforeCount = sharedMachines.length;
      sharedMachines = sharedMachines.filter((m) => {
        const matchesId = m.id === machine.id;
        const matchesMachineId = machine.machineId && m.machineId === machine.machineId;
        return !matchesId && !matchesMachineId;
      });
      const afterCount = sharedMachines.length;

      const machineWithTimestamp = {
        ...machine,
        lastUpdated: new Date().toISOString(),
        updatedBy: 'technician',
      };
      sharedMachines.push(machineWithTimestamp);

      localStorage.setItem(STORAGE_KEYS.SHARED_MACHINES, JSON.stringify(sharedMachines));

      const action = beforeCount === afterCount ? 'ADDED' : 'UPDATED';
      console.log(`🔄 TECHNICIAN UPDATE: ${action} machine ${machine.id} (status: ${machine.status}) to shared storage - Admin will see this change!`);
      console.log(`📊 Storage stats: ${beforeCount} -> ${sharedMachines.length} machines in shared storage`);

      window.dispatchEvent(new CustomEvent('machineDataChanged', {
        detail: { machine: machineWithTimestamp, action }
      }));
    } catch (error) {
      console.warn('Failed to sync machine to shared storage:', error);
    }
  },

  getMachineFromSharedStorage: (id) => {
    try {
      const stored = localStorage.getItem(STORAGE_KEYS.SHARED_MACHINES);
      if (!stored) return null;

      const sharedMachines = JSON.parse(stored);
      if (!Array.isArray(sharedMachines)) return null;

      const found = sharedMachines.find((m) => m.id === id || m.machineId === id);
      return found ? normalizeMachine(found) : null;
    } catch (error) {
      console.warn('Failed to get machine from shared storage:', error);
      return null;
    }
  },

  getAllMachinesFromSharedStorage: () => {
    try {
      const stored = localStorage.getItem(STORAGE_KEYS.SHARED_MACHINES);
      if (!stored) {
        console.log('🚀 INITIALIZING: Setting up shared storage with demo machines for cross-user sync');
        const demoMachines = generateDemoMachines();

        const normalizedDemoMachines = demoMachines.map(machine => ({
          ...machine,
          electricityStatus: machine.electricityStatus || "available",
          alerts: machine.alerts || [],
          recentRefills: machine.recentRefills || [],
          lastUpdated: new Date().toISOString(),
          updatedBy: 'system'
        }));

        localStorage.setItem(STORAGE_KEYS.SHARED_MACHINES, JSON.stringify(normalizedDemoMachines));
        console.log(`✅ INITIALIZED: Created ${normalizedDemoMachines.length} demo machines in shared storage`);
        return normalizedDemoMachines.map(normalizeMachine);
      }

      const sharedMachines = JSON.parse(stored);
      if (!Array.isArray(sharedMachines)) {
        console.warn('⚠️ SHARED STORAGE: Invalid data format, creating default data...');
        localStorage.removeItem(STORAGE_KEYS.SHARED_MACHINES);
        const demoMachines = generateDemoMachines();
        const normalizedDemoMachines = demoMachines.map(machine => ({
          ...machine,
          electricityStatus: machine.electricityStatus || "available",
          alerts: machine.alerts || [],
          recentRefills: machine.recentRefills || [],
          lastUpdated: new Date().toISOString(),
          updatedBy: 'system'
        }));
        localStorage.setItem(STORAGE_KEYS.SHARED_MACHINES, JSON.stringify(normalizedDemoMachines));
        return normalizedDemoMachines.map(normalizeMachine);
      }

      const machines = sharedMachines.map(normalizeMachine);

      // Ensure storage is updated with normalized shape (backfill usage/supplies)
      try {
        localStorage.setItem(STORAGE_KEYS.SHARED_MACHINES, JSON.stringify(machines));
      } catch {}

      console.log(`📊 SHARED STORAGE: Loaded ${machines.length} machines`);
      machines.forEach(m => {
        console.log(`  - ${m.id}: ${m.status} (power: ${m.powerStatus}, electricity: ${m.electricityStatus || 'N/A'})`);
      });

      return machines;
    } catch (error) {
      console.error('Failed to get machines from shared storage:', error);
      localStorage.removeItem(STORAGE_KEYS.SHARED_MACHINES);
      try {
        const demoMachines = generateDemoMachines();
        const normalizedDemoMachines = demoMachines.map(machine => ({
          ...machine,
          electricityStatus: machine.electricityStatus || "available",
          alerts: machine.alerts || [],
          recentRefills: machine.recentRefills || [],
          lastUpdated: new Date().toISOString(),
          updatedBy: 'system'
        }));
        localStorage.setItem(STORAGE_KEYS.SHARED_MACHINES, JSON.stringify(normalizedDemoMachines));
        console.log('🔄 RECOVERY: Created fallback demo machines after error');
        return normalizedDemoMachines.map(normalizeMachine);
      } catch (fallbackError) {
        console.error('Failed to create fallback data:', fallbackError);
        return [];
      }
    }
  },

  refreshSharedStorage: () => {
    console.log('🔄 FORCE REFRESH: Manually refreshing shared storage...');
    window.dispatchEvent(new CustomEvent('forceRefreshMachines'));
  },

  debugStorageState: () => {
    console.log('=== STORAGE DEBUG STATE ===');
    const localMachines = dataManager.getAllMachines();
    const sharedMachines = dataManager.getAllMachinesFromSharedStorage();
    console.log(`Local machines: ${localMachines.length}`);
    console.log(`Shared machines: ${sharedMachines.length}`);
    console.log('Local:', localMachines.map(m => `${m.id}:${m.status}`));
    console.log('Shared:', sharedMachines.map(m => `${m.id}:${m.status}`));
    console.log('============================');
  },

  initialize: () => {
    console.log('🚀 DataManager: Initializing data persistence system...');

    dataManager.getAllMachinesFromSharedStorage();

    if (typeof window !== 'undefined') {
      window.addEventListener('storage', (e) => {
        if (e.key === STORAGE_KEYS.SHARED_MACHINES && e.newValue !== e.oldValue) {
          console.log('🔄 DataManager: Detected cross-tab storage change');
          window.dispatchEvent(new CustomEvent('machineDataChanged', {
            detail: { source: 'cross-tab', action: 'storage-update' }
          }));
        }
      });

      setInterval(() => {
        try {
          const sharedCount = dataManager.getAllMachinesFromSharedStorage().length;
          if (sharedCount === 0) {
            console.warn('⚠️ DataManager: Shared storage appears empty, reinitializing...');
            dataManager.getAllMachinesFromSharedStorage();
          }
        } catch (error) {
          console.error('DataManager: Periodic sync error:', error);
        }
      }, 10000);

      console.log('✅ DataManager: Initialization complete with cross-tab sync and periodic checks');
    }
  },

  ensureUserDataPersistence: (userRole, officeName) => {
    console.log(`👥 DataManager: Ensuring data persistence for ${userRole}${officeName ? ` in ${officeName}` : ''}`);

    const sharedMachines = dataManager.getAllMachinesFromSharedStorage();

    if (userRole === 'technician' && officeName) {
      const officeMachines = sharedMachines.filter(m => m.office === officeName);
      console.log(`✅ Technician ${officeName}: ${officeMachines.length} machines available`);

      if (officeMachines.length === 0) {
        console.log(`📦 No machines found for ${officeName}, this may be normal for new offices`);
      }
    } else if (userRole === 'admin') {
      console.log(`✅ Admin: ${sharedMachines.length} total machines across all offices`);
      const officeBreakdown = sharedMachines.reduce((acc, machine) => {
        acc[machine.office] = (acc[machine.office] || 0) + 1;
        return acc;
      }, {});
      console.log('Office breakdown:', officeBreakdown);
    }
  },
};
