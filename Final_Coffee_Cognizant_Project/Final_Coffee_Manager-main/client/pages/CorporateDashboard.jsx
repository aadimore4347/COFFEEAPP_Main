import React, { useState, useEffect } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { apiClient } from "@/lib/api";
import { dataManager } from "@/lib/dataManager";
import { mqttClient, useMQTTSubscription } from "@/lib/mqtt";
import {
  Coffee,
  MapPin,
  Building,
  Settings,
  LogOut,
  ChevronRight,
  Eye,
  Edit3,
  ArrowLeft,
  Activity,
  TrendingUp,
  Users,
  Clock,
  Zap,
  AlertTriangle,
  CheckCircle,
  BarChart3,
  PieChart,
  Target,
  Cpu,
  Droplets,
  Plus,
  Trash2,
} from "lucide-react";
import AddMachineModal from "@/components/AddMachineModal";
import DeleteMachineDialog from "@/components/DeleteMachineDialog";
import { toast } from "sonner";
import { SUCCESS_MESSAGES } from "@/config";

export default function CorporateDashboard() {
  const { user, logout } = useAuth();
  const [currentStep, setCurrentStep] = useState(0);
  const [selectedLocation, setSelectedLocation] = useState("");
  const [selectedOffice, setSelectedOffice] = useState("");
  const [selectedMachine, setSelectedMachine] = useState("");

  // Corporate data structure
  const [locations, setLocations] = useState([]);
  const [offices, setOffices] = useState([]);
  const [machines, setMachines] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  // Real-time data
  const [realtimeUpdates, setRealtimeUpdates] = useState(new Map());

  // Add Machine Modal state
  const [isAddMachineModalOpen, setIsAddMachineModalOpen] = useState(false);
  const [isAddingMachine, setIsAddingMachine] = useState(false);

  // Delete Machine Modal state
  const [isDeleteMachineDialogOpen, setIsDeleteMachineDialogOpen] = useState(false);
  const [machineToDelete, setMachineToDelete] = useState(null);
  const [isDeletingMachine, setIsDeletingMachine] = useState(false);

  // Streamlined 3-step process: Location → Office → Machine
  const steps = [
    {
      id: "location",
      title: "Select Location",
      description: "Choose your operational city",
      icon: <MapPin className="w-5 h-5" />,
      completed: !!selectedLocation,
    },
    {
      id: "office",
      title: "Select Office",
      description: "Choose your office branch",
      icon: <Building className="w-5 h-5" />,
      completed: !!selectedOffice,
    },
    {
      id: "machine",
      title: "Machine Analytics",
      description: "View machine performance",
      icon: <Coffee className="w-5 h-5" />,
      completed: !!selectedMachine,
    },
  ];

  // Corporate locations data - Pune and Mumbai only
  const corporateLocations = [
    {
      name: "Pune",
      totalMachines: 12,
      efficiency: 94,
      status: "excellent",
      offices: [
        {
          name: "Hinjewadi IT Park",
          address: "Phase 1, Hinjewadi, Pune - 411057",
          machines: [
            {
              id: "PUN-HIN-001",
              name: "Reception Lounge",
              status: "operational",
              performance: {
                dailyCups: 125,
                efficiency: 96,
                supplies: { water: 85, milk: 70, coffee: 80, sugar: 90 },
              },
            },
            {
              id: "PUN-HIN-002",
              name: "Cafeteria Level 1",
              status: "operational",
              performance: {
                dailyCups: 89,
                efficiency: 92,
                supplies: { water: 75, milk: 60, coffee: 85, sugar: 85 },
              },
            },
            {
              id: "PUN-HIN-003",
              name: "Cafeteria Level 2",
              status: "maintenance",
              performance: {
                dailyCups: 0,
                efficiency: 0,
                supplies: { water: 45, milk: 20, coffee: 30, sugar: 55 },
              },
            },
            {
              id: "PUN-HIN-004",
              name: "Break Room Tower A",
              status: "operational",
              performance: {
                dailyCups: 67,
                efficiency: 88,
                supplies: { water: 90, milk: 80, coffee: 75, sugar: 70 },
              },
            },
          ],
          performance: {
            weeklyCups: 1890,
            efficiency: 92,
            uptime: 98.5,
            alerts: 1,
          },
        },
        {
          name: "Koregaon Park Corporate",
          address: "Koregaon Park, Pune - 411001",
          machines: [
            {
              id: "PUN-KOR-001",
              name: "Executive Floor",
              status: "operational",
              performance: {
                dailyCups: 78,
                efficiency: 94,
                supplies: { water: 80, milk: 85, coffee: 70, sugar: 95 },
              },
            },
            {
              id: "PUN-KOR-002",
              name: "Employee Lounge",
              status: "operational",
              performance: {
                dailyCups: 134,
                efficiency: 97,
                supplies: { water: 90, milk: 75, coffee: 85, sugar: 80 },
              },
            },
            {
              id: "PUN-KOR-003",
              name: "Conference Center",
              status: "operational",
              performance: {
                dailyCups: 45,
                efficiency: 89,
                supplies: { water: 70, milk: 65, coffee: 90, sugar: 75 },
              },
            },
          ],
          performance: {
            weeklyCups: 1802,
            efficiency: 93,
            uptime: 99.2,
            alerts: 0,
          },
        },
        {
          name: "Viman Nagar Tech Hub",
          address: "Viman Nagar, Pune - 411014",
          machines: [
            {
              id: "PUN-VIM-001",
              name: "Innovation Center",
              status: "operational",
              performance: {
                dailyCups: 92,
                efficiency: 91,
                supplies: { water: 85, milk: 70, coffee: 80, sugar: 85 },
              },
            },
            {
              id: "PUN-VIM-002",
              name: "Co-working Space",
              status: "operational",
              performance: {
                dailyCups: 156,
                efficiency: 95,
                supplies: { water: 75, milk: 80, coffee: 85, sugar: 90 },
              },
            },
            {
              id: "PUN-VIM-003",
              name: "Meeting Rooms",
              status: "offline",
              performance: {
                dailyCups: 0,
                efficiency: 0,
                supplies: { water: 25, milk: 15, coffee: 20, sugar: 30 },
              },
            },
            {
              id: "PUN-VIM-004",
              name: "Cafeteria Main",
              status: "operational",
              performance: {
                dailyCups: 198,
                efficiency: 98,
                supplies: { water: 95, milk: 90, coffee: 85, sugar: 80 },
              },
            },
            {
              id: "PUN-VIM-005",
              name: "Reception Area",
              status: "operational",
              performance: {
                dailyCups: 67,
                efficiency: 87,
                supplies: { water: 60, milk: 55, coffee: 70, sugar: 85 },
              },
            },
          ],
          performance: {
            weeklyCups: 3591,
            efficiency: 94,
            uptime: 96.8,
            alerts: 2,
          },
        },
      ],
    },
    {
      name: "Mumbai",
      totalMachines: 15,
      efficiency: 96,
      status: "excellent",
      offices: [
        {
          name: "Bandra Kurla Complex",
          address: "BKC, Bandra East, Mumbai - 400051",
          machines: [
            {
              id: "MUM-BKC-001",
              name: "Corporate Tower A",
              status: "operational",
              performance: {
                dailyCups: 189,
                efficiency: 97,
                supplies: { water: 90, milk: 85, coffee: 90, sugar: 95 },
              },
            },
            {
              id: "MUM-BKC-002",
              name: "Corporate Tower B",
              status: "operational",
              performance: {
                dailyCups: 167,
                efficiency: 95,
                supplies: { water: 85, milk: 80, coffee: 85, sugar: 90 },
              },
            },
            {
              id: "MUM-BKC-003",
              name: "Executive Lounge",
              status: "operational",
              performance: {
                dailyCups: 89,
                efficiency: 98,
                supplies: { water: 95, milk: 90, coffee: 95, sugar: 85 },
              },
            },
            {
              id: "MUM-BKC-004",
              name: "Conference Hall",
              status: "operational",
              performance: {
                dailyCups: 56,
                efficiency: 92,
                supplies: { water: 80, milk: 75, coffee: 80, sugar: 90 },
              },
            },
            {
              id: "MUM-BKC-005",
              name: "Cafeteria Premium",
              status: "operational",
              performance: {
                dailyCups: 234,
                efficiency: 99,
                supplies: { water: 90, milk: 95, coffee: 90, sugar: 85 },
              },
            },
          ],
          performance: {
            weeklyCups: 5145,
            efficiency: 96,
            uptime: 99.8,
            alerts: 0,
          },
        },
        {
          name: "Lower Parel Financial",
          address: "Lower Parel, Mumbai - 400013",
          machines: [
            {
              id: "MUM-LOW-001",
              name: "Trading Floor",
              status: "operational",
              performance: {
                dailyCups: 145,
                efficiency: 94,
                supplies: { water: 85, milk: 80, coffee: 85, sugar: 90 },
              },
            },
            {
              id: "MUM-LOW-002",
              name: "Executive Suite",
              status: "operational",
              performance: {
                dailyCups: 78,
                efficiency: 96,
                supplies: { water: 90, milk: 85, coffee: 90, sugar: 95 },
              },
            },
            {
              id: "MUM-LOW-003",
              name: "Client Meeting Area",
              status: "operational",
              performance: {
                dailyCups: 89,
                efficiency: 93,
                supplies: { water: 75, milk: 70, coffee: 80, sugar: 85 },
              },
            },
            {
              id: "MUM-LOW-004",
              name: "Employee Break Zone",
              status: "maintenance",
              performance: {
                dailyCups: 0,
                efficiency: 0,
                supplies: { water: 40, milk: 25, coffee: 35, sugar: 50 },
              },
            },
          ],
          performance: {
            weeklyCups: 2184,
            efficiency: 94,
            uptime: 97.5,
            alerts: 1,
          },
        },
        {
          name: "Andheri Tech Center",
          address: "Andheri East, Mumbai - 400069",
          machines: [
            {
              id: "MUM-AND-001",
              name: "Innovation Lab",
              status: "operational",
              performance: {
                dailyCups: 123,
                efficiency: 95,
                supplies: { water: 80, milk: 85, coffee: 90, sugar: 80 },
              },
            },
            {
              id: "MUM-AND-002",
              name: "Development Floor",
              status: "operational",
              performance: {
                dailyCups: 198,
                efficiency: 97,
                supplies: { water: 90, milk: 80, coffee: 85, sugar: 90 },
              },
            },
            {
              id: "MUM-AND-003",
              name: "Testing Lab",
              status: "operational",
              performance: {
                dailyCups: 67,
                efficiency: 89,
                supplies: { water: 70, milk: 65, coffee: 75, sugar: 85 },
              },
            },
            {
              id: "MUM-AND-004",
              name: "Collaboration Space",
              status: "operational",
              performance: {
                dailyCups: 156,
                efficiency: 96,
                supplies: { water: 85, milk: 90, coffee: 80, sugar: 75 },
              },
            },
            {
              id: "MUM-AND-005",
              name: "Main Cafeteria",
              status: "operational",
              performance: {
                dailyCups: 289,
                efficiency: 98,
                supplies: { water: 95, milk: 85, coffee: 90, sugar: 95 },
              },
            },
            {
              id: "MUM-AND-006",
              name: "Reception Lobby",
              status: "operational",
              performance: {
                dailyCups: 89,
                efficiency: 91,
                supplies: { water: 75, milk: 70, coffee: 85, sugar: 80 },
              },
            },
          ],
          performance: {
            weeklyCups: 5733,
            efficiency: 94,
            uptime: 98.9,
            alerts: 0,
          },
        },
      ],
    },
  ];

  // MQTT subscription for real-time updates
  useMQTTSubscription("coffee/machines/+/status", (message) => {
    const update = message.payload;
    setRealtimeUpdates((prev) => new Map(prev.set(update.machineId, update)));
  });

  // Load corporate data and merge with localStorage data
  useEffect(() => {
    const loadData = () => {
      let loadedLocations = [...corporateLocations];

      const storedMachines = dataManager.getAllMachinesFromSharedStorage();

      if (storedMachines.length > 0) {
        console.log(`✅ ADMIN VIEW: Loaded ${storedMachines.length} machines from shared storage (cross-user sync enabled)`);

        const toDashboardMachine = (m) => ({
          ...m,
          performance: m.performance || {
            dailyCups: m?.usage?.dailyCups ?? 0,
            efficiency: 100,
            supplies: {
              water: m?.supplies?.water ?? 0,
              milk: m?.supplies?.milk ?? 0,
              coffee: (m?.supplies?.coffee ?? m?.supplies?.coffeeBeans) ?? 0,
              sugar: m?.supplies?.sugar ?? 0,
            },
          },
        });

        const machinesByOffice = storedMachines.reduce((acc, machine) => {
          const office = machine.office || 'Unknown Office';
          if (!acc[office]) acc[office] = [];
          acc[office].push(toDashboardMachine(machine));
          return acc;
        }, {});

        loadedLocations = loadedLocations.map(location => ({
          ...location,
          offices: location.offices.map(office => {
            const storedMachinesForOffice = machinesByOffice[office.name] || [];

            let updatedMachines = [...office.machines];

            storedMachinesForOffice.forEach(storedMachine => {
              const enriched = toDashboardMachine(storedMachine);
              const existingIndex = updatedMachines.findIndex(m => m.id === enriched.id);
              if (existingIndex !== -1) {
                updatedMachines[existingIndex] = enriched;
                console.log(`🔄 ADMIN: Updated machine ${enriched.id} status to ${enriched.status}`);
              } else {
                updatedMachines.push(enriched);
                console.log(`➕ ADMIN: Added new machine ${enriched.id}`);
              }
            });

            return {
              ...office,
              machines: updatedMachines
            };
          })
        }));
      }

      setLocations(loadedLocations);
    };

    loadData();
  }, []);

  const handleLocationSelect = (location) => {
    setSelectedLocation(location);
    setSelectedOffice("");
    setSelectedMachine("");
    const locationData = locations.find((l) => l.name === location);
    setOffices(locationData?.offices || []);
    setCurrentStep(1);
  };

  const handleOfficeSelect = (office) => {
    setSelectedOffice(office);
    setSelectedMachine("");
    const officeData = offices.find((o) => o.name === office);
    setMachines(officeData?.machines || []);
    setCurrentStep(2);
  };

  const handleMachineSelect = (machine) => {
    setSelectedMachine(machine);
  };

  const handleAddMachine = async (machineData) => {
    setIsAddingMachine(true);

    try {
      const officePrefix = selectedOffice === "Hinjewadi IT Park" ? "HIJ" :
                          selectedOffice === "Koregaon Park Corporate" ? "KOR" : "NEW";
      const machineNumber = (machines.length + 1).toString().padStart(3, '0');
      const newMachineId = `${officePrefix}-${machineNumber}`;

      const newMachine = {
        id: newMachineId,
        machineId: newMachineId,
        name: machineData.name,
        status: machineData.status,
        office: selectedOffice,
        floor: machineData.floor || "Ground Floor",
        performance: {
          dailyCups: 0,
          efficiency: 100,
          supplies: {
            water: machineData.supplies.water,
            milk: machineData.supplies.milk,
            coffee: machineData.supplies.coffeeBeans,
            sugar: machineData.supplies.sugar,
          },
        },
        location: machineData.location,
        powerStatus: "online",
        lastPowerUpdate: new Date().toISOString().slice(0, 19).replace('T', ' '),
        lastMaintenance: new Date().toISOString().slice(0, 10),
        nextMaintenance: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
        supplies: {
          water: machineData.supplies.water,
          milk: machineData.supplies.milk,
          coffee: machineData.supplies.coffeeBeans,
          coffeeBeans: machineData.supplies.coffeeBeans,
          sugar: machineData.supplies.sugar,
        },
        maintenance: machineData.maintenance,
        usage: {
          dailyCups: 0,
          weeklyCups: 0,
        },
        notes: machineData.notes || "New machine installation",
      };

      dataManager.saveMachine(newMachine);

      try {
        const backendData = dataManager.mapFrontendToBackend(newMachine);
        await apiClient.createMachine(backendData);
        console.log('Machine saved to backend successfully');
      } catch (error) {
        console.log('Backend unavailable, saved locally only:', error.message);
      }

      setMachines(prev => [...prev, newMachine]);

      setLocations(prevLocations =>
        prevLocations.map(location => ({
          ...location,
          offices: location.offices.map(office =>
            office.name === selectedOffice
              ? { ...office, machines: [...office.machines, newMachine] }
              : office
          )
        }))
      );

      setIsAddMachineModalOpen(false);
      console.log('Machine added successfully:', newMachine);

    } catch (error) {
      console.error('Failed to add machine:', error);
    } finally {
      setIsAddingMachine(false);
    }
  };

  const handleDeleteMachine = (machine) => {
    setMachineToDelete(machine);
    setIsDeleteMachineDialogOpen(true);
  };

  const handleConfirmDeleteMachine = async () => {
    if (!machineToDelete) return;

    setIsDeletingMachine(true);

    try {
      dataManager.removeMachine(machineToDelete.id);

      try {
        await apiClient.deleteMachine(machineToDelete.id);
        console.log('Machine deleted from backend successfully');
      } catch (error) {
        console.log('Backend unavailable, deleted locally only:', error.message);
      }

      setMachines(prev => prev.filter(m => m.id !== machineToDelete.id));

      setLocations(prevLocations =>
        prevLocations.map(location => ({
          ...location,
          offices: location.offices.map(office => ({
            ...office,
            machines: office.machines.filter(m => m.id !== machineToDelete.id)
          }))
        }))
      );

      if (selectedMachine === machineToDelete.id) {
        setSelectedMachine("");
      }

      setIsDeleteMachineDialogOpen(false);
      setMachineToDelete(null);

      toast.success(SUCCESS_MESSAGES.MACHINE_DELETED);

      console.log('Machine deleted successfully:', machineToDelete.id);

    } catch (error) {
      console.error('Failed to delete machine:', error);
      toast.error('Failed to delete machine. Please try again.');
    } finally {
      setIsDeletingMachine(false);
    }
  };

  const getLocationData = (locationName) => {
    return corporateLocations.find((l) => l.name === locationName);
  };

  const getOfficeData = (officeName) => {
    return offices.find((o) => o.name === officeName);
  };

  const getMachineData = (machineId) => {
    return machines.find((m) => m.id === machineId);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case "operational":
        return "text-green-600";
      case "maintenance":
        return "text-orange-500";
      case "offline":
        return "text-red-500";
      default:
        return "text-gray-500";
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case "operational":
        return <CheckCircle className="w-4 h-4 text-green-500" />;
      case "maintenance":
        return <AlertTriangle className="w-4 h-4 text-orange-500" />;
      case "offline":
        return <AlertTriangle className="w-4 h-4 text-red-500" />;
      default:
        return <AlertTriangle className="w-4 h-4 text-gray-400" />;
    }
  };

  const allStepsCompleted =
    selectedLocation && selectedOffice && selectedMachine;

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
      {/* Corporate Header */}
      <header className="border-b bg-white/90 backdrop-blur-md shadow-sm">
        <div className="container mx-auto px-6 h-20 flex items-center justify-between">
          <div className="flex items-center gap-4">
            <Button
              variant="ghost"
              size="sm"
              onClick={logout}
              className="hover:scale-105 transition-all duration-200 hover:bg-primary/10 group"
            >
              <ArrowLeft className="w-4 h-4 mr-2 group-hover:-translate-x-1 transition-transform" />
              <span className="hidden sm:inline">Sign Out</span>
            </Button>
            <div className="w-px h-8 bg-border" />
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gradient-to-br from-blue-600 to-indigo-600 rounded-xl flex items-center justify-center shadow-lg">
                <Coffee className="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 className="text-xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                  CoffeeFlow Enterprise
                </h1>
                <p className="text-sm text-muted-foreground">
                  Operations Dashboard
                </p>
              </div>
            </div>
          </div>

          <div className="flex items-center gap-4">
            <Badge
              variant={user?.role === "technician" ? "default" : "secondary"}
              className="gap-2 px-3 py-1.5"
            >
              {user?.role === "technician" ? (
                <Edit3 className="w-4 h-4" />
              ) : (
                <Eye className="w-4 h-4" />
              )}
              <span className="font-medium">{user?.name}</span>
              <span className="text-xs opacity-75">({user?.role})</span>
            </Badge>
          </div>
        </div>
      </header>

      <main className="container mx-auto px-6 py-8">
        <div className="max-w-7xl mx-auto space-y-8">
          {/* Page Header */}
          <div className="text-center space-y-4">
            <h2 className="text-4xl font-bold bg-gradient-to-r from-gray-900 via-blue-800 to-indigo-800 bg-clip-text text-transparent">
              Machine Operations Center
            </h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Monitor and manage your coffee vending machines across corporate
              locations with real-time analytics and performance insights.
            </p>
          </div>

          {/* Navigation Progress */}
          <div className="flex items-center justify-center">
            <div className="flex items-center space-x-6">
              {steps.map((step, index) => (
                <React.Fragment key={step.id}>
                  <div className="flex flex-col items-center space-y-3">
                    <div
                      className={`w-16 h-16 rounded-2xl flex items-center justify-center transition-all duration-300 ${
                        step.completed
                          ? "bg-gradient-to-br from-blue-500 to-indigo-600 text-white shadow-lg"
                          : index === currentStep
                            ? "bg-gradient-to-br from-blue-100 to-indigo-100 text-blue-600 border-2 border-blue-300 shadow-md"
                            : "bg-gray-100 text-gray-400"
                      }`}
                    >
                      {step.icon}
                    </div>
                    <div className="text-center">
                      <p
                        className={`text-sm font-semibold ${step.completed || index === currentStep ? "text-gray-900" : "text-gray-500"}`}
                      >
                        {step.title}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {step.description}
                      </p>
                    </div>
                  </div>
                  {index < steps.length - 1 && (
                    <ChevronRight
                      className={`w-6 h-6 ${step.completed ? "text-blue-500" : "text-gray-300"}`}
                    />
                  )}
                </React.Fragment>
              ))}
            </div>
          </div>

          {/* Step 1: Location Selection */}
          {currentStep === 0 && (
            <Card className="shadow-xl border-0 bg-white/70 backdrop-blur-sm">
              <CardHeader className="text-center pb-6">
                <CardTitle className="text-2xl font-bold flex items-center justify-center gap-3">
                  <MapPin className="w-6 h-6 text-blue-600" />
                  Select Corporate Location
                </CardTitle>
                <CardDescription className="text-base">
                  Choose your operational city to view office locations and
                  performance metrics
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-4xl mx-auto">
                  {corporateLocations.map((location, index) => (
                    <Card
                      key={location.name}
                      className="cursor-pointer transition-all duration-300 hover:scale-105 hover:shadow-2xl border-2 hover:border-blue-300 group bg-gradient-to-br from-white to-blue-50/50"
                      onClick={() => handleLocationSelect(location.name)}
                    >
                      <CardHeader className="pb-4">
                        <div className="flex items-center justify-between">
                          <CardTitle className="text-xl font-bold text-gray-900 group-hover:text-blue-600 transition-colors">
                            {location.name}
                          </CardTitle>
                          <Badge
                            className={`${
                              location.status === "excellent"
                                ? "bg-green-100 text-green-700"
                                : location.status === "good"
                                  ? "bg-blue-100 text-blue-700"
                                  : "bg-orange-100 text-orange-700"
                            }`}
                          >
                            {location.status === "excellent"
                              ? "🌟 Excellent"
                              : location.status === "good"
                                ? "👍 Good"
                                : "⚠️ Needs Attention"}
                          </Badge>
                        </div>
                      </CardHeader>
                      <CardContent className="space-y-4">
                        <div className="grid grid-cols-2 gap-4">
                          <div className="text-center p-3 bg-white/60 rounded-lg">
                            <div className="text-2xl font-bold text-blue-600">
                              {location.totalMachines}
                            </div>
                            <div className="text-xs text-muted-foreground">
                              Total Machines
                            </div>
                          </div>
                        </div>


                        <div className="pt-3 border-t">
                          <div className="flex items-center justify-between">
                            <span className="text-sm text-muted-foreground">
                              {location.offices.length} Office Locations
                            </span>
                            <ChevronRight className="w-4 h-4 text-blue-500 group-hover:translate-x-1 transition-transform" />
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}

          {/* Step 2: Office Selection with Admin Analytics */}
          {currentStep === 1 && selectedLocation && (
            <div className="space-y-6">
              {/* Admin Location Performance Overview */}
              {user?.role === "admin" && (
                <Card className="shadow-xl border-0 bg-gradient-to-r from-blue-600 to-indigo-600 text-white">
                  <CardHeader>
                    <CardTitle className="text-2xl font-bold flex items-center gap-3">
                      <BarChart3 className="w-6 h-6" />
                      {selectedLocation} - Performance Overview
                    </CardTitle>
                    <CardDescription className="text-blue-100">
                      Real-time analytics and key performance indicators for {" "}
                      {selectedLocation} operations
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                      {(() => {
                        const locationData = getLocationData(selectedLocation);
                        const totalCups =
                          locationData?.offices.reduce(
                            (sum, office) =>
                              sum + office.performance.weeklyCups,
                            0,
                          ) || 0;
                        const totalAlerts =
                          locationData?.offices.reduce(
                            (sum, office) => sum + office.performance.alerts,
                            0,
                          ) || 0;

                        return (
                          <>
                            <div className="text-center">
                              <div className="text-3xl font-bold text-white">
                                {totalCups.toLocaleString()}
                              </div>
                              <div className="text-blue-100">
                                Weekly Cups Served
                              </div>
                              <div className="text-xs text-blue-200 mt-1">
                                ↗️ +12% vs last week
                              </div>
                            </div>
                            <div className="text-center">
                              <div className="text-3xl font-bold text-white">
                                {locationData?.offices.length || 0}
                              </div>
                              <div className="text-blue-100">
                                Office Locations
                              </div>
                              <div className="text-xs text-blue-200 mt-1">
                                🏢 Active offices
                              </div>
                            </div>
                            <div className="text-center">
                              <div className="text-3xl font-bold text-white">
                                {totalAlerts}
                              </div>
                              <div className="text-blue-100">Active Alerts</div>
                              <div className="text-xs text-blue-200 mt-1">
                                ⚠️ Requires attention
                              </div>
                            </div>
                          </>
                        );
                      })()}
                    </div>
                  </CardContent>
                </Card>
              )}

              {/* Office Selection */}
              <Card className="shadow-xl border-0 bg-white/70 backdrop-blur-sm">
                <CardHeader className="text-center pb-6">
                  <CardTitle className="text-2xl font-bold flex items-center justify-center gap-3">
                    <Building className="w-6 h-6 text-blue-600" />
                    Select Office in {selectedLocation}
                  </CardTitle>
                  <CardDescription className="text-base">
                    Choose your office location to view machine performance and
                    analytics
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    {offices.map((office, index) => (
                      <Card
                        key={office.name}
                        className="cursor-pointer transition-all duration-300 hover:scale-102 hover:shadow-lg border-2 hover:border-blue-300 group"
                        onClick={() => handleOfficeSelect(office.name)}
                      >
                        <CardContent className="p-6">
                          <div className="flex items-center justify-between">
                            <div className="flex-1">
                              <h3 className="text-lg font-bold text-gray-900 group-hover:text-blue-600 transition-colors">
                                {office.name}
                              </h3>
                              <p className="text-sm text-muted-foreground mb-3">
                                {office.address}
                              </p>

                              <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
                                <div className="text-center">
                                  <div className="text-lg font-bold text-blue-600">
                                    {office.performance.weeklyCups}
                                  </div>
                                  <div className="text-xs text-muted-foreground">
                                    Weekly Cups
                                  </div>
                                </div>
                                <div className="text-center">
                                  <div className="text-lg font-bold text-purple-600">
                                    {office.machines.length}
                                  </div>
                                  <div className="text-xs text-muted-foreground">
                                    Machines
                                  </div>
                                </div>
                                <div className="text-center">
                                  <div
                                    className={`text-lg font-bold ${office.performance.alerts > 0 ? "text-red-500" : "text-green-500"}`}
                                  >
                                    {office.performance.alerts}
                                  </div>
                                  <div className="text-xs text-muted-foreground">
                                    Alerts
                                  </div>
                                </div>
                              </div>
                            </div>

                            <div className="flex items-center gap-3">
                              <Badge variant="outline" className="gap-1">
                                <Coffee className="w-3 h-3" />
                                {office.machines.length} Machines
                              </Badge>
                              <ChevronRight className="w-5 h-5 text-blue-500 group-hover:translate-x-1 transition-transform" />
                            </div>
                          </div>
                        </CardContent>
                      </Card>
                    ))}
                  </div>
                </CardContent>
              </Card>
            </div>
          )}

          {/* Step 3: Machine Analytics */}
          {currentStep === 2 && selectedOffice && (
            <div className="space-y-6">
              {/* Back actions */}
              <div className="flex items-center gap-3">
                <Button
                  variant="ghost"
                  onClick={() => {
                    setSelectedMachine("");
                    setCurrentStep(1);
                    window.scrollTo({ top: 0, behavior: "smooth" });
                  }}
                  className="gap-2"
                >
                  <ArrowLeft className="w-4 h-4" /> Back to Offices
                </Button>
                <Button
                  variant="ghost"
                  onClick={() => {
                    setSelectedMachine("");
                    setSelectedOffice("");
                    setCurrentStep(0);
                    window.scrollTo({ top: 0, behavior: "smooth" });
                  }}
                  className="gap-2"
                >
                  <MapPin className="w-4 h-4" /> Back to Locations
                </Button>
              </div>

              {/* Office Performance Dashboard */}
              <Card className="shadow-xl border-0 bg-gradient-to-r from-indigo-600 to-purple-600 text-white">
                <CardHeader>
                  <CardTitle className="text-2xl font-bold flex items-center gap-3">
                    <Target className="w-6 h-6" />
                    {selectedOffice} - Machine Performance
                  </CardTitle>
                  <CardDescription className="text-indigo-100">
                    Detailed analytics and machine status for {selectedOffice}
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                    {(() => {
                      const officeData = getOfficeData(selectedOffice);
                      const operational =
                        officeData?.machines.filter(
                          (m) => m.status === "operational",
                        ).length || 0;
                      const maintenance =
                        officeData?.machines.filter(
                          (m) => m.status === "maintenance",
                        ).length || 0;
                      const offline =
                        officeData?.machines.filter(
                          (m) => m.status === "offline",
                        ).length || 0;
                      const total = operational + maintenance + offline;

                      return (
                        <>
                          <div className="text-center">
                            <div className="text-3xl font-bold text-white">
                              {operational}/{total}
                            </div>
                            <div className="text-indigo-100">Operational</div>
                            <div className="text-xs text-indigo-200 mt-1">
                              ✅ Running smoothly
                            </div>
                          </div>
                          <div className="text-center">
                            <div className="text-3xl font-bold text-white">
                              {maintenance}
                            </div>
                            <div className="text-indigo-100">Maintenance</div>
                            <div className="text-xs text-indigo-200 mt-1">
                              🔧 Under service
                            </div>
                          </div>
                          <div className="text-center">
                            <div className="text-3xl font-bold text-white">
                              {offline}
                            </div>
                            <div className="text-indigo-100">Offline</div>
                            <div className="text-xs text-indigo-200 mt-1">
                              ❌ Needs attention
                            </div>
                          </div>
                          <div className="text-center">
                            <div className="text-3xl font-bold text-white">
                              {Math.round((officeData?.performance.weeklyCups || 0) / 7)}
                            </div>
                            <div className="text-indigo-100">
                              Daily Average
                            </div>
                            <div className="text-xs text-indigo-200 mt-1">
                              ☕ Cups per day
                            </div>
                          </div>
                        </>
                      );
                    })()}
                  </div>
                </CardContent>
              </Card>

              {/* Machine Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {/* Add Machine Card - Admin Only */}
                {user?.role === "admin" && (
                  <Card
                    className="cursor-pointer transition-all duration-300 hover:scale-105 hover:shadow-xl border-2 border-dashed border-primary/30 hover:border-primary bg-gradient-to-br from-primary/5 to-primary/10 hover:from-primary/10 hover:to-primary/20"
                    onClick={() => setIsAddMachineModalOpen(true)}
                  >
                    <CardContent className="flex flex-col items-center justify-center h-full min-h-[300px] space-y-4">
                      <div className="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center">
                        <Plus className="w-8 h-8 text-primary" />
                      </div>
                      <div className="text-center space-y-2">
                        <h3 className="text-lg font-semibold text-primary">Add New Machine</h3>
                        <p className="text-sm text-muted-foreground">
                          Install a new coffee machine in {selectedOffice}
                        </p>
                      </div>
                      <div className="text-xs text-muted-foreground bg-primary/10 px-3 py-1 rounded-full">
                        Admin Only
                      </div>
                    </CardContent>
                  </Card>
                )}

                {machines.map((machine, index) => (
                  <Card
                    key={machine.id}
                    className={`cursor-pointer transition-all duration-300 hover:scale-105 hover:shadow-xl border-2 ${
                      selectedMachine === machine.id
                        ? "border-blue-500 bg-blue-50"
                        : "hover:border-blue-300 bg-white"
                    }`}
                    onClick={() => handleMachineSelect(machine.id)}
                  >
                    <CardHeader className="pb-3">
                      <div className="flex items-center justify-between">
                        <CardTitle className="text-lg font-bold">
                          {machine.name}
                        </CardTitle>
                        <div className="flex items-center gap-2">
                          {getStatusIcon(machine.status)}
                          <Badge
                            className={
                              machine.status === "operational"
                                ? "bg-green-100 text-green-700"
                                : machine.status === "maintenance"
                                  ? "bg-orange-100 text-orange-700"
                                  : "bg-red-100 text-red-700"
                            }
                          >
                            {machine.status === "offline" ? "Not Functional" : machine.status}
                          </Badge>
                        </div>
                      </div>
                      <p className="text-sm text-muted-foreground font-mono">
                        {machine.id}
                      </p>
                      {machine.status === "offline" && (
                        <p className="text-sm text-red-600 font-medium mt-1">
                          ⚠️ Machine is not operational
                        </p>
                      )}
                    </CardHeader>
                    <CardContent className="space-y-4">
                      {/* Performance Metrics */}
                      <div className="grid grid-cols-2 gap-3">
                        <div className="text-center p-2 bg-gray-50 rounded">
                          <div className="text-lg font-bold text-blue-600">
                            {machine.performance?.dailyCups ?? machine.usage?.dailyCups ?? 0}
                          </div>
                          <div className="text-xs text-muted-foreground">
                            Daily Cups
                          </div>
                        </div>
                      </div>

                      {/* Supply Levels */}
                      <div className="space-y-2">
                        <h4 className="text-sm font-semibold">Supply Levels</h4>
                        <div className="grid grid-cols-2 gap-2 text-xs">
                          <div className="flex items-center justify-between">
                            <span className="flex items-center gap-1">
                              <Droplets className="w-3 h-3 text-blue-500" />
                              Water
                            </span>
                            <span
                              className={
                                (machine.performance?.supplies?.water ?? machine.supplies?.water ?? 0) > 50
                                  ? "text-green-600"
                                  : "text-red-500"
                              }
                            >
                              {(machine.performance?.supplies?.water ?? machine.supplies?.water ?? 0)}%
                            </span>
                          </div>
                          <div className="flex items-center justify-between">
                            <span className="flex items-center gap-1">
                              <Coffee className="w-3 h-3 text-brown-600" />
                              Coffee
                            </span>
                            <span
                              className={
                                (machine.performance?.supplies?.coffee ?? machine.supplies?.coffee ?? machine.supplies?.coffeeBeans ?? 0) > 50
                                  ? "text-green-600"
                                  : "text-red-500"
                              }
                            >
                              {(machine.performance?.supplies?.coffee ?? machine.supplies?.coffee ?? machine.supplies?.coffeeBeans ?? 0)}%
                            </span>
                          </div>
                          <div className="flex items-center justify-between">
                            <span>Milk</span>
                            <span
                              className={
                                (machine.performance?.supplies?.milk ?? machine.supplies?.milk ?? 0) > 50
                                  ? "text-green-600"
                                  : "text-red-500"
                              }
                            >
                              {(machine.performance?.supplies?.milk ?? machine.supplies?.milk ?? 0)}%
                            </span>
                          </div>
                          <div className="flex items-center justify-between">
                            <span>Sugar</span>
                            <span
                              className={
                                (machine.performance?.supplies?.sugar ?? machine.supplies?.sugar ?? 0) > 50
                                  ? "text-green-600"
                                  : "text-red-500"
                              }
                            >
                              {(machine.performance?.supplies?.sugar ?? machine.supplies?.sugar ?? 0)}%
                            </span>
                          </div>
                        </div>
                      </div>

                      {/* Efficiency */}
                      <div className="space-y-1">
                        <div className="flex items-center justify-between">
                          <span className="text-sm font-medium">
                            Efficiency
                          </span>
                          <span className="text-sm font-bold text-purple-600">
                            {machine.performance?.efficiency ?? 100}%
                          </span>
                        </div>
                        <Progress
                          value={machine.performance?.efficiency ?? 100}
                          className="h-2"
                        />
                      </div>

                      {/* Admin Controls */}
                      {user?.role === "admin" && (
                        <div className="pt-2 border-t border-gray-100">
                          <Button
                            variant="destructive"
                            size="sm"
                            className="w-full gap-2 text-xs"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleDeleteMachine(machine);
                            }}
                          >
                            <Trash2 className="w-3 h-3" />
                            Delete Machine
                          </Button>
                        </div>
                      )}
                    </CardContent>
                  </Card>
                ))}
              </div>

              {/* Machine Management Button */}
              {selectedMachine && (
                <Card className="border-2 border-blue-500 bg-gradient-to-r from-blue-50 to-indigo-50">
                  <CardContent className="pt-6">
                    <div className="text-center space-y-4">
                      <h3 className="text-2xl font-bold text-blue-800">
                        Machine Selected
                      </h3>
                      <p className="text-muted-foreground">
                        {getMachineData(selectedMachine)?.name} is ready for{" "}
                        {user?.role === "technician"
                          ? "management and maintenance"
                          : "detailed monitoring"}
                      </p>
                      <Link to={`/machine?id=${selectedMachine}`}>
                        <Button
                          size="lg"
                          className="bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white font-semibold px-8 py-3"
                        >
                          <Settings className="w-5 h-5 mr-2" />
                          {user?.role === "technician"
                            ? "Manage Machine"
                            : "View Machine Details"}
                        </Button>
                      </Link>
                    </div>
                  </CardContent>
                </Card>
              )}
            </div>
          )}
        </div>
      </main>

      {/* Add Machine Modal */}
      <AddMachineModal
        isOpen={isAddMachineModalOpen}
        onClose={() => setIsAddMachineModalOpen(false)}
        onSubmit={handleAddMachine}
        selectedOffice={selectedOffice}
        isLoading={isAddingMachine}
      />

      {/* Delete Machine Dialog */}
      <DeleteMachineDialog
        isOpen={isDeleteMachineDialogOpen}
        onClose={() => {
          setIsDeleteMachineDialogOpen(false);
          setMachineToDelete(null);
        }}
        onConfirm={handleConfirmDeleteMachine}
        machine={machineToDelete}
        isLoading={isDeletingMachine}
      />
    </div>
  );
}
