// Verification script to test that machine status changes persist and sync across users
import { dataManager } from './dataManager';
export const verifyStatusPersistence = ()=>{
    console.log('🧪 Testing Status Persistence...');
    // Step 1: Get machines from shared storage
    const machines = dataManager.getAllMachinesFromSharedStorage();
    console.log(`📊 Found ${machines.length} machines in shared storage`);
    if (machines.length === 0) {
        console.log('❌ No machines found in shared storage!');
        return false;
    }
    // Step 2: Simulate technician setting a machine offline
    const testMachine = machines[0];
    const originalStatus = testMachine.status;
    console.log(`🔧 Testing with machine: ${testMachine.id} (current status: ${originalStatus})`);
    // Simulate technician update
    const updatedMachine = {
        ...testMachine,
        status: 'offline',
        powerStatus: 'offline',
        electricityStatus: 'unavailable',
        lastPowerUpdate: new Date().toLocaleString(),
        office: testMachine.office || 'Test Office' // Ensure office exists
    };
    console.log('🔧 TECHNICIAN: Setting machine to offline...');
    dataManager.saveMachine(updatedMachine);
    // Step 3: Verify admin can see the change
    const adminView = dataManager.getAllMachinesFromSharedStorage();
    const updatedFromAdmin = adminView.find((m)=>m.id === testMachine.id);
    if (updatedFromAdmin && updatedFromAdmin.status === 'offline') {
        console.log('✅ SUCCESS: Status change persisted and visible to admin!');
        console.log(`✅ Machine ${testMachine.id} status: ${originalStatus} → ${updatedFromAdmin.status}`);
        // Restore original status
        const restoredMachine = {
            ...updatedMachine,
            status: originalStatus
        };
        dataManager.saveMachine(restoredMachine);
        console.log(`🔄 Restored machine ${testMachine.id} to original status: ${originalStatus}`);
        return true;
    } else {
        console.log('❌ FAILED: Status change not visible to admin!');
        console.log('��� Expected: offline, Got:', updatedFromAdmin?.status);
        return false;
    }
};
// Test the merge logic specifically
export const testMergeLogic = ()=>{
    console.log('🧪 Testing Merge Logic...');
    // Get current machines
    const sharedMachines = dataManager.getAllMachinesFromSharedStorage();
    console.log(`📊 Shared storage has ${sharedMachines.length} machines`);
    // Test that machines with same ID get replaced, not duplicated
    if (sharedMachines.length > 0) {
        const testMachine = sharedMachines[0];
        const timestamp = Date.now();
        // Update the same machine with a unique note
        const updatedMachine = {
            ...testMachine,
            status: 'offline',
            notes: `Test update ${timestamp}`
        };
        dataManager.saveMachine(updatedMachine);
        // Verify no duplicates and update persisted
        const afterUpdate = dataManager.getAllMachinesFromSharedStorage();
        const machinesWithSameId = afterUpdate.filter((m)=>m.id === testMachine.id);
        const updatedMachineFromStorage = afterUpdate.find((m)=>m.id === testMachine.id);
        if (machinesWithSameId.length === 1 && updatedMachineFromStorage?.notes?.includes(`Test update ${timestamp}`)) {
            console.log('✅ SUCCESS: Machine updated without duplication');
            return true;
        } else {
            console.log('❌ FAILED: Machine duplication or update failed');
            console.log(`❌ Found ${machinesWithSameId.length} machines with same ID`);
            return false;
        }
    }
    return false;
};
// Expose to window for manual testing
if (typeof window !== 'undefined') {
    window.verifyStatusPersistence = verifyStatusPersistence;
    window.testMergeLogic = testMergeLogic;
    // Auto-run on load for verification
    setTimeout(()=>{
        console.log('🚀 Auto-running status persistence verification...');
        verifyStatusPersistence();
        testMergeLogic();
    }, 2000);
}
