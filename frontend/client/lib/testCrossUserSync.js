// Test utility to verify cross-user data synchronization
// This simulates the scenario where technician updates are visible to admin
import { dataManager } from './dataManager';
export const testCrossUserSync = ()=>{
    console.log('🧪 Testing cross-user data synchronization...');
    // Clear all storage first
    localStorage.removeItem('coffee_machines');
    localStorage.removeItem('coffee_shared_machines');
    // Simulate technician (Diksha) making changes to Hinjewadi machine
    console.log('\n👩‍🔧 TECHNICIAN SCENARIO: Diksha updates machine HIJ-001');
    // Get the machine (simulating technician opening machine details)
    let machine = dataManager.getMachine('HIJ-001');
    if (!machine) {
        console.log('❌ Machine HIJ-001 not found in data');
        return false;
    }
    console.log('📋 Original machine state:', {
        supplies: machine.supplies,
        status: machine.status,
        electricityStatus: machine.electricityStatus,
        notes: machine.notes
    });
    // Simulate technician making updates
    const technicianUpdates = {
        ...machine,
        // Refill supplies
        supplies: {
            ...machine.supplies,
            water: 95,
            coffee: 90,
            milk: 85 // Refilled milk
        },
        // Change status
        status: 'maintenance',
        electricityStatus: 'unavailable',
        // Add notes
        notes: 'Machine taken offline for maintenance by Diksha. Supplies refilled.',
        // Add recent refill
        recentRefills: [
            {
                id: 'refill-test-' + Date.now(),
                supplyType: 'water',
                supplyName: 'Water Tank',
                amount: 95,
                refillAmount: 10,
                timestamp: new Date().toISOString(),
                technician: 'Diksha',
                timeAgo: 'Just now'
            }
        ]
    };
    // Save the updates (this should sync to shared storage)
    console.log('💾 Technician saving updates...');
    dataManager.saveMachine(technicianUpdates);
    console.log('✅ Technician updates saved');
    console.log('📋 Updated machine state:', {
        supplies: technicianUpdates.supplies,
        status: technicianUpdates.status,
        electricityStatus: technicianUpdates.electricityStatus,
        notes: technicianUpdates.notes
    });
    // Simulate admin logging in and viewing the same machine
    console.log('\n👨‍💼 ADMIN SCENARIO: Admin views machine HIJ-001');
    // Admin gets the machine (should see technician's updates)
    const adminViewedMachine = dataManager.getMachine('HIJ-001');
    if (!adminViewedMachine) {
        console.log('❌ Admin cannot find machine HIJ-001');
        return false;
    }
    console.log('👀 Admin sees machine state:', {
        supplies: adminViewedMachine.supplies,
        status: adminViewedMachine.status,
        electricityStatus: adminViewedMachine.electricityStatus,
        notes: adminViewedMachine.notes
    });
    // Verify that admin sees technician's updates
    const adminSeesUpdates = adminViewedMachine.supplies.water === 95 && adminViewedMachine.supplies.coffee === 90 && adminViewedMachine.status === 'maintenance' && adminViewedMachine.electricityStatus === 'unavailable' && adminViewedMachine.notes.includes('Diksha');
    if (adminSeesUpdates) {
        console.log('✅ SUCCESS: Admin can see all technician updates!');
        console.log('  ✓ Supply levels updated');
        console.log('  ✓ Machine status changed to maintenance');
        console.log('  ✓ Electricity status changed to unavailable');
        console.log('  ✓ Notes include technician name');
        return true;
    } else {
        console.log('❌ FAILURE: Admin cannot see technician updates');
        console.log('Expected water: 95, got:', adminViewedMachine.supplies.water);
        console.log('Expected status: maintenance, got:', adminViewedMachine.status);
        console.log('Expected electricity: unavailable, got:', adminViewedMachine.electricityStatus);
        return false;
    }
};
// Export for use in browser console during testing
if (typeof window !== 'undefined') {
    window.testCrossUserSync = testCrossUserSync;
}
