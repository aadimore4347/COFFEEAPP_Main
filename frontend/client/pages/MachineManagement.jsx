import React, { useState, useEffect } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Coffee, Droplets, Milk, Candy, Calendar, AlertTriangle, CheckCircle, Save, Eye, Edit3, Plus, Zap, ZapOff, Activity, TrendingUp, Clock, RefreshCw, MapPin, Building } from "lucide-react";
import { useParams } from "react-router-dom";
import SupplyRefillModal from "@/components/SupplyRefillModal";
import UsageChart from "@/components/UsageChart";
import BrewTypeAnalytics from "@/components/BrewTypeAnalytics";
import InteractiveBreadcrumb from "@/components/InteractiveBreadcrumb";
import { pathToOfficeName, officeNameToPath } from "@/lib/officeRouting";
import { apiClient } from "@/lib/api";
import { dataManager } from "@/lib/dataManager";
export default function MachineManagement({ officePath } = {}) {
    const { user } = useAuth();
    const { machineId } = useParams();
    const [refillModalOpen, setRefillModalOpen] = useState(false);
    const [selectedSupply, setSelectedSupply] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [isEditingElectricity, setIsEditingElectricity] = useState(false);
    const [isLoadingMachine, setIsLoadingMachine] = useState(true);
    // Generate dynamic alerts based on machine condition
    const generateDynamicAlerts = (machine)=>{
        const alerts = [];
        // Generate alerts for low supplies (< 20%)
        Object.entries(machine.supplies).forEach(([key, value])=>{
            if (value < 20) {
                const supplyNames = {
                    water: 'Water Tank',
                    milk: 'Milk Container',
                    coffeeBeans: 'Coffee Beans',
                    sugar: 'Sugar Container'
                };
                alerts.push({
                    id: `supply-${key}-${Date.now()}`,
                    type: 'warning',
                    title: `${supplyNames[key]} Running Low`,
                    description: `${value}% remaining - refill recommended`,
                    category: 'supply',
                    resolved: false,
                    createdAt: new Date().toISOString()
                });
            }
        });
        // Electricity status alerts
        if (machine.electricityStatus === 'unavailable') {
            alerts.push({
                id: `electricity-${Date.now()}`,
                type: 'critical',
                title: 'Electricity Unavailable',
                description: 'Machine cannot operate without power supply',
                category: 'system',
                resolved: false,
                createdAt: new Date().toISOString()
            });
        }
        // Maintenance alerts
        if (machine.maintenance.filterStatus === 'needs_replacement') {
            alerts.push({
                id: `filter-${Date.now()}`,
                type: 'warning',
                title: 'Filter Replacement Required',
                description: 'Water filter needs replacement',
                category: 'maintenance',
                resolved: false,
                createdAt: new Date().toISOString()
            });
        }
        if (machine.maintenance.cleaningStatus === 'needs_cleaning') {
            alerts.push({
                id: `cleaning-${Date.now()}`,
                type: 'info',
                title: 'Cleaning Required',
                description: 'Machine needs scheduled cleaning',
                category: 'cleaning',
                resolved: false,
                createdAt: new Date().toISOString()
            });
        }
        return alerts;
    };
    // Get machine data by ID (searches localStorage first, then static data)
    const getMachineDataById = (id)=>{
        // First check localStorage for updated data
        const localMachine = dataManager.getMachine(id);
        if (localMachine) {
            console.log('📦 Found machine in localStorage:', localMachine);
            return localMachine;
        }
        // Fallback to static data
        const allMachines = getAllMachinesData();
        return allMachines.find((m)=>m.id === id) || null;
    };
    // Get all machines from all offices
    const getAllMachinesData = ()=>{
        const allMachines = [];
        Object.values(getOfficeMachinesData()).forEach((machines)=>{
            if (Array.isArray(machines)) {
                allMachines.push(...machines);
            } else {
                allMachines.push(machines);
            }
        });
        return allMachines;
    };
    // Get all office machine data
    const getOfficeMachinesData = ()=>{
        return {
            "Hinjewadi IT Park": [
                {
                    id: "HIJ-001",
                    name: "Coffee Station Alpha",
                    location: "Hinjewadi IT Park - Building A2",
                    status: "operational",
                    powerStatus: "online",
                    electricityStatus: "available",
                    lastPowerUpdate: "2024-01-16 09:30",
                    lastMaintenance: "2024-01-10",
                    nextMaintenance: "2024-02-10",
                    supplies: {
                        water: 85,
                        milk: 60,
                        coffeeBeans: 75,
                        sugar: 90
                    },
                    maintenance: {
                        filterStatus: "good",
                        cleaningStatus: "clean",
                        temperature: 92,
                        pressure: 15
                    },
                    usage: {
                        dailyCups: 127,
                        weeklyCups: 890
                    },
                    notes: "Machine running smoothly. Recent cleaning completed on schedule."
                },
                {
                    id: "HIJ-002",
                    name: "Espresso Hub Beta",
                    location: "Hinjewadi IT Park - Building B1",
                    status: "operational",
                    powerStatus: "online",
                    electricityStatus: "available",
                    lastPowerUpdate: "2024-01-16 08:45",
                    lastMaintenance: "2024-01-12",
                    nextMaintenance: "2024-02-12",
                    supplies: {
                        water: 92,
                        milk: 45,
                        coffeeBeans: 88,
                        sugar: 76
                    },
                    maintenance: {
                        filterStatus: "good",
                        cleaningStatus: "clean",
                        temperature: 89,
                        pressure: 14
                    },
                    usage: {
                        dailyCups: 98,
                        weeklyCups: 686
                    },
                    notes: "High performance. Minor calibration needed."
                }
            ],
            "Koregaon Park Office": [
                {
                    id: "KOR-001",
                    name: "Executive Espresso",
                    location: "Koregaon Park Office - Ground Floor",
                    status: "operational",
                    powerStatus: "online",
                    electricityStatus: "available",
                    lastPowerUpdate: "2024-01-16 10:15",
                    lastMaintenance: "2024-01-05",
                    nextMaintenance: "2024-02-05",
                    supplies: {
                        water: 78,
                        milk: 80,
                        coffeeBeans: 65,
                        sugar: 95
                    },
                    maintenance: {
                        filterStatus: "good",
                        cleaningStatus: "clean",
                        temperature: 88,
                        pressure: 12
                    },
                    usage: {
                        dailyCups: 89,
                        weeklyCups: 650
                    },
                    notes: "Popular machine. Consistent performance."
                }
            ]
        };
    };
    // Get office-specific machine data (legacy function)
    const getOfficeMachineData = ()=>{
        const officeMachines = {
            "Hinjewadi IT Park": {
                id: "HIJ-001",
                name: "Coffee Station Alpha",
                location: "Hinjewadi IT Park - Building A2",
                status: "operational",
                powerStatus: "online",
                lastPowerUpdate: "2024-01-16 09:30",
                lastMaintenance: "2024-01-10",
                nextMaintenance: "2024-02-10",
                supplies: {
                    water: 85,
                    milk: 60,
                    coffeeBeans: 75,
                    sugar: 90
                },
                maintenance: {
                    filterStatus: "good",
                    cleaningStatus: "clean",
                    temperature: 92,
                    pressure: 15
                },
                usage: {
                    dailyCups: 127,
                    weeklyCups: 890
                },
                notes: "Machine running smoothly. Recent cleaning completed on schedule."
            },
            "Koregaon Park Corporate": {
                id: "KOR-001",
                name: "Espresso Pro",
                location: "Koregaon Park Corporate - Ground Floor",
                status: "maintenance",
                powerStatus: "offline",
                lastPowerUpdate: "2024-01-16 08:15",
                lastMaintenance: "2024-01-05",
                nextMaintenance: "2024-02-05",
                supplies: {
                    water: 45,
                    milk: 80,
                    coffeeBeans: 30,
                    sugar: 95
                },
                maintenance: {
                    filterStatus: "needs_replacement",
                    cleaningStatus: "needs_cleaning",
                    temperature: 88,
                    pressure: 12
                },
                usage: {
                    dailyCups: 89,
                    weeklyCups: 650
                },
                notes: "Filter replacement scheduled for today."
            },
            "Viman Nagar Tech Hub": {
                id: "VIM-001",
                name: "Latte Master",
                location: "Viman Nagar Tech Hub - 3rd Floor",
                status: "operational",
                powerStatus: "online",
                lastPowerUpdate: "2024-01-16 10:45",
                lastMaintenance: "2024-01-12",
                nextMaintenance: "2024-02-12",
                supplies: {
                    water: 90,
                    milk: 45,
                    coffeeBeans: 85,
                    sugar: 70
                },
                maintenance: {
                    filterStatus: "good",
                    cleaningStatus: "clean",
                    temperature: 94,
                    pressure: 16
                },
                usage: {
                    dailyCups: 156,
                    weeklyCups: 1120
                },
                notes: "High usage office. Consider additional machine."
            },
            "Bandra Kurla Complex": {
                id: "BKC-001",
                name: "Cappuccino Deluxe",
                location: "Bandra Kurla Complex - 12th Floor",
                status: "operational",
                powerStatus: "online",
                lastPowerUpdate: "2024-01-16 11:20",
                lastMaintenance: "2024-01-08",
                nextMaintenance: "2024-02-08",
                supplies: {
                    water: 70,
                    milk: 85,
                    coffeeBeans: 60,
                    sugar: 80
                },
                maintenance: {
                    filterStatus: "good",
                    cleaningStatus: "clean",
                    temperature: 91,
                    pressure: 14
                },
                usage: {
                    dailyCups: 98,
                    weeklyCups: 720
                },
                notes: "New installation. Performing well."
            },
            "Andheri Tech Center": {
                id: "AND-001",
                name: "Premium Coffee Station",
                location: "Andheri Tech Center - 8th Floor",
                status: "operational",
                powerStatus: "online",
                lastPowerUpdate: "2024-01-16 09:00",
                lastMaintenance: "2024-01-15",
                nextMaintenance: "2024-02-15",
                supplies: {
                    water: 95,
                    milk: 70,
                    coffeeBeans: 80,
                    sugar: 85
                },
                maintenance: {
                    filterStatus: "good",
                    cleaningStatus: "clean",
                    temperature: 90,
                    pressure: 13
                },
                usage: {
                    dailyCups: 110,
                    weeklyCups: 770
                },
                notes: "Excellent performance. Regular maintenance on track."
            },
            "Lower Parel Financial": {
                id: "LOW-001",
                name: "Business Brew",
                location: "Lower Parel Financial - Tower B",
                status: "operational",
                powerStatus: "online",
                lastPowerUpdate: "2024-01-16 07:45",
                lastMaintenance: "2024-01-14",
                nextMaintenance: "2024-02-14",
                supplies: {
                    water: 88,
                    milk: 65,
                    coffeeBeans: 72,
                    sugar: 92
                },
                maintenance: {
                    filterStatus: "good",
                    cleaningStatus: "clean",
                    temperature: 93,
                    pressure: 15
                },
                usage: {
                    dailyCups: 134,
                    weeklyCups: 945
                },
                notes: "Busy financial district environment. Machine handling well."
            }
        };
        // Determine which office to show
        let targetOffice;
        if (officePath) {
            // Convert path back to office name using proper mapping
            targetOffice = pathToOfficeName(officePath);
        } else if (user?.officeName) {
            targetOffice = user.officeName;
        } else {
            // Default fallback
            targetOffice = "Hinjewadi IT Park";
        }
        const machineData = officeMachines[targetOffice] || officeMachines["Hinjewadi IT Park"];
        // Add default power status and electricity status if not present (for backward compatibility)
        const result = machineData;
        if (!result.powerStatus) {
            result.powerStatus = result.status === "offline" ? "offline" : "online";
            result.lastPowerUpdate = "2024-01-16 10:00";
        }
        if (!result.electricityStatus) {
            result.electricityStatus = "available";
        }
        if (!result.recentRefills) {
            result.recentRefills = [];
        }
        return result;
    };
    const [machineData, setMachineData] = useState(null);
    // Load machine data from dataManager (shared storage first, then fallback)
    useEffect(()=>{
        const loadMachineData = async ()=>{
            setIsLoadingMachine(true);
            if (!machineId) {
                // Legacy behavior for when no machineId is provided
                const officeData = getOfficeMachineData();
                setMachineData(officeData);
                setIsLoadingMachine(false);
                return;
            }
            try {
                // PRIORITY 1: Check shared storage (includes all technician updates)
                let machineData = dataManager.getMachine(machineId);
                if (machineData) {
                    console.log('✅ MACHINE DETAIL: Loaded machine from storage:', machineData.id, 'status:', machineData.status);
                } else {
                    // PRIORITY 2: Try to load from API (for new machines not yet in storage)
                    try {
                        const apiData = await apiClient.getMachineByMachineId(machineId);
                        if (apiData) {
                            machineData = dataManager.mapBackendToFrontend(apiData);
                            console.log('📡 MACHINE DETAIL: Loaded machine from API:', machineData);
                            // Save to storage for future consistency
                            dataManager.saveMachine(machineData);
                        }
                    } catch (apiError) {
                        console.log('API unavailable, trying static fallback:', apiError.message);
                    }
                    // PRIORITY 3: Fallback to static data (only if nothing else works)
                    if (!machineData) {
                        machineData = getMachineDataById(machineId);
                        if (machineData) {
                            console.log('💾 MACHINE DETAIL: Using static fallback data for:', machineId);
                            // Save static data to storage for future consistency
                            dataManager.saveMachine(machineData);
                        }
                    }
                }
                if (machineData) {
                    setMachineData(machineData);
                    // Generate dynamic alerts based on machine condition
                    const dynamicAlerts = generateDynamicAlerts(machineData);
                    const existingAlerts = machineData.alerts || [];
                    const mergedAlerts = [
                        ...existingAlerts,
                        ...dynamicAlerts
                    ];
                    setAlerts(mergedAlerts);
                } else {
                    console.error('MACHINE DETAIL: Could not load machine data for:', machineId);
                }
            } catch (error) {
                console.error("MACHINE DETAIL: Failed to load machine data:", error);
            } finally{
                setIsLoadingMachine(false);
            }
        };
        loadMachineData();
        // Listen for storage changes to detect updates from other tabs/users
        const handleStorageChange = (e)=>{
            if (e.key === 'coffee_shared_machines' && e.newValue !== e.oldValue) {
                console.log('🔄 MACHINE DETAIL: Detected storage change, reloading machine data...');
                loadMachineData();
            }
        };
        const handleMachineDataChanged = (e)=>{
            // Check if this machine was updated
            if (e.detail?.machine?.id === machineId || e.detail?.machine?.machineId === machineId) {
                console.log('🔄 MACHINE DETAIL: Machine data changed event received, reloading...');
                loadMachineData();
            }
        };
        window.addEventListener('storage', handleStorageChange);
        window.addEventListener('machineDataChanged', handleMachineDataChanged);
        return ()=>{
            window.removeEventListener('storage', handleStorageChange);
            window.removeEventListener('machineDataChanged', handleMachineDataChanged);
        };
    }, [
        machineId
    ]);
    const canEdit = user?.role === "technician";
    // Alert management state - initialized empty, populated after data loads
    const [alerts, setAlerts] = useState([]);
    // Update alerts when machine data changes
    useEffect(()=>{
        if (!machineData) return;
        const dynamicAlerts = generateDynamicAlerts(machineData);
        const existingResolvedAlerts = alerts.filter((alert)=>alert.resolved);
        setAlerts([
            ...existingResolvedAlerts,
            ...dynamicAlerts
        ]);
    }, [
        machineData?.supplies,
        machineData?.electricityStatus,
        machineData?.maintenance
    ]);
    // Handle alert resolution
    const handleAlertResolution = async (alertId)=>{
        const updatedAlerts = alerts.map((alert)=>alert.id === alertId ? {
                ...alert,
                resolved: true,
                resolvedBy: user?.name || "Technician",
                resolvedAt: "Just now"
            } : alert);
        // Update local state immediately
        setAlerts(updatedAlerts);
        // Update machine data to include resolved alerts
        const updatedMachineData = {
            ...machineData,
            alerts: updatedAlerts
        };
        setMachineData(updatedMachineData);
        try {
            // Save to backend
            await apiClient.updateMachine(machineData.id, updatedMachineData);
        } catch (error) {
            console.error("Failed to resolve alert:", error);
        // Could revert local state and show error toast
        }
    };
    const supplies = machineData ? [
        {
            name: "Water",
            key: "water",
            current: machineData.supplies.water,
            icon: /*#__PURE__*/ React.createElement(Droplets, {
                className: "w-4 h-4"
            }),
            unit: "L",
            cost: 25
        },
        {
            name: "Milk",
            key: "milk",
            current: machineData.supplies.milk,
            icon: /*#__PURE__*/ React.createElement(Milk, {
                className: "w-4 h-4"
            }),
            unit: "L",
            cost: 45
        },
        {
            name: "Coffee Beans",
            key: "coffeeBeans",
            current: machineData.supplies.coffeeBeans || machineData.supplies.coffee || 0,
            icon: /*#__PURE__*/ React.createElement(Coffee, {
                className: "w-4 h-4"
            }),
            unit: "kg",
            cost: 120
        },
        {
            name: "Sugar",
            key: "sugar",
            current: machineData.supplies.sugar,
            icon: /*#__PURE__*/ React.createElement(Candy, {
                className: "w-4 h-4"
            }),
            unit: "kg",
            cost: 30
        }
    ] : [];
    const handleSave = async ()=>{
        setIsLoading(true);
        try {
            // Ensure office field is present before saving
            const dataToSave = {
                ...machineData,
                office: machineData.office || user?.officeName || "Unknown Office"
            };
            // Save to localStorage immediately for offline support
            dataManager.saveMachine(dataToSave);
            // Try to save to backend
            try {
                const backendData = dataManager.mapFrontendToBackend(machineData);
                await apiClient.updateMachine(machineData.id, backendData);
                console.log('Machine data saved to backend successfully');
            } catch (apiError) {
                console.log('Backend unavailable, saved locally only:', apiError.message);
            }
        // Changes saved successfully
        } catch (error) {
            console.error("Failed to save machine data:", error);
        } finally{
            setIsLoading(false);
        }
    };
    const handleElectricityStatusChange = async (newElectricityStatus)=>{
        if (!canEdit) return;
        // Determine new machine status based on electricity
        let newStatus = machineData.status;
        let newPowerStatus = machineData.powerStatus;
        if (newElectricityStatus === "unavailable") {
            newStatus = "offline";
            newPowerStatus = "offline";
        } else if (newElectricityStatus === "available" && machineData.status === "offline") {
            newStatus = "operational";
            newPowerStatus = "online";
        }
        const updatedData = {
            ...machineData,
            electricityStatus: newElectricityStatus,
            status: newStatus,
            powerStatus: newPowerStatus,
            office: machineData.office || user?.officeName || "Unknown Office",
            lastPowerUpdate: new Date().toLocaleString("en-US", {
                year: "numeric",
                month: "2-digit",
                day: "2-digit",
                hour: "2-digit",
                minute: "2-digit",
                hour12: false
            })
        };
        // Update local state immediately
        setMachineData(updatedData);
        // Save to localStorage immediately - THIS CHANGE IS PERMANENT
        dataManager.saveMachine(updatedData);
        console.log(`🔒 PERMANENT CHANGE: Machine ${machineData.id} electricity status changed to "${newElectricityStatus}" (machine status: "${newStatus}") - Admin will see this!`);
        try {
            // Save to backend
            const backendData = dataManager.mapFrontendToBackend(updatedData);
            await apiClient.updateMachine(machineData.id, backendData);
            console.log('Electricity status updated in backend successfully');
        } catch (error) {
            console.log('Backend unavailable, saved electricity status locally:', error.message);
        }
        setIsEditingElectricity(false);
    };
    const handleSupplyRefill = async (supplyKey, amount)=>{
        const currentValue = machineData.supplies[supplyKey] || 0;
        const newValue = Math.min(100, currentValue + amount);
        const updatedSupplies = {
            ...machineData.supplies,
            [supplyKey]: newValue
        };
        // Ensure backend key `coffee` stays in sync when updating coffeeBeans
        if (supplyKey === 'coffeeBeans') {
            updatedSupplies.coffee = newValue;
        }
        console.log(`🔄 Refilling ${supplyKey}: ${currentValue}% → ${newValue}%`);
        // Add to recent refills
        const supplyNames = {
            water: 'Water Tank',
            milk: 'Milk Container',
            coffeeBeans: 'Coffee Beans',
            sugar: 'Sugar Container'
        };
        const newRefill = {
            id: `refill-${supplyKey}-${Date.now()}`,
            supplyType: supplyKey,
            supplyName: supplyNames[supplyKey],
            amount: newValue,
            refillAmount: amount,
            timestamp: new Date().toISOString(),
            technician: user?.name || 'Technician',
            timeAgo: 'Just now'
        };
        const updatedRefills = [
            newRefill,
            ...machineData.recentRefills || []
        ].slice(0, 10); // Keep last 10 refills
        // Create updated machine data
        const updatedMachineData = {
            ...machineData,
            supplies: updatedSupplies,
            recentRefills: updatedRefills,
            office: machineData.office || user?.officeName || "Unknown Office"
        };
        // Update local state immediately for responsive UI
        setMachineData(updatedMachineData);
        // Save entire machine to localStorage to ensure persistence
        dataManager.saveMachine(updatedMachineData);
        console.log('💾 Saved updated machine to localStorage');
        try {
            // Save to backend using backend supply keys
            const backendSupplies = dataManager.mapFrontendToBackend({
                supplies: updatedSupplies
            }).supplies;
            await apiClient.updateSupplies(machineData.id, {
                supplies: backendSupplies
            });
            console.log('📡 Supplies updated in backend successfully');
        } catch (error) {
            console.log('���️ Backend unavailable, saved supplies locally only:', error.message);
        }
    };
    const openRefillModal = (supply)=>{
        if (!canEdit) return;
        setSelectedSupply(supply);
        setRefillModalOpen(true);
    };
    const handlePowerStatusChange = async (newStatus)=>{
        if (!canEdit) return;
        const updatedData = {
            ...machineData,
            powerStatus: newStatus,
            office: machineData.office || user?.officeName || "Unknown Office",
            lastPowerUpdate: new Date().toLocaleString("en-US", {
                year: "numeric",
                month: "2-digit",
                day: "2-digit",
                hour: "2-digit",
                minute: "2-digit",
                hour12: false
            }),
            // Also update machine status if power goes offline
            status: newStatus === "offline" ? "offline" : machineData.status === "offline" ? "operational" : machineData.status
        };
        // Update local state immediately
        setMachineData(updatedData);
        // Save to localStorage immediately - THIS CHANGE IS PERMANENT
        dataManager.saveMachine(updatedData);
        console.log(`🔒 PERMANENT CHANGE: Machine ${machineData.id} power status changed to "${newStatus}" - Admin will see this!`);
        try {
            // Save to backend
            const backendData = dataManager.mapFrontendToBackend(updatedData);
            await apiClient.updateMachine(machineData.id, backendData);
            console.log('Power status updated in backend successfully');
        } catch (error) {
            console.log('Backend unavailable, saved power status locally:', error.message);
        }
    };
    const getStatusColor = (status)=>{
        switch(status){
            case "operational":
                return "bg-green-500";
            case "maintenance":
                return "bg-orange-500";
            case "offline":
                return "bg-red-500";
            default:
                return "bg-gray-500";
        }
    };
    const getSupplyColor = (percentage)=>{
        if (percentage > 60) return "text-green-600";
        if (percentage > 30) return "text-orange-500";
        return "text-red-500";
    };
    const getSupplyIcon = (percentage)=>{
        if (percentage > 60) return /*#__PURE__*/ React.createElement(CheckCircle, {
            className: "w-4 h-4 text-green-600"
        });
        if (percentage > 30) return /*#__PURE__*/ React.createElement(AlertTriangle, {
            className: "w-4 h-4 text-orange-500"
        });
        return /*#__PURE__*/ React.createElement(AlertTriangle, {
            className: "w-4 h-4 text-red-500"
        });
    };
    // Animate progress bars on mount
    useEffect(()=>{
        const timer = setTimeout(()=>{
        // Trigger rerender to animate progress bars
        }, 100);
        return ()=>clearTimeout(timer);
    }, []);
    // Show loading state while machine data is being loaded
    if (isLoadingMachine || !machineData) {
        return /*#__PURE__*/ React.createElement("div", {
            className: "min-h-screen bg-gradient-to-br from-coffee-light/30 to-background flex items-center justify-center"
        }, /*#__PURE__*/ React.createElement("div", {
            className: "text-center"
        }, /*#__PURE__*/ React.createElement(RefreshCw, {
            className: "w-8 h-8 animate-spin mx-auto mb-4 text-primary"
        }), /*#__PURE__*/ React.createElement("p", {
            className: "text-lg font-medium"
        }, "Loading machine data..."), /*#__PURE__*/ React.createElement("p", {
            className: "text-sm text-muted-foreground"
        }, "Please wait while we fetch the latest information")));
    }
    return /*#__PURE__*/ React.createElement("div", {
        className: "min-h-screen bg-gradient-to-br from-coffee-light/30 to-background"
    }, /*#__PURE__*/ React.createElement("header", {
        className: "border-b bg-white/80 backdrop-blur-sm sticky top-0 z-50"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "container mx-auto px-4 h-16 flex items-center justify-between"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-3"
    }, /*#__PURE__*/ React.createElement(InteractiveBreadcrumb, {
        backUrl: user?.role === "technician" ? machineId && user?.officeName ? `/office/${officeNameToPath(user.officeName)}` : "/dashboard" : machineData.office || officePath ? `/office/${officeNameToPath(machineData.office || pathToOfficeName(officePath || ""))}` : "/dashboard" // Fallback if no office info available
        ,
        className: "flex-1"
    }), /*#__PURE__*/ React.createElement("div", {
        className: "w-8 h-8 bg-primary rounded-lg flex items-center justify-center animate-pulse"
    }, /*#__PURE__*/ React.createElement(Coffee, {
        className: "w-5 h-5 text-primary-foreground"
    })), /*#__PURE__*/ React.createElement("h1", {
        className: "text-xl font-bold text-coffee-brown"
    }, officePath ? `${pathToOfficeName(officePath)} - Coffee Machines` : "Machine Management")), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-3"
    }, (user?.officeName || officePath) && /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Badge, {
        variant: "outline",
        className: "gap-1 animate-fadeIn"
    }, /*#__PURE__*/ React.createElement(Building, {
        className: "w-3 h-3"
    }), officePath ? pathToOfficeName(officePath) : user?.officeName), user?.city && /*#__PURE__*/ React.createElement(Badge, {
        variant: "secondary",
        className: "gap-1 animate-fadeIn"
    }, /*#__PURE__*/ React.createElement(MapPin, {
        className: "w-3 h-3"
    }), user.city.charAt(0).toUpperCase() + user.city.slice(1))), /*#__PURE__*/ React.createElement(Badge, {
        variant: user?.role === "technician" ? "default" : "secondary",
        className: "gap-1 animate-fadeIn"
    }, user?.role === "technician" ? /*#__PURE__*/ React.createElement(Edit3, {
        className: "w-3 h-3"
    }) : /*#__PURE__*/ React.createElement(Eye, {
        className: "w-3 h-3"
    }), user?.role === "technician" ? "Edit Mode" : "View Only"), canEdit && /*#__PURE__*/ React.createElement(Button, {
        onClick: handleSave,
        variant: "default",
        disabled: isLoading,
        className: "hover:scale-105 transition-transform"
    }, isLoading ? /*#__PURE__*/ React.createElement(React.Fragment, null, /*#__PURE__*/ React.createElement(RefreshCw, {
        className: "w-4 h-4 mr-2 animate-spin"
    }), "Saving...") : /*#__PURE__*/ React.createElement(React.Fragment, null, /*#__PURE__*/ React.createElement(Save, {
        className: "w-4 h-4 mr-2"
    }), "Save Changes"))))), /*#__PURE__*/ React.createElement("main", {
        className: "container mx-auto px-4 py-8"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "max-w-7xl mx-auto space-y-6"
    }, /*#__PURE__*/ React.createElement(Card, {
        className: "animate-fadeIn"
    }, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-between"
    }, /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Coffee, {
        className: "w-5 h-5"
    }), machineData.name), /*#__PURE__*/ React.createElement(CardDescription, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(MapPin, {
        className: "w-3 h-3"
    }), machineData.location), user?.role === "technician" && /*#__PURE__*/ React.createElement("div", {
        className: "text-xs text-muted-foreground mt-1"
    }, "Assigned to: ", user.name))))), /*#__PURE__*/ React.createElement(Card, {
        className: "animate-fadeIn",
        style: {
            animationDelay: "100ms"
        }
    }, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Activity, {
        className: "w-5 h-5"
    }), "Machine Status & Power Management"), /*#__PURE__*/ React.createElement(CardDescription, null, "Current operational status and electricity management")), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 md:grid-cols-3 gap-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        className: "text-sm font-medium"
    }, "Machine Status"), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement("div", {
        className: `w-3 h-3 rounded-full ${getStatusColor(machineData.status)}`
    }), /*#__PURE__*/ React.createElement("span", {
        className: "capitalize text-sm font-medium"
    }, machineData.status === "offline" && machineData.electricityStatus === "unavailable" ? "Not Functional (No Electricity)" : machineData.status === "offline" ? "Not Functional" : machineData.status))), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        className: "text-sm font-medium"
    }, "Electricity Status"), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, machineData.electricityStatus === "available" ? /*#__PURE__*/ React.createElement(Zap, {
        className: "w-4 h-4 text-green-600"
    }) : /*#__PURE__*/ React.createElement(ZapOff, {
        className: "w-4 h-4 text-red-600"
    }), /*#__PURE__*/ React.createElement("span", {
        className: `text-sm font-medium ${machineData.electricityStatus === "available" ? "text-green-600" : "text-red-600"}`
    }, machineData.electricityStatus === "available" ? "Available" : "Unavailable"))), canEdit && /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        className: "text-sm font-medium"
    }, "Technician Controls"), /*#__PURE__*/ React.createElement("div", {
        className: "flex gap-2"
    }, !isEditingElectricity ? /*#__PURE__*/ React.createElement(Button, {
        size: "sm",
        variant: "outline",
        onClick: ()=>setIsEditingElectricity(true),
        className: "text-xs"
    }, /*#__PURE__*/ React.createElement(Edit3, {
        className: "w-3 h-3 mr-1"
    }), "Edit Status") : /*#__PURE__*/ React.createElement("div", {
        className: "flex gap-2"
    }, /*#__PURE__*/ React.createElement(Button, {
        size: "sm",
        variant: machineData.electricityStatus === "available" ? "destructive" : "default",
        onClick: ()=>handleElectricityStatusChange(machineData.electricityStatus === "available" ? "unavailable" : "available"),
        className: "text-xs"
    }, machineData.electricityStatus === "available" ? /*#__PURE__*/ React.createElement(React.Fragment, null, /*#__PURE__*/ React.createElement(ZapOff, {
        className: "w-3 h-3 mr-1"
    }), "Set Offline") : /*#__PURE__*/ React.createElement(React.Fragment, null, /*#__PURE__*/ React.createElement(Zap, {
        className: "w-3 h-3 mr-1"
    }), "Set Online")), /*#__PURE__*/ React.createElement(Button, {
        size: "sm",
        variant: "outline",
        onClick: ()=>setIsEditingElectricity(false),
        className: "text-xs"
    }, "Cancel"))))), /*#__PURE__*/ React.createElement("div", {
        className: "mt-4 pt-4 border-t"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "text-xs text-muted-foreground"
    }, "Last power update: ", machineData.lastPowerUpdate || "Unknown")))), /*#__PURE__*/ React.createElement(Tabs, {
        defaultValue: "overview",
        className: "space-y-6"
    }, /*#__PURE__*/ React.createElement(TabsList, {
        className: "grid w-full grid-cols-3"
    }, /*#__PURE__*/ React.createElement(TabsTrigger, {
        value: "overview",
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Activity, {
        className: "w-4 h-4"
    }), "Overview"), /*#__PURE__*/ React.createElement(TabsTrigger, {
        value: "supplies",
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Droplets, {
        className: "w-4 h-4"
    }), "Supplies"), /*#__PURE__*/ React.createElement(TabsTrigger, {
        value: "analytics",
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(TrendingUp, {
        className: "w-4 h-4"
    }), "Analytics")), /*#__PURE__*/ React.createElement(TabsContent, {
        value: "overview",
        className: "space-y-6"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 gap-6"
    }), /*#__PURE__*/ React.createElement(Card, {
        className: "animate-fadeIn",
        style: {
            animationDelay: "300ms"
        }
    }, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(AlertTriangle, {
        className: "w-4 h-4"
    }), "Machine Alerts & Issues"), /*#__PURE__*/ React.createElement(CardDescription, null, "Monitor and resolve machine issues (", alerts.filter((a)=>!a.resolved).length, " active)")), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-4"
    }, alerts.map((alert)=>{
        const getAlertColor = (type, resolved)=>{
            if (resolved) return "border-green-200 bg-green-50";
            switch(type){
                case "critical":
                    return "border-red-200 bg-red-50";
                case "warning":
                    return "border-orange-200 bg-orange-50";
                case "info":
                    return "border-blue-200 bg-blue-50";
                default:
                    return "border-gray-200 bg-gray-50";
            }
        };
        const getAlertIcon = (type, resolved)=>{
            if (resolved) return /*#__PURE__*/ React.createElement(CheckCircle, {
                className: "h-4 w-4 text-green-600"
            });
            switch(type){
                case "critical":
                    return /*#__PURE__*/ React.createElement(AlertTriangle, {
                        className: "h-4 w-4 text-red-600"
                    });
                case "warning":
                    return /*#__PURE__*/ React.createElement(AlertTriangle, {
                        className: "h-4 w-4 text-orange-600"
                    });
                case "info":
                    return /*#__PURE__*/ React.createElement(Clock, {
                        className: "h-4 w-4 text-blue-600"
                    });
                default:
                    return /*#__PURE__*/ React.createElement(AlertTriangle, {
                        className: "h-4 w-4 text-gray-600"
                    });
            }
        };
        const getAlertPrefix = (type, resolved)=>{
            if (resolved) return "Resolved:";
            switch(type){
                case "critical":
                    return "Critical:";
                case "warning":
                    return "Warning:";
                case "info":
                    return "Info:";
                default:
                    return "Alert:";
            }
        };
        return /*#__PURE__*/ React.createElement(Alert, {
            key: alert.id,
            className: getAlertColor(alert.type, alert.resolved)
        }, getAlertIcon(alert.type, alert.resolved), /*#__PURE__*/ React.createElement(AlertDescription, null, /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center justify-between"
        }, /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("strong", null, getAlertPrefix(alert.type, alert.resolved)), " ", alert.title, " - ", alert.description, alert.resolved && alert.resolvedBy && /*#__PURE__*/ React.createElement("div", {
            className: "text-xs text-muted-foreground mt-1"
        }, "Resolved by ", alert.resolvedBy, " (", alert.resolvedAt, ")")), canEdit && !alert.resolved && /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-2 ml-4"
        }, /*#__PURE__*/ React.createElement(Button, {
            size: "sm",
            variant: "outline",
            onClick: ()=>handleAlertResolution(alert.id)
        }, /*#__PURE__*/ React.createElement(CheckCircle, {
            className: "w-3 h-3 mr-1"
        }), "Mark Resolved")), alert.resolved && /*#__PURE__*/ React.createElement(Badge, {
            variant: "default",
            className: "bg-green-100 text-green-800"
        }, "✓ Resolved"))));
    }), alerts.filter((a)=>!a.resolved).length === 0 && /*#__PURE__*/ React.createElement("div", {
        className: "text-center py-8"
    }, /*#__PURE__*/ React.createElement(CheckCircle, {
        className: "w-12 h-12 text-green-500 mx-auto mb-4"
    }), /*#__PURE__*/ React.createElement("h3", {
        className: "text-lg font-medium"
    }, "All Clear!"), /*#__PURE__*/ React.createElement("p", {
        className: "text-muted-foreground"
    }, "No active alerts for this machine."))))), canEdit && /*#__PURE__*/ React.createElement(Card, {
        className: "animate-fadeIn",
        style: {
            animationDelay: "500ms"
        }
    }, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Edit3, {
        className: "w-4 h-4"
    }), "Technician Notes"), /*#__PURE__*/ React.createElement(CardDescription, null, "Add maintenance notes, observations, or reminders")), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement(Textarea, {
        value: machineData.notes,
        onChange: (e)=>setMachineData((prev)=>({
                    ...prev,
                    notes: e.target.value
                })),
        placeholder: "Add maintenance notes, observations, or reminders...",
        className: "min-h-[100px]"
    }))))), /*#__PURE__*/ React.createElement(TabsContent, {
        value: "supplies",
        className: "space-y-6"
    }, /*#__PURE__*/ React.createElement(Card, {
        className: "animate-fadeIn"
    }, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Droplets, {
        className: "w-5 h-5"
    }), "Supply Status Overview"), /*#__PURE__*/ React.createElement(CardDescription, null, "Current supply levels and consumption trends")), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-2 md:grid-cols-4 gap-4"
    }, supplies.map((supply, index)=>/*#__PURE__*/ React.createElement("div", {
            key: supply.key,
            className: "text-center"
        }, /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center justify-center mb-2"
        }, supply.icon), /*#__PURE__*/ React.createElement(Progress, {
            value: supply.current,
            className: "mb-2"
        }), /*#__PURE__*/ React.createElement("div", {
            className: "text-sm font-medium"
        }, supply.current, "%"), /*#__PURE__*/ React.createElement("div", {
            className: "text-xs text-muted-foreground"
        }, supply.name)))))), /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6"
    }, supplies.map((supply, index)=>/*#__PURE__*/ React.createElement(Card, {
            key: supply.key,
            className: `cursor-pointer transition-all duration-300 hover:scale-105 hover:shadow-lg ${canEdit ? "hover:border-primary" : ""} animate-fadeIn`,
            style: {
                animationDelay: `${index * 150}ms`
            },
            onClick: ()=>openRefillModal(supply)
        }, /*#__PURE__*/ React.createElement(CardHeader, {
            className: "pb-3"
        }, /*#__PURE__*/ React.createElement(CardTitle, {
            className: "flex items-center justify-between text-base"
        }, /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-2"
        }, supply.icon, supply.name), getSupplyIcon(supply.current))), /*#__PURE__*/ React.createElement(CardContent, {
            className: "space-y-4"
        }, /*#__PURE__*/ React.createElement("div", {
            className: "text-center"
        }, /*#__PURE__*/ React.createElement("div", {
            className: `text-3xl font-bold ${getSupplyColor(supply.current)}`
        }, supply.current, "%"), /*#__PURE__*/ React.createElement("p", {
            className: "text-sm text-muted-foreground"
        }, "Current Level")), /*#__PURE__*/ React.createElement(Progress, {
            value: supply.current,
            className: "h-3 transition-all duration-1000 ease-out"
        }), /*#__PURE__*/ React.createElement("div", {
            className: "flex justify-between text-xs text-muted-foreground"
        }, /*#__PURE__*/ React.createElement("span", null, "Empty"), /*#__PURE__*/ React.createElement("span", null, "Full")), canEdit && /*#__PURE__*/ React.createElement(Button, {
            size: "sm",
            variant: "outline",
            className: "w-full group hover:bg-primary hover:text-primary-foreground transition-colors"
        }, /*#__PURE__*/ React.createElement(Plus, {
            className: "w-4 h-4 mr-2 group-hover:scale-110 transition-transform"
        }), "Refill"))))), /*#__PURE__*/ React.createElement(Card, {
        className: "animate-fadeIn",
        style: {
            animationDelay: "400ms"
        }
    }, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Calendar, {
        className: "w-4 h-4"
    }), "Recent Refills"), /*#__PURE__*/ React.createElement(CardDescription, null, machineData.recentRefills?.length || 0, " refill", (machineData.recentRefills?.length || 0) !== 1 ? 's' : '', " recorded")), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-3"
    }, machineData.recentRefills && machineData.recentRefills.length > 0 ? machineData.recentRefills.map((refill, index)=>{
        const formatTimeAgo = (timestamp)=>{
            const now = new Date();
            const refillTime = new Date(timestamp);
            const diffMs = now.getTime() - refillTime.getTime();
            const diffMins = Math.floor(diffMs / (1000 * 60));
            const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
            const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
            if (diffMins < 1) return 'Just now';
            if (diffMins < 60) return `${diffMins} minute${diffMins !== 1 ? 's' : ''} ago`;
            if (diffHours < 24) return `${diffHours} hour${diffHours !== 1 ? 's' : ''} ago`;
            return `${diffDays} day${diffDays !== 1 ? 's' : ''} ago`;
        };
        const getBgColor = (supplyType)=>{
            switch(supplyType){
                case 'water':
                    return 'bg-blue-50 border-blue-200';
                case 'milk':
                    return 'bg-purple-50 border-purple-200';
                case 'coffeeBeans':
                    return 'bg-amber-50 border-amber-200';
                case 'sugar':
                    return 'bg-pink-50 border-pink-200';
                default:
                    return 'bg-gray-50 border-gray-200';
            }
        };
        return /*#__PURE__*/ React.createElement("div", {
            key: refill.id,
            className: `flex items-center justify-between p-3 rounded-lg border ${getBgColor(refill.supplyType)}`
        }, /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("div", {
            className: "font-medium text-sm"
        }, refill.supplyName), /*#__PURE__*/ React.createElement("div", {
            className: "text-xs text-muted-foreground"
        }, "Refilled to ", refill.amount, "% (+", refill.refillAmount, "%)")), /*#__PURE__*/ React.createElement("div", {
            className: "text-right"
        }, /*#__PURE__*/ React.createElement("div", {
            className: "text-xs font-medium"
        }, formatTimeAgo(refill.timestamp)), /*#__PURE__*/ React.createElement("div", {
            className: "text-xs text-muted-foreground"
        }, "by ", refill.technician)));
    }) : /*#__PURE__*/ React.createElement("div", {
        className: "text-center py-8"
    }, /*#__PURE__*/ React.createElement(Calendar, {
        className: "w-12 h-12 text-gray-400 mx-auto mb-4"
    }), /*#__PURE__*/ React.createElement("h3", {
        className: "text-lg font-medium"
    }, "No Recent Refills"), /*#__PURE__*/ React.createElement("p", {
        className: "text-muted-foreground"
    }, "Refill activities will appear here after supplies are refilled.")))))), /*#__PURE__*/ React.createElement(TabsContent, {
        value: "analytics",
        className: "space-y-6"
    }, /*#__PURE__*/ React.createElement(UsageChart, null), /*#__PURE__*/ React.createElement(BrewTypeAnalytics, {
        machineId: machineData.id
    }), /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 md:grid-cols-2 gap-6"
    }, /*#__PURE__*/ React.createElement(Card, {
        className: "animate-fadeIn",
        style: {
            animationDelay: "200ms"
        }
    }, /*#__PURE__*/ React.createElement(CardHeader, {
        className: "pb-3"
    }, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Clock, {
        className: "w-4 h-4"
    }), "Peak Hours")), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-3"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex justify-between items-center"
    }, /*#__PURE__*/ React.createElement("span", {
        className: "text-sm"
    }, "8-9 AM"), /*#__PURE__*/ React.createElement(Badge, {
        variant: "outline"
    }, "52 cups")), /*#__PURE__*/ React.createElement("div", {
        className: "flex justify-between items-center"
    }, /*#__PURE__*/ React.createElement("span", {
        className: "text-sm"
    }, "1-2 PM"), /*#__PURE__*/ React.createElement(Badge, {
        variant: "outline"
    }, "48 cups")), /*#__PURE__*/ React.createElement("div", {
        className: "flex justify-between items-center"
    }, /*#__PURE__*/ React.createElement("span", {
        className: "text-sm"
    }, "3-4 PM"), /*#__PURE__*/ React.createElement(Badge, {
        variant: "outline"
    }, "35 cups")))))))))), selectedSupply && /*#__PURE__*/ React.createElement(SupplyRefillModal, {
        isOpen: refillModalOpen,
        onClose: ()=>{
            setRefillModalOpen(false);
            setSelectedSupply(null);
        },
        supply: selectedSupply,
        onRefill: (amount)=>handleSupplyRefill(selectedSupply.key, amount),
        canEdit: canEdit
    }));
}
