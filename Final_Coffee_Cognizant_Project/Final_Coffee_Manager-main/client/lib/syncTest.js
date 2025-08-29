// Utility to test and verify cross-user data synchronization
import { dataManager } from './dataManager';
export const testSync = ()=>{
    console.log('🧪 Testing cross-user sync...');
    // Test 1: Check if shared storage is working
    const sharedMachines = dataManager.getAllMachinesFromSharedStorage();
    const localMachines = dataManager.getAllMachines();
    console.log(`📊 Shared storage: ${sharedMachines.length} machines`);
    console.log(`📊 Local storage: ${localMachines.length} machines`);
    // Test 2: Simulate technician update
    if (sharedMachines.length > 0) {
        const testMachine = sharedMachines[0];
        const updatedMachine = {
            ...testMachine,
            supplies: {
                ...testMachine.supplies,
                water: 95
            },
            notes: `Updated at ${new Date().toLocaleTimeString()}`
        };
        console.log('🔧 Simulating technician update...');
        dataManager.saveMachine(updatedMachine);
        // Test 3: Verify admin can see the update
        const adminView = dataManager.getAllMachinesFromSharedStorage();
        const updatedFromAdmin = adminView.find((m)=>m.id === testMachine.id);
        if (updatedFromAdmin && updatedFromAdmin.supplies.water === 95) {
            console.log('✅ SUCCESS: Admin can see technician update!');
        } else {
            console.log('❌ FAILED: Admin cannot see technician update');
        }
    }
    return {
        sharedCount: sharedMachines.length,
        localCount: localMachines.length,
        synced: sharedMachines.length > 0
    };
};
// Expose to window for testing in browser console
if (typeof window !== 'undefined') {
    window.testSync = testSync;
}
