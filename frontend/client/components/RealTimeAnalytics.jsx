import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { 
  Activity, 
  TrendingUp, 
  AlertTriangle, 
  CheckCircle, 
  RefreshCw,
  Wifi,
  WifiOff,
  Database,
  Server,
  Coffee,
  Droplets,
  Thermometer,
  Zap
} from 'lucide-react';
import { toast } from 'sonner';
import realTimeMQTT from '@/lib/realTimeMqtt';
import backendAPI from '@/lib/backendApi';

export default function RealTimeAnalytics({ userRole, selectedFacility }) {
  const [mqttData, setMqttData] = useState(new Map());
  const [simulatorStats, setSimulatorStats] = useState(null);
  const [connectionStatus, setConnectionStatus] = useState({
    mqtt: 'disconnected',
    backend: 'disconnected'
  });
  const [isLoading, setIsLoading] = useState(false);
  const [refreshInterval, setRefreshInterval] = useState(30000); // 30 seconds for facility, 60 for admin

  useEffect(() => {
    // Set refresh interval based on user role
    const interval = userRole === 'ADMIN' ? 30000 : 30000; // Both use 30 seconds to match MQTT simulator
    setRefreshInterval(interval);

    // Initialize MQTT connection
    const initializeMQTT = () => {
      try {
        realTimeMQTT.connect();
        
        // Subscribe to simulator stats
        realTimeMQTT.subscribe('simulator:stats', (data) => {
          setSimulatorStats(data);
        });

        // Subscribe to individual machine updates
        realTimeMQTT.subscribe('coffeeMachine/+/update', (data) => {
          setMqttData(prev => new Map(prev.set(data.machineId, data)));
        });

        // Subscribe to connection status
        realTimeMQTT.subscribe('connection', (data) => {
          setConnectionStatus(prev => ({ ...prev, mqtt: data.status }));
        });

        setConnectionStatus(prev => ({ ...prev, mqtt: 'connected' }));
      } catch (error) {
        console.error('MQTT initialization error:', error);
        setConnectionStatus(prev => ({ ...prev, mqtt: 'error' }));
      }
    };

    // Check backend connection
    const checkBackendConnection = async () => {
      try {
        const isConnected = await backendAPI.checkConnection();
        setConnectionStatus(prev => ({ ...prev, backend: isConnected ? 'connected' : 'disconnected' }));
      } catch (error) {
        setConnectionStatus(prev => ({ ...prev, backend: 'error' }));
      }
    };

    initializeMQTT();
    checkBackendConnection();

    // Set up periodic refresh - match MQTT simulator interval
    const refreshInterval = setInterval(() => {
      checkBackendConnection();
      // Refresh MQTT data every 30 seconds to match simulator
      if (realTimeMQTT.isConnected) {
        realTimeMQTT.triggerDataGeneration().catch(console.error);
      }
    }, 30000); // Fixed to 30 seconds

    return () => {
      clearInterval(refreshInterval);
      realTimeMQTT.disconnect();
    };
  }, [userRole]);

  // Get connection status badge
  const getConnectionBadge = (status) => {
    switch (status) {
      case 'connected':
        return <Badge variant="default" className="bg-green-500"><Wifi className="w-3 h-3 mr-1" />Connected</Badge>;
      case 'disconnected':
        return <Badge variant="secondary"><WifiOff className="w-3 h-3 mr-1" />Disconnected</Badge>;
      case 'error':
        return <Badge variant="destructive"><AlertTriangle className="w-3 h-3 mr-1" />Error</Badge>;
      default:
        return <Badge variant="outline">Unknown</Badge>;
    }
  };

  // Get machine status badge
  const getMachineStatusBadge = (status) => {
    switch (status) {
      case 'ON':
        return <Badge variant="default" className="bg-green-500"><CheckCircle className="w-3 h-3 mr-1" />Operational</Badge>;
      case 'OFF':
        return <Badge variant="secondary"><Activity className="w-3 h-3 mr-1" />Offline</Badge>;
      case 'ERROR':
        return <Badge variant="destructive"><AlertTriangle className="w-3 h-3 mr-1" />Error</Badge>;
      default:
        return <Badge variant="outline">{status}</Badge>;
    }
  };

  // Get supply level color
  const getSupplyLevelColor = (level) => {
    if (level >= 70) return 'text-green-600';
    if (level >= 30) return 'text-yellow-600';
    return 'text-red-600';
  };

  // Get supply level variant
  const getSupplyLevelVariant = (level) => {
    if (level >= 70) return 'default';
    if (level >= 30) return 'secondary';
    return 'destructive';
  };

  // Handle manual data refresh
  const handleManualRefresh = async () => {
    setIsLoading(true);
    try {
      await realTimeMQTT.triggerDataGeneration();
      toast.success('Data refresh triggered successfully');
    } catch (error) {
      toast.error('Failed to refresh data');
    } finally {
      setIsLoading(false);
    }
  };

  // Handle machine refill
  const handleRefill = async (machineId, supplyType) => {
    try {
      const machine = mqttData.get(machineId);
      if (!machine) return;

      const newLevel = 100; // Refill to 100%
      
      // Update local state immediately
      setMqttData(prev => {
        const updated = new Map(prev);
        const updatedMachine = { ...machine, [supplyType]: newLevel };
        updated.set(machineId, updatedMachine);
        return updated;
      });

      // Try to update backend
      try {
        await backendAPI.updateMachine(machineId, {
          supplies: { [supplyType]: newLevel },
          lastRefill: new Date().toISOString(),
        });
        toast.success(`${supplyType} refilled successfully`);
      } catch (error) {
        toast.success(`${supplyType} refilled (local update)`);
      }
    } catch (error) {
      toast.error('Refill failed');
    }
  };

  // Calculate overall statistics
  const overallStats = React.useMemo(() => {
    if (!simulatorStats || !simulatorStats.machines) return null;

    const machines = Object.values(simulatorStats.machines);
    const totalMachines = machines.length;
    const operationalMachines = machines.filter(m => m.status === 'ON').length;
    const errorMachines = machines.filter(m => m.status === 'ERROR').length;
    
    const avgWaterLevel = machines.reduce((sum, m) => sum + (m.waterLevel || 0), 0) / totalMachines;
    const avgMilkLevel = machines.reduce((sum, m) => sum + (m.milkLevel || 0), 0) / totalMachines;
    const avgBeansLevel = machines.reduce((sum, m) => sum + (m.beansLevel || 0), 0) / totalMachines;
    const avgTemperature = machines.reduce((sum, m) => sum + (m.temperature || 0), 0) / totalMachines;

    return {
      totalMachines,
      operationalMachines,
      errorMachines,
      uptimePercentage: (operationalMachines / totalMachines) * 100,
      avgWaterLevel: Math.round(avgWaterLevel),
      avgMilkLevel: Math.round(avgMilkLevel),
      avgBeansLevel: Math.round(avgBeansLevel),
      avgTemperature: Math.round(avgTemperature * 10) / 10,
    };
  }, [simulatorStats]);

  return (
    <div className="space-y-6">
      {/* Connection Status */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium flex items-center">
              <Database className="w-4 h-4 mr-2" />
              Backend API
            </CardTitle>
          </CardHeader>
          <CardContent>
            {getConnectionBadge(connectionStatus.backend)}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium flex items-center">
              <Server className="w-4 h-4 mr-2" />
              MQTT Worker
            </CardTitle>
          </CardHeader>
          <CardContent>
            {getConnectionBadge(connectionStatus.mqtt)}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium flex items-center">
              <RefreshCw className="w-4 h-4 mr-2" />
              Refresh Interval
            </CardTitle>
          </CardHeader>
          <CardContent>
            <Badge variant="outline">
              {refreshInterval / 1000}s
            </Badge>
          </CardContent>
        </Card>
      </div>

      {/* Overall Statistics */}
      {overallStats && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center justify-between">
              <span>System Overview</span>
              <Button 
                onClick={handleManualRefresh} 
                disabled={isLoading}
                size="sm"
                variant="outline"
              >
                <RefreshCw className={`w-4 h-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
                Refresh
              </Button>
            </CardTitle>
            <CardDescription>
              Real-time system performance metrics
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div className="text-center">
                <div className="text-2xl font-bold text-blue-600">{overallStats.totalMachines}</div>
                <div className="text-sm text-gray-600">Total Machines</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-green-600">{overallStats.operationalMachines}</div>
                <div className="text-sm text-gray-600">Operational</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-red-600">{overallStats.errorMachines}</div>
                <div className="text-sm text-gray-600">Errors</div>
              </div>
              <div className="text-center">
                <div className="text-2xl font-bold text-purple-600">{overallStats.uptimePercentage.toFixed(1)}%</div>
                <div className="text-sm text-gray-600">Uptime</div>
              </div>
            </div>

            <div className="mt-4 grid grid-cols-2 md:grid-cols-4 gap-4">
              <div>
                <div className="flex items-center justify-between text-sm mb-1">
                  <span>Water Level</span>
                  <span className={getSupplyLevelColor(overallStats.avgWaterLevel)}>
                    {overallStats.avgWaterLevel}%
                  </span>
                </div>
                <Progress value={overallStats.avgWaterLevel} variant={getSupplyLevelVariant(overallStats.avgWaterLevel)} />
              </div>
              <div>
                <div className="flex items-center justify-between text-sm mb-1">
                  <span>Milk Level</span>
                  <span className={getSupplyLevelColor(overallStats.avgMilkLevel)}>
                    {overallStats.avgMilkLevel}%
                  </span>
                </div>
                <Progress value={overallStats.avgMilkLevel} variant={getSupplyLevelVariant(overallStats.avgMilkLevel)} />
              </div>
              <div>
                <div className="flex items-center justify-between text-sm mb-1">
                  <span>Beans Level</span>
                  <span className={getSupplyLevelColor(overallStats.avgBeansLevel)}>
                    {overallStats.avgBeansLevel}%
                  </span>
                </div>
                <Progress value={overallStats.avgBeansLevel} variant={getSupplyLevelVariant(overallStats.avgBeansLevel)} />
              </div>
              <div>
                <div className="flex items-center justify-between text-sm mb-1">
                  <span>Temperature</span>
                  <span className="text-blue-600">{overallStats.avgTemperature}°C</span>
                </div>
                <Progress value={((overallStats.avgTemperature - 20) / 80) * 100} />
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Machine Details */}
      {simulatorStats && simulatorStats.machines && (
        <Card>
          <CardHeader>
            <CardTitle>Machine Status</CardTitle>
            <CardDescription>
              Real-time sensor data and machine status
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Tabs defaultValue="overview" className="w-full">
              <TabsList className="grid w-full grid-cols-3">
                <TabsTrigger value="overview">Overview</TabsTrigger>
                <TabsTrigger value="sensors">Sensors</TabsTrigger>
                <TabsTrigger value="alerts">Alerts</TabsTrigger>
              </TabsList>

              <TabsContent value="overview" className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {Object.entries(simulatorStats.machines).map(([machineKey, machine]) => {
                    const machineId = machineKey.replace('Machine_', '');
                    return (
                      <Card key={machineId} className="p-4">
                        <div className="flex items-center justify-between mb-3">
                          <h4 className="font-semibold">Machine {machineId}</h4>
                          {getMachineStatusBadge(machine.status)}
                        </div>
                        
                        <div className="space-y-2 text-sm">
                          <div className="flex justify-between">
                            <span>Usage Count:</span>
                            <span className="font-medium">{machine.usageCount}</span>
                          </div>
                          <div className="flex justify-between">
                            <span>Last Usage:</span>
                            <span className="text-gray-600">
                              {new Date(machine.lastUsage).toLocaleDateString()}
                            </span>
                          </div>
                        </div>
                      </Card>
                    );
                  })}
                </div>
              </TabsContent>

              <TabsContent value="sensors" className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {Object.entries(simulatorStats.machines).map(([machineKey, machine]) => {
                    const machineId = machineKey.replace('Machine_', '');
                    return (
                      <Card key={machineId} className="p-4">
                        <h4 className="font-semibold mb-3">Machine {machineId} Sensors</h4>
                        
                        <div className="space-y-3">
                          {/* Temperature */}
                          <div>
                            <div className="flex items-center justify-between text-sm mb-1">
                              <span className="flex items-center">
                                <Thermometer className="w-4 h-4 mr-2" />
                                Temperature
                              </span>
                              <span className="font-medium">{machine.temperature?.toFixed(1)}°C</span>
                            </div>
                            <Progress value={((machine.temperature - 20) / 80) * 100} />
                          </div>

                          {/* Water Level */}
                          <div>
                            <div className="flex items-center justify-between text-sm mb-1">
                              <span className="flex items-center">
                                <Droplets className="w-4 h-4 mr-2" />
                                Water Level
                              </span>
                              <div className="flex items-center space-x-2">
                                <span className={`font-medium ${getSupplyLevelColor(machine.waterLevel)}`}>
                                  {Math.round(machine.waterLevel)}%
                                </span>
                                <Button
                                  size="sm"
                                  variant="outline"
                                  onClick={() => handleRefill(machineId, 'waterLevel')}
                                  disabled={machine.waterLevel >= 80}
                                >
                                  Refill
                                </Button>
                              </div>
                            </div>
                            <Progress value={machine.waterLevel} variant={getSupplyLevelVariant(machine.waterLevel)} />
                          </div>

                          {/* Milk Level */}
                          <div>
                            <div className="flex items-center justify-between text-sm mb-1">
                              <span className="flex items-center">
                                <Coffee className="w-4 h-4 mr-2" />
                                Milk Level
                              </span>
                              <div className="flex items-center space-x-2">
                                <span className={`font-medium ${getSupplyLevelColor(machine.milkLevel)}`}>
                                  {Math.round(machine.milkLevel)}%
                                </span>
                                <Button
                                  size="sm"
                                  variant="outline"
                                  onClick={() => handleRefill(machineId, 'milkLevel')}
                                  disabled={machine.milkLevel >= 80}
                                >
                                  Refill
                                </Button>
                              </div>
                            </div>
                            <Progress value={machine.milkLevel} variant={getSupplyLevelVariant(machine.milkLevel)} />
                          </div>

                          {/* Beans Level */}
                          <div>
                            <div className="flex items-center justify-between text-sm mb-1">
                              <span className="flex items-center">
                                <Zap className="w-4 h-4 mr-2" />
                                Beans Level
                              </span>
                              <div className="flex items-center space-x-2">
                                <span className={`font-medium ${getSupplyLevelColor(machine.beansLevel)}`}>
                                  {Math.round(machine.beansLevel)}%
                                </span>
                                <Button
                                  size="sm"
                                  variant="outline"
                                  onClick={() => handleRefill(machineId, 'beansLevel')}
                                  disabled={machine.beansLevel >= 80}
                                >
                                  Refill
                                </Button>
                              </div>
                            </div>
                            <Progress value={machine.beansLevel} variant={getSupplyLevelVariant(machine.beansLevel)} />
                          </div>
                        </div>
                      </Card>
                    );
                  })}
                </div>
              </TabsContent>

              <TabsContent value="alerts" className="space-y-4">
                <div className="space-y-3">
                  {Object.entries(simulatorStats.machines).map(([machineKey, machine]) => {
                    const machineId = machineKey.replace('Machine_', '');
                    const alerts = [];
                    
                    if (machine.waterLevel < 20) alerts.push(`Low water level: ${Math.round(machine.waterLevel)}%`);
                    if (machine.milkLevel < 20) alerts.push(`Low milk level: ${Math.round(machine.milkLevel)}%`);
                    if (machine.beansLevel < 20) alerts.push(`Low beans level: ${Math.round(machine.beansLevel)}%`);
                    if (machine.status === 'ERROR') alerts.push('Machine error detected');
                    if (machine.temperature > 95) alerts.push(`High temperature: ${machine.temperature?.toFixed(1)}°C`);

                    if (alerts.length === 0) return null;

                    return (
                      <Card key={machineId} className="p-4 border-red-200 bg-red-50">
                        <h4 className="font-semibold text-red-800 mb-2">Machine {machineId} Alerts</h4>
                        <div className="space-y-1">
                          {alerts.map((alert, index) => (
                            <div key={index} className="flex items-center text-red-700">
                              <AlertTriangle className="w-4 h-4 mr-2" />
                              {alert}
                            </div>
                          ))}
                        </div>
                      </Card>
                    );
                  })}
                  
                  {Object.values(simulatorStats.machines).every(machine => {
                    const alerts = [];
                    if (machine.waterLevel < 20) alerts.push('water');
                    if (machine.milkLevel < 20) alerts.push('milk');
                    if (machine.beansLevel < 20) alerts.push('beans');
                    if (machine.status === 'ERROR') alerts.push('error');
                    if (machine.temperature > 95) alerts.push('temperature');
                    return alerts.length === 0;
                  }) && (
                    <Card className="p-4 border-green-200 bg-green-50">
                      <div className="flex items-center text-green-800">
                        <CheckCircle className="w-5 h-5 mr-2" />
                        All systems operational - No alerts
                      </div>
                    </Card>
                  )}
                </div>
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>
      )}

      {/* Simulator Controls (Admin Only) */}
      {userRole === 'ADMIN' && (
        <Card>
          <CardHeader>
            <CardTitle>Simulator Controls</CardTitle>
            <CardDescription>
              Control the MQTT sensor data simulator
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex space-x-4">
              <Button
                onClick={handleManualRefresh}
                disabled={isLoading}
                variant="outline"
              >
                <RefreshCw className={`w-4 h-4 mr-2 ${isLoading ? 'animate-spin' : ''}`} />
                Trigger Data Generation
              </Button>
              
              <Button
                onClick={() => realTimeMQTT.resetMachineStates()}
                variant="outline"
              >
                Reset Machine States
              </Button>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}