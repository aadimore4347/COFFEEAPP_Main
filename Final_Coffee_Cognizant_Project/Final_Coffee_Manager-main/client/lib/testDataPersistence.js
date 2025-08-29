// Test file to verify machine data persistence fix
// This can be used during development to verify the bug is fixed
import { dataManager } from './dataManager';
export const testDataPersistence = ()=>{
    console.log('🧪 Testing data persistence fix...');
    // Clear any existing test data
    dataManager.clearAll();
    // Create a test machine with both id and machineId
    const testMachine = {
        id: "TEST-001",
        machineId: "VM-TEST-001",
        name: "Test Coffee Machine",
        location: "Test Location",
        office: "Test Office",
        floor: "Floor 1",
        status: "operational",
        powerStatus: "online",
        electricityStatus: "available",
        lastPowerUpdate: new Date().toISOString(),
        lastMaintenance: "2024-01-01",
        nextMaintenance: "2024-02-01",
        supplies: {
            water: 50,
            milk: 50,
            coffee: 50,
            coffeeBeans: 50,
            sugar: 50
        },
        maintenance: {
            filterStatus: "good",
            cleaningStatus: "clean",
            pressure: 15
        },
        usage: {
            dailyCups: 100,
            weeklyCups: 700
        },
        notes: "Original notes",
        alerts: [],
        recentRefills: []
    };
    // Save the machine
    console.log('💾 Saving test machine...');
    dataManager.saveMachine(testMachine);
    // Load by id
    const loadedById = dataManager.getMachine("TEST-001");
    console.log('🔍 Loaded by id:', loadedById?.notes);
    // Load by machineId  
    const loadedByMachineId = dataManager.getMachine("VM-TEST-001");
    console.log('🔍 Loaded by machineId:', loadedByMachineId?.notes);
    // Update the machine (simulating MachineManagement save)
    const updatedMachine = {
        ...testMachine,
        notes: "Updated notes - changes should persist!",
        supplies: {
            ...testMachine.supplies,
            water: 80
        }
    };
    console.log('💾 Saving updated machine...');
    dataManager.saveMachine(updatedMachine);
    // Load again by both ids to verify persistence
    const reloadedById = dataManager.getMachine("TEST-001");
    const reloadedByMachineId = dataManager.getMachine("VM-TEST-001");
    console.log('✅ Final check - loaded by id:', reloadedById?.notes, 'water:', reloadedById?.supplies.water);
    console.log('✅ Final check - loaded by machineId:', reloadedByMachineId?.notes, 'water:', reloadedByMachineId?.supplies.water);
    // Verify no duplicates
    const allMachines = dataManager.getAllMachines();
    const testMachines = allMachines.filter((m)=>m.id === "TEST-001" || m.machineId === "VM-TEST-001");
    console.log(`🔢 Number of test machines found: ${testMachines.length} (should be 1)`);
    if (testMachines.length === 1 && testMachines[0].notes === "Updated notes - changes should persist!" && testMachines[0].supplies.water === 80) {
        console.log('✅ Test PASSED - Data persistence is working correctly!');
        return true;
    } else {
        console.log('❌ Test FAILED - Data persistence issue still exists');
        return false;
    }
};
// Export for use in browser console during testing
if (typeof window !== 'undefined') {
    window.testDataPersistence = testDataPersistence;
}
