import React, { useState, useEffect, useCallback } from "react";
import { useParams, Link } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Coffee, Droplets, Milk, Candy, Eye, Edit3, MapPin, Building, Activity, Plus } from "lucide-react";
import { pathToOfficeName } from "@/lib/officeRouting";
import InteractiveBreadcrumb from "@/components/InteractiveBreadcrumb";
import AddMachineModal from "@/components/AddMachineModal";
import { dataManager } from "@/lib/dataManager";
import { apiClient } from "@/lib/api";

export default function OfficeOverview() {
  const { officePath } = useParams();
  const { user, logout } = useAuth();
  const [isAddMachineModalOpen, setIsAddMachineModalOpen] = useState(false);
  const [isAddingMachine, setIsAddingMachine] = useState(false);
  const [machines, setMachines] = useState([]);

  if (!officePath) return <div>Office not found</div>;
  const officeName = pathToOfficeName(officePath);

  const getOfficeMachines = () => [];

  const loadMachineData = useCallback(() => {
    let storedMachines = dataManager.getAllMachinesFromSharedStorage().filter(m => m.office === officeName);
    if (storedMachines.length === 0) {
      const demoMachines = getOfficeMachines();
      demoMachines.forEach(machine => {
        dataManager.saveMachine({ ...machine, electricityStatus: machine.electricityStatus || "available", office: officeName, alerts: machine.alerts || [], recentRefills: machine.recentRefills || [] });
      });
      storedMachines = dataManager.getAllMachinesFromSharedStorage().filter(m => m.office === officeName);
    }
    setMachines(storedMachines);
  }, [officeName]);

  useEffect(() => { loadMachineData(); }, [loadMachineData]);
  useEffect(() => {
    const handleStorageChange = (e) => { if (e.key === 'coffee_shared_machines' && e.newValue !== e.oldValue) loadMachineData(); };
    const handleWindowFocus = () => loadMachineData();
    const handleVisibilityChange = () => { if (!document.hidden) loadMachineData(); };
    const handleMachineDataChanged = () => loadMachineData();
    const handleForceRefresh = () => loadMachineData();
    window.addEventListener('storage', handleStorageChange);
    window.addEventListener('focus', handleWindowFocus);
    document.addEventListener('visibilitychange', handleVisibilityChange);
    window.addEventListener('machineDataChanged', handleMachineDataChanged);
    window.addEventListener('forceRefreshMachines', handleForceRefresh);
    const intervalId = setInterval(loadMachineData, 3000);
    return () => {
      window.removeEventListener('storage', handleStorageChange);
      window.removeEventListener('focus', handleWindowFocus);
      document.removeEventListener('visibilitychange', handleVisibilityChange);
      window.removeEventListener('machineDataChanged', handleMachineDataChanged);
      window.removeEventListener('forceRefreshMachines', handleForceRefresh);
      clearInterval(intervalId);
    };
  }, [loadMachineData]);

  const canEdit = user?.role === "technician";
  const canAddMachines = user?.role === "admin";

  const getSupplyColor = (p) => (p > 60 ? "text-green-600" : p > 30 ? "text-orange-500" : "text-red-600");

  const handleAddMachine = async (machineData) => {
    setIsAddingMachine(true);
    try {
      const officePrefix = officeName === "Hinjewadi IT Park" ? "HIJ" : officeName === "Koregaon Park Office" ? "KOR" : officeName === "Viman Nagar Tech Hub" ? "VIM" : officeName === "Bandra Kurla Complex" ? "BKC" : officeName === "Lower Parel Financial" ? "LOW" : officeName === "Andheri Tech Center" ? "AND" : "NEW";
      const machineNumber = (machines.length + 1).toString().padStart(3, '0');
      const newMachineId = `${officePrefix}-${machineNumber}`;
      const newMachine = { id: newMachineId, machineId: newMachineId, name: machineData.name, location: machineData.location, status: machineData.status, office: officeName, powerStatus: "online", electricityStatus: "available", lastPowerUpdate: new Date().toISOString().slice(0, 19).replace('T', ' '), lastMaintenance: new Date().toISOString().slice(0, 10), nextMaintenance: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10), supplies: { water: machineData.supplies.water, milk: machineData.supplies.milk, coffeeBeans: machineData.supplies.coffeeBeans, coffee: machineData.supplies.coffeeBeans, sugar: machineData.supplies.sugar }, maintenance: machineData.maintenance, usage: { dailyCups: 0, weeklyCups: 0 }, notes: machineData.notes || "New machine installation", alerts: [], recentRefills: [] };
      dataManager.saveMachine(newMachine);
      try { const backendData = dataManager.mapFrontendToBackend(newMachine); await apiClient.createMachine(backendData); } catch (error) {}
      loadMachineData(); setIsAddMachineModalOpen(false);
    } catch (error) {
    } finally { setIsAddingMachine(false); }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-orange-50 to-amber-50">
      <header className="border-b bg-white/80 backdrop-blur-sm sticky top-0 z-50">
        <div className="container mx-auto px-4 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <InteractiveBreadcrumb backUrl={user?.role === "admin" ? "/dashboard" : undefined} onBack={user?.role === "technician" ? () => logout() : undefined} className="flex-1" />
            <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center animate-pulse"><Coffee className="w-5 h-5 text-primary-foreground" /></div>
            <h1 className="text-xl font-bold text-coffee-brown">{officeName} - Coffee Machines</h1>
          </div>
          <div className="flex items-center gap-3">
            {user?.officeName && (
              <div className="flex items-center gap-2">
                <Badge variant="outline" className="gap-1 animate-fadeIn"><Building className="w-3 h-3" />{officeName}</Badge>
                {user.city && (<Badge variant="secondary" className="gap-1 animate-fadeIn"><MapPin className="w-3 h-3" />{user.city.charAt(0).toUpperCase() + user.city.slice(1)}</Badge>)}
              </div>
            )}
            <Badge variant={canEdit ? "default" : "secondary"} className="gap-1 animate-fadeIn">{canEdit ? <Edit3 className="w-3 h-3" /> : <Eye className="w-3 h-3" />}{user?.role === "technician" ? "Technician" : "Admin"}</Badge>
            {canAddMachines && (<Button onClick={() => setIsAddMachineModalOpen(true)} className="gap-2"><Plus className="w-4 h-4" />Add Machine</Button>)}
          </div>
        </div>
      </header>

      <div className="container mx-auto px-4 py-6 space-y-6">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2"><Building className="w-5 h-5" />Office Overview - {officeName}</CardTitle>
            <CardDescription>Total {machines.length} coffee machines available</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="text-center p-6"><div className="text-3xl font-bold text-primary mb-2">{machines.length}</div><div className="text-muted-foreground">Coffee Machines Available</div></div>
          </CardContent>
        </Card>

        <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
          {machines.map((machine) => (
            <Card key={machine.id} className="hover:shadow-lg transition-shadow">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <CardTitle className="text-lg">{machine.name}</CardTitle>
                    <CardDescription className="flex items-center gap-1 mt-1"><MapPin className="w-3 h-3" />{machine.location}</CardDescription>
                  </div>
                  <div className="flex items-center gap-2">
                    <div className={`w-3 h-3 rounded-full ${machine.status === "operational" ? "bg-green-500" : machine.status === "maintenance" ? "bg-orange-500" : "bg-red-500"}`} />
                    <Badge className={machine.status === "operational" ? "bg-green-100 text-green-700" : machine.status === "maintenance" ? "bg-orange-100 text-orange-700" : "bg-red-100 text-red-700"}>{machine.status === "offline" || machine.powerStatus === "offline" ? "Not Functional" : machine.status}</Badge>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                {(machine.status === "offline" || machine.powerStatus === "offline" || machine.electricityStatus === "unavailable") && (
                  <div className="bg-red-50 border border-red-200 rounded-lg p-3 mb-4"><p className="text-sm text-red-800 font-medium flex items-center gap-2">⚠️ Machine is not operational{machine.electricityStatus === "unavailable" && " (No electricity)"}</p></div>
                )}
                <div className="space-y-2">
                  <div className="text-sm font-medium">Supply Levels</div>
                  <div className="grid grid-cols-2 gap-2">
                    <div className="space-y-1">
                      <div className="flex justify-between text-xs"><span className="flex items-center gap-1"><Droplets className="w-3 h-3 text-blue-500" />Water</span><span className={getSupplyColor(machine.supplies.water)}>{machine.supplies.water}%</span></div>
                      <Progress value={machine.supplies.water} className="h-1" />
                    </div>
                    <div className="space-y-1">
                      <div className="flex justify-between text-xs"><span className="flex items-center gap-1"><Milk className="w-3 h-3 text-purple-500" />Milk</span><span className={getSupplyColor(machine.supplies.milk)}>{machine.supplies.milk}%</span></div>
                      <Progress value={machine.supplies.milk} className="h-1" />
                    </div>
                    <div className="space-y-1">
                      <div className="flex justify-between text-xs"><span className="flex items-center gap-1"><Coffee className="w-3 h-3 text-brown-500" />Beans</span><span className={getSupplyColor(machine.supplies.coffeeBeans)}>{machine.supplies.coffeeBeans}%</span></div>
                      <Progress value={machine.supplies.coffeeBeans} className="h-1" />
                    </div>
                    <div className="space-y-1">
                      <div className="flex justify-between text-xs"><span className="flex items-center gap-1"><Candy className="w-3 h-3 text-pink-500" />Sugar</span><span className={getSupplyColor(machine.supplies.sugar)}>{machine.supplies.sugar}%</span></div>
                      <Progress value={machine.supplies.sugar} className="h-1" />
                    </div>
                  </div>
                </div>
                <div className="flex justify-between text-sm">
                  <div className="text-center"><div className="font-semibold text-lg">{machine?.usage?.dailyCups ?? 0}</div><div className="text-muted-foreground text-xs">Today</div></div>
                  <div className="text-center"><div className="font-semibold text-lg">{machine?.usage?.weeklyCups ?? 0}</div><div className="text-muted-foreground text-xs">This Week</div></div>
                </div>
                <Link to={`/machine/${machine.id}`}>
                  <Button className="w-full" variant={canEdit ? "default" : "outline"}>
                    <Activity className="w-4 h-4 mr-2" />{canEdit ? "Manage Machine" : "View Details"}
                  </Button>
                </Link>
              </CardContent>
            </Card>
          ))}
        </div>

        {machines.length === 0 && (
          <Card>
            <CardContent className="text-center py-8">
              <Coffee className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
              <h3 className="text-lg font-semibold mb-2">No machines found</h3>
              <p className="text-muted-foreground mb-4">No coffee machines are currently registered for {officeName}.</p>
              {canAddMachines && (<Button onClick={() => setIsAddMachineModalOpen(true)} className="gap-2"><Plus className="w-4 h-4" />Add First Machine</Button>)}
            </CardContent>
          </Card>
        )}

        <AddMachineModal isOpen={isAddMachineModalOpen} onClose={() => setIsAddMachineModalOpen(false)} onSubmit={handleAddMachine} selectedOffice={officeName} isLoading={isAddingMachine} />
      </div>
    </div>
  );
}
