import React, { useState, useEffect } from "react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Wifi, WifiOff, Server, Activity, CheckCircle, AlertTriangle, RefreshCw } from "lucide-react";
import { apiClient } from "@/lib/api";
import { mqttClient, initializeMQTT } from "@/lib/mqtt";
export default function ConnectionStatus({ className = "", showDetails = false }) {
    const [backendStatus, setBackendStatus] = useState("checking");
    const [mqttStatus, setMqttStatus] = useState("disconnected");
    const [lastCheck, setLastCheck] = useState(new Date());
    const [isRetrying, setIsRetrying] = useState(false);
    const checkBackendConnection = async ()=>{
        try {
            setBackendStatus("checking");
            await apiClient.getMachines();
            setBackendStatus("connected");
        } catch (error) {
            // Silently handle connection errors in demo mode
            setBackendStatus("disconnected");
        }
        setLastCheck(new Date());
    };
    const checkMqttConnection = ()=>{
        setMqttStatus(mqttClient.isConnectedToBroker() ? "connected" : "disconnected");
    };
    const retryConnections = async ()=>{
        setIsRetrying(true);
        // Retry backend
        await checkBackendConnection();
        // Retry MQTT
        if (!mqttClient.isConnectedToBroker()) {
            try {
                await initializeMQTT();
            } catch (error) {
            // MQTT retry failed (silently handled)
            }
        }
        setIsRetrying(false);
    };
    useEffect(()=>{
        // Initial checks
        checkBackendConnection();
        checkMqttConnection();
        // Periodic checks
        const interval = setInterval(()=>{
            checkBackendConnection();
            checkMqttConnection();
        }, 30000); // Check every 30 seconds
        return ()=>clearInterval(interval);
    }, []);
    const getStatusIcon = (status)=>{
        switch(status){
            case "connected":
                return /*#__PURE__*/ React.createElement(CheckCircle, {
                    className: "w-4 h-4 text-green-500"
                });
            case "checking":
                return /*#__PURE__*/ React.createElement(Activity, {
                    className: "w-4 h-4 text-blue-500 animate-spin"
                });
            case "disconnected":
            default:
                return /*#__PURE__*/ React.createElement(AlertTriangle, {
                    className: "w-4 h-4 text-red-500"
                });
        }
    };
    const getStatusColor = (status)=>{
        switch(status){
            case "connected":
                return "bg-green-500";
            case "checking":
                return "bg-blue-500";
            case "disconnected":
                return "bg-red-500";
            default:
                return "bg-gray-500";
        }
    };
    if (!showDetails) {
        // Compact view
        return /*#__PURE__*/ React.createElement("div", {
            className: `flex items-center gap-2 ${className}`
        }, /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-1"
        }, /*#__PURE__*/ React.createElement(Server, {
            className: "w-4 h-4"
        }), /*#__PURE__*/ React.createElement("div", {
            className: `w-2 h-2 rounded-full ${getStatusColor(backendStatus)}`
        })), /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-1"
        }, mqttStatus === "connected" ? /*#__PURE__*/ React.createElement(Wifi, {
            className: "w-4 h-4"
        }) : /*#__PURE__*/ React.createElement(WifiOff, {
            className: "w-4 h-4"
        }), /*#__PURE__*/ React.createElement("div", {
            className: `w-2 h-2 rounded-full ${getStatusColor(mqttStatus)}`
        })), (backendStatus === "disconnected" || mqttStatus === "disconnected") && /*#__PURE__*/ React.createElement(Button, {
            variant: "ghost",
            size: "sm",
            onClick: retryConnections,
            disabled: isRetrying,
            className: "h-6 px-2"
        }, /*#__PURE__*/ React.createElement(RefreshCw, {
            className: `w-3 h-3 ${isRetrying ? "animate-spin" : ""}`
        })));
    }
    // Detailed view
    return /*#__PURE__*/ React.createElement(Card, {
        className: className
    }, /*#__PURE__*/ React.createElement(CardHeader, {
        className: "pb-3"
    }, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "text-base"
    }, "Connection Status"), /*#__PURE__*/ React.createElement(CardDescription, null, "Backend API and real-time communication status")), /*#__PURE__*/ React.createElement(CardContent, {
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-between p-3 bg-muted/30 rounded-lg"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-3"
    }, /*#__PURE__*/ React.createElement(Server, {
        className: "w-5 h-5"
    }), /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("p", {
        className: "font-medium"
    }, "Backend API"), /*#__PURE__*/ React.createElement("p", {
        className: "text-sm text-muted-foreground"
    }, "Spring Boot Server"))), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, getStatusIcon(backendStatus), /*#__PURE__*/ React.createElement(Badge, {
        variant: backendStatus === "connected" ? "default" : "destructive"
    }, backendStatus === "checking" ? "Checking..." : backendStatus))), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-between p-3 bg-muted/30 rounded-lg"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-3"
    }, mqttStatus === "connected" ? /*#__PURE__*/ React.createElement(Wifi, {
        className: "w-5 h-5"
    }) : /*#__PURE__*/ React.createElement(WifiOff, {
        className: "w-5 h-5"
    }), /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("p", {
        className: "font-medium"
    }, "Real-time Updates"), /*#__PURE__*/ React.createElement("p", {
        className: "text-sm text-muted-foreground"
    }, "MQTT Communication"))), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, getStatusIcon(mqttStatus), /*#__PURE__*/ React.createElement(Badge, {
        variant: mqttStatus === "connected" ? "default" : "secondary"
    }, mqttStatus))), (backendStatus === "disconnected" || mqttStatus === "disconnected") && /*#__PURE__*/ React.createElement(Alert, null, /*#__PURE__*/ React.createElement(AlertTriangle, {
        className: "h-4 w-4"
    }), /*#__PURE__*/ React.createElement(AlertDescription, null, "Some services are unavailable. The app will work in offline mode with limited functionality.", /*#__PURE__*/ React.createElement(Button, {
        variant: "outline",
        size: "sm",
        onClick: retryConnections,
        disabled: isRetrying,
        className: "ml-2"
    }, isRetrying ? /*#__PURE__*/ React.createElement(React.Fragment, null, /*#__PURE__*/ React.createElement(RefreshCw, {
        className: "w-3 h-3 mr-1 animate-spin"
    }), "Retrying...") : /*#__PURE__*/ React.createElement(React.Fragment, null, /*#__PURE__*/ React.createElement(RefreshCw, {
        className: "w-3 h-3 mr-1"
    }), "Retry")))), /*#__PURE__*/ React.createElement("div", {
        className: "text-xs text-muted-foreground text-center"
    }, "Last checked: ", lastCheck.toLocaleTimeString())));
}
// Status indicator for header/navbar
export function StatusIndicator({ className = "" }) {
    return /*#__PURE__*/ React.createElement(ConnectionStatus, {
        className: className,
        showDetails: false
    });
}
