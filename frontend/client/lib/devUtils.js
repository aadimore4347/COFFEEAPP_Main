// Development utilities for testing and debugging
// These utilities are only available in development mode
import { testDataPersistence } from './testDataPersistence';
import { testCrossUserSync } from './testCrossUserSync';
import { testDynamicConfiguration, testConfigurationScenarios } from './testDynamicConfig';
// Make test functions available in browser console during development
if (import.meta.env.DEV && typeof window !== 'undefined') {
    window.devUtils = {
        // Core functionality tests
        testDataPersistence,
        testCrossUserSync,
        testDynamicConfiguration,
        testConfigurationScenarios,
        // Helper to inspect localStorage data
        inspectStoredMachines: ()=>{
            const stored = localStorage.getItem('coffee_machines');
            const machines = stored ? JSON.parse(stored) : [];
            console.log('📦 Stored machines in localStorage:', machines);
            return machines;
        },
        // Helper to inspect shared storage data
        inspectSharedMachines: ()=>{
            const stored = localStorage.getItem('coffee_shared_machines');
            const machines = stored ? JSON.parse(stored) : [];
            console.log('🔄 Shared machines in localStorage:', machines);
            return machines;
        },
        // Helper to clear all stored data
        clearStoredData: ()=>{
            localStorage.removeItem('coffee_machines');
            localStorage.removeItem('coffee_shared_machines');
            localStorage.removeItem('coffee_last_sync');
            console.log('🗑️ Cleared all stored coffee machine data');
        },
        // Helper to run all tests
        runAllTests: ()=>{
            console.log('🧪 Running all tests...');
            const results = {
                dataPersistence: testDataPersistence(),
                crossUserSync: testCrossUserSync(),
                dynamicConfiguration: testDynamicConfiguration()
            };
            console.log('\n📊 Overall Test Results:');
            Object.entries(results).forEach(([test, passed])=>{
                console.log(`${passed ? '✅' : '❌'} ${test}: ${passed ? 'PASSED' : 'FAILED'}`);
            });
            const allPassed = Object.values(results).every((result)=>result);
            console.log(`\n🎯 Overall Status: ${allPassed ? 'ALL TESTS PASSED' : 'SOME TESTS FAILED'}`);
            if (allPassed) {
                console.log('🎉 System is fully functional and properly configured!');
            }
            return results;
        }
    };
    console.log('🛠️ Development utilities loaded. Available in window.devUtils:');
    console.log('  - testDataPersistence(): Test the data persistence fix');
    console.log('  - testCrossUserSync(): Test cross-user data synchronization');
    console.log('  - testDynamicConfiguration(): Test dynamic configuration system');
    console.log('  - testConfigurationScenarios(): Test configuration scenarios');
    console.log('  - inspectStoredMachines(): View stored machines');
    console.log('  - inspectSharedMachines(): View shared machines');
    console.log('  - clearStoredData(): Clear all stored data');
    console.log('  - runAllTests(): Run all tests at once');
}
export { testDataPersistence, testCrossUserSync, testDynamicConfiguration, testConfigurationScenarios };
