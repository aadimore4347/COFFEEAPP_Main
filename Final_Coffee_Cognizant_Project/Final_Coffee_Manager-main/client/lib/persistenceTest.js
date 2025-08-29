// Utility to test data persistence across login/logout cycles
// This helps verify that technician changes persist and are visible to admins
import { dataManager } from './dataManager';
export const persistenceTest = {
    // Simulate a technician changing machine status
    simulateTechnicianStatusChange: (machineId, newStatus)=>{
        console.log('🔧 PERSISTENCE TEST: Simulating technician status change...');
        // Get current machine
        const machine = dataManager.getMachine(machineId);
        if (!machine) {
            console.error(`Machine ${machineId} not found`);
            return false;
        }
        // Update machine status
        const updatedMachine = {
            ...machine,
            status: newStatus,
            powerStatus: newStatus === 'offline' ? 'offline' : 'online',
            electricityStatus: newStatus === 'offline' ? 'unavailable' : 'available',
            lastPowerUpdate: new Date().toLocaleString(),
            office: machine.office || 'Test Office'
        };
        // Save the changes
        dataManager.saveMachine(updatedMachine);
        console.log(`✅ PERSISTENCE TEST: Machine ${machineId} status changed to "${newStatus}"`);
        return true;
    },
    // Verify that changes persist after logout/login simulation
    verifyPersistence: (machineId, expectedStatus)=>{
        console.log('🔍 PERSISTENCE TEST: Verifying data persistence...');
        // Check local storage
        const localMachine = dataManager.getMachine(machineId);
        const sharedMachine = dataManager.getMachineFromSharedStorage(machineId);
        console.log('Local machine status:', localMachine?.status);
        console.log('Shared machine status:', sharedMachine?.status);
        const localMatch = localMachine?.status === expectedStatus;
        const sharedMatch = sharedMachine?.status === expectedStatus;
        console.log(`��� Local storage persistence: ${localMatch ? 'PASS' : 'FAIL'}`);
        console.log(`✅ Shared storage persistence: ${sharedMatch ? 'PASS' : 'FAIL'}`);
        return localMatch && sharedMatch;
    },
    // Test complete flow: technician changes -> admin sees changes
    testCrossUserVisibility: (machineId)=>{
        console.log('🔄 PERSISTENCE TEST: Testing cross-user visibility...');
        const originalMachine = dataManager.getMachine(machineId);
        const originalStatus = originalMachine?.status;
        // Simulate technician making a change
        const newStatus = originalStatus === 'operational' ? 'offline' : 'operational';
        console.log(`📝 Technician changing machine from "${originalStatus}" to "${newStatus}"`);
        this.simulateTechnicianStatusChange(machineId, newStatus);
        // Simulate admin checking (loading from shared storage)
        const adminView = dataManager.getMachineFromSharedStorage(machineId);
        const adminSeesCorrectStatus = adminView?.status === newStatus;
        console.log(`👥 Admin sees status: "${adminView?.status}"`);
        console.log(`✅ Cross-user visibility: ${adminSeesCorrectStatus ? 'PASS' : 'FAIL'}`);
        // Restore original status
        if (originalMachine) {
            dataManager.saveMachine({
                ...originalMachine,
                status: originalStatus
            });
        }
        return adminSeesCorrectStatus;
    },
    // Run all persistence tests
    runAllTests: ()=>{
        console.log('🧪 RUNNING PERSISTENCE TESTS...');
        // Test with a known machine ID
        const testMachineId = 'HIJ-001';
        const test1 = persistenceTest.simulateTechnicianStatusChange(testMachineId, 'offline');
        const test2 = persistenceTest.verifyPersistence(testMachineId, 'offline');
        const test3 = persistenceTest.testCrossUserVisibility(testMachineId);
        const allPassed = test1 && test2 && test3;
        console.log(`🎯 PERSISTENCE TEST RESULTS: ${allPassed ? 'ALL TESTS PASSED ✅' : 'SOME TESTS FAILED ❌'}`);
        return allPassed;
    },
    // Debug current storage state
    debugStorageState: ()=>{
        console.log('🔍 DEBUGGING STORAGE STATE...');
        dataManager.debugStorageState();
        // Also show some sample machine states
        const sampleMachines = [
            'HIJ-001',
            'KOR-001',
            'VIM-001'
        ];
        sampleMachines.forEach((id)=>{
            const local = dataManager.getMachine(id);
            const shared = dataManager.getMachineFromSharedStorage(id);
            console.log(`${id}:`, {
                local: local?.status || 'not found',
                shared: shared?.status || 'not found'
            });
        });
    }
};
// Make it available globally for debugging
if (typeof window !== 'undefined') {
    window.testPersistence = persistenceTest;
    console.log('🧪 DEBUG: Use window.testPersistence.runAllTests() to test data persistence');
}
export default persistenceTest;
