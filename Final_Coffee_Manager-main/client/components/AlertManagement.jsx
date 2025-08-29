import React, { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Textarea } from "@/components/ui/textarea";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { AlertTriangle, CheckCircle, Clock, Droplets, Wrench, Calendar, FileText, Save, X, Edit3, History, Settings } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";
const AlertManagement = ()=>{
    const { user } = useAuth();
    const [selectedAlert, setSelectedAlert] = useState(null);
    const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
    const [editForm, setEditForm] = useState({
        status: "",
        resolution: "",
        estimatedTime: ""
    });
    // Sample alerts - in real app, this would come from API
    const [alerts, setAlerts] = useState([
        {
            id: "1",
            type: "critical",
            category: "maintenance",
            title: "Filter Replacement Required",
            description: "Water filter has reached end of life and needs immediate replacement",
            priority: "high",
            status: "active",
            createdAt: "2024-01-15T10:30:00Z",
            machineComponent: "Water Filter"
        },
        {
            id: "2",
            type: "warning",
            category: "cleaning",
            title: "Deep Cleaning Overdue",
            description: "Machine hasn't been deep cleaned for 48 hours",
            priority: "medium",
            status: "in_progress",
            createdAt: "2024-01-14T14:20:00Z",
            machineComponent: "Brewing Unit"
        },
        {
            id: "3",
            type: "warning",
            category: "supply",
            title: "Low Coffee Beans",
            description: "Coffee bean level is below 20%",
            priority: "medium",
            status: "resolved",
            createdAt: "2024-01-13T09:15:00Z",
            resolvedAt: "2024-01-13T11:30:00Z",
            resolvedBy: "John Technician",
            resolution: "Refilled coffee bean hopper to 100%",
            machineComponent: "Bean Hopper"
        },
        {
            id: "4",
            type: "info",
            category: "system",
            title: "Scheduled Maintenance Due",
            description: "Monthly preventive maintenance is scheduled for tomorrow",
            priority: "low",
            status: "active",
            createdAt: "2024-01-12T08:00:00Z",
            estimatedTime: "2 hours",
            machineComponent: "Overall System"
        }
    ]);
    const getAlertIcon = (category)=>{
        switch(category){
            case "maintenance":
                return /*#__PURE__*/ React.createElement(Wrench, {
                    className: "w-4 h-4"
                });
            case "supply":
                return /*#__PURE__*/ React.createElement(Droplets, {
                    className: "w-4 h-4"
                });
            case "cleaning":
                return /*#__PURE__*/ React.createElement(Settings, {
                    className: "w-4 h-4"
                });
            case "system":
                return /*#__PURE__*/ React.createElement(AlertTriangle, {
                    className: "w-4 h-4"
                });
            default:
                return /*#__PURE__*/ React.createElement(AlertTriangle, {
                    className: "w-4 h-4"
                });
        }
    };
    const getAlertColor = (type, status)=>{
        if (status === "resolved") return "bg-green-50 border-green-200";
        switch(type){
            case "critical":
                return "bg-red-50 border-red-200";
            case "warning":
                return "bg-orange-50 border-orange-200";
            case "info":
                return "bg-blue-50 border-blue-200";
            default:
                return "bg-gray-50 border-gray-200";
        }
    };
    const getStatusColor = (status)=>{
        switch(status){
            case "active":
                return "destructive";
            case "in_progress":
                return "secondary";
            case "resolved":
                return "default";
            default:
                return "outline";
        }
    };
    const getPriorityColor = (priority)=>{
        switch(priority){
            case "high":
                return "text-red-600 bg-red-100";
            case "medium":
                return "text-orange-600 bg-orange-100";
            case "low":
                return "text-blue-600 bg-blue-100";
            default:
                return "text-gray-600 bg-gray-100";
        }
    };
    const formatDate = (dateString)=>{
        return new Date(dateString).toLocaleString();
    };
    const handleEditAlert = (alert)=>{
        setSelectedAlert(alert);
        setEditForm({
            status: alert.status,
            resolution: alert.resolution || "",
            estimatedTime: alert.estimatedTime || ""
        });
        setIsEditDialogOpen(true);
    };
    const handleSaveAlert = ()=>{
        if (!selectedAlert) return;
        const updatedAlerts = alerts.map((alert)=>{
            if (alert.id === selectedAlert.id) {
                return {
                    ...alert,
                    status: editForm.status,
                    resolution: editForm.resolution,
                    estimatedTime: editForm.estimatedTime,
                    resolvedAt: editForm.status === "resolved" ? new Date().toISOString() : undefined,
                    resolvedBy: editForm.status === "resolved" ? user?.name : undefined
                };
            }
            return alert;
        });
        setAlerts(updatedAlerts);
        setIsEditDialogOpen(false);
        setSelectedAlert(null);
    };
    const activeAlerts = alerts.filter((alert)=>alert.status === "active");
    const inProgressAlerts = alerts.filter((alert)=>alert.status === "in_progress");
    const resolvedAlerts = alerts.filter((alert)=>alert.status === "resolved");
    const renderAlertCard = (alert)=>/*#__PURE__*/ React.createElement(Card, {
            key: alert.id,
            className: `transition-all duration-300 hover:shadow-lg ${getAlertColor(alert.type, alert.status)}`
        }, /*#__PURE__*/ React.createElement(CardHeader, {
            className: "pb-3"
        }, /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center justify-between"
        }, /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-2"
        }, getAlertIcon(alert.category), /*#__PURE__*/ React.createElement(CardTitle, {
            className: "text-sm font-medium"
        }, alert.title)), /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-2"
        }, /*#__PURE__*/ React.createElement(Badge, {
            variant: "outline",
            className: getPriorityColor(alert.priority)
        }, alert.priority.toUpperCase()), /*#__PURE__*/ React.createElement(Badge, {
            variant: getStatusColor(alert.status)
        }, alert.status.replace("_", " ").toUpperCase())))), /*#__PURE__*/ React.createElement(CardContent, {
            className: "space-y-4"
        }, /*#__PURE__*/ React.createElement("p", {
            className: "text-sm text-muted-foreground"
        }, alert.description), /*#__PURE__*/ React.createElement("div", {
            className: "grid grid-cols-2 gap-4 text-xs"
        }, /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-1"
        }, /*#__PURE__*/ React.createElement(Calendar, {
            className: "w-3 h-3"
        }), /*#__PURE__*/ React.createElement("span", null, formatDate(alert.createdAt))), alert.machineComponent && /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-1"
        }, /*#__PURE__*/ React.createElement(Settings, {
            className: "w-3 h-3"
        }), /*#__PURE__*/ React.createElement("span", null, alert.machineComponent))), alert.status === "resolved" && /*#__PURE__*/ React.createElement("div", {
            className: "p-3 bg-green-50 rounded-lg border border-green-200"
        }, /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-2 mb-2"
        }, /*#__PURE__*/ React.createElement(CheckCircle, {
            className: "w-4 h-4 text-green-600"
        }), /*#__PURE__*/ React.createElement("span", {
            className: "font-medium text-green-800"
        }, "Resolved")), /*#__PURE__*/ React.createElement("p", {
            className: "text-sm text-green-700"
        }, alert.resolution), /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-4 mt-2 text-xs text-green-600"
        }, /*#__PURE__*/ React.createElement("span", null, "By: ", alert.resolvedBy), /*#__PURE__*/ React.createElement("span", null, "On: ", alert.resolvedAt && formatDate(alert.resolvedAt)))), user?.role === "technician" && alert.status !== "resolved" && /*#__PURE__*/ React.createElement(Button, {
            onClick: ()=>handleEditAlert(alert),
            variant: "outline",
            size: "sm",
            className: "w-full"
        }, /*#__PURE__*/ React.createElement(Edit3, {
            className: "w-3 h-3 mr-2"
        }), "Update Alert")));
    return /*#__PURE__*/ React.createElement("div", {
        className: "space-y-6"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 md:grid-cols-4 gap-4"
    }, /*#__PURE__*/ React.createElement(Card, {
        className: "bg-red-50 border-red-200"
    }, /*#__PURE__*/ React.createElement(CardContent, {
        className: "p-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-between"
    }, /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("p", {
        className: "text-sm font-medium text-red-800"
    }, "Active Alerts"), /*#__PURE__*/ React.createElement("p", {
        className: "text-2xl font-bold text-red-600"
    }, activeAlerts.length)), /*#__PURE__*/ React.createElement(AlertTriangle, {
        className: "w-8 h-8 text-red-500"
    })))), /*#__PURE__*/ React.createElement(Card, {
        className: "bg-orange-50 border-orange-200"
    }, /*#__PURE__*/ React.createElement(CardContent, {
        className: "p-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-between"
    }, /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("p", {
        className: "text-sm font-medium text-orange-800"
    }, "In Progress"), /*#__PURE__*/ React.createElement("p", {
        className: "text-2xl font-bold text-orange-600"
    }, inProgressAlerts.length)), /*#__PURE__*/ React.createElement(Clock, {
        className: "w-8 h-8 text-orange-500"
    })))), /*#__PURE__*/ React.createElement(Card, {
        className: "bg-green-50 border-green-200"
    }, /*#__PURE__*/ React.createElement(CardContent, {
        className: "p-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-between"
    }, /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("p", {
        className: "text-sm font-medium text-green-800"
    }, "Resolved"), /*#__PURE__*/ React.createElement("p", {
        className: "text-2xl font-bold text-green-600"
    }, resolvedAlerts.length)), /*#__PURE__*/ React.createElement(CheckCircle, {
        className: "w-8 h-8 text-green-500"
    })))), /*#__PURE__*/ React.createElement(Card, {
        className: "bg-blue-50 border-blue-200"
    }, /*#__PURE__*/ React.createElement(CardContent, {
        className: "p-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-between"
    }, /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("p", {
        className: "text-sm font-medium text-blue-800"
    }, "Total Alerts"), /*#__PURE__*/ React.createElement("p", {
        className: "text-2xl font-bold text-blue-600"
    }, alerts.length)), /*#__PURE__*/ React.createElement(FileText, {
        className: "w-8 h-8 text-blue-500"
    }))))), /*#__PURE__*/ React.createElement(Tabs, {
        defaultValue: "active",
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement(TabsList, {
        className: "grid w-full grid-cols-3"
    }, /*#__PURE__*/ React.createElement(TabsTrigger, {
        value: "active",
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(AlertTriangle, {
        className: "w-4 h-4"
    }), "Active (", activeAlerts.length, ")"), /*#__PURE__*/ React.createElement(TabsTrigger, {
        value: "progress",
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Clock, {
        className: "w-4 h-4"
    }), "In Progress (", inProgressAlerts.length, ")"), /*#__PURE__*/ React.createElement(TabsTrigger, {
        value: "resolved",
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(CheckCircle, {
        className: "w-4 h-4"
    }), "Resolved (", resolvedAlerts.length, ")")), /*#__PURE__*/ React.createElement(TabsContent, {
        value: "active",
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "grid gap-4"
    }, activeAlerts.map(renderAlertCard), activeAlerts.length === 0 && /*#__PURE__*/ React.createElement(Card, {
        className: "p-8 text-center"
    }, /*#__PURE__*/ React.createElement(CheckCircle, {
        className: "w-12 h-12 text-green-500 mx-auto mb-4"
    }), /*#__PURE__*/ React.createElement("h3", {
        className: "text-lg font-medium"
    }, "No Active Alerts"), /*#__PURE__*/ React.createElement("p", {
        className: "text-muted-foreground"
    }, "All systems are running smoothly!")))), /*#__PURE__*/ React.createElement(TabsContent, {
        value: "progress",
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "grid gap-4"
    }, inProgressAlerts.map(renderAlertCard), inProgressAlerts.length === 0 && /*#__PURE__*/ React.createElement(Card, {
        className: "p-8 text-center"
    }, /*#__PURE__*/ React.createElement(Clock, {
        className: "w-12 h-12 text-gray-400 mx-auto mb-4"
    }), /*#__PURE__*/ React.createElement("h3", {
        className: "text-lg font-medium"
    }, "No Alerts in Progress"), /*#__PURE__*/ React.createElement("p", {
        className: "text-muted-foreground"
    }, "Start working on active alerts to see them here.")))), /*#__PURE__*/ React.createElement(TabsContent, {
        value: "resolved",
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "grid gap-4"
    }, resolvedAlerts.map(renderAlertCard), resolvedAlerts.length === 0 && /*#__PURE__*/ React.createElement(Card, {
        className: "p-8 text-center"
    }, /*#__PURE__*/ React.createElement(History, {
        className: "w-12 h-12 text-gray-400 mx-auto mb-4"
    }), /*#__PURE__*/ React.createElement("h3", {
        className: "text-lg font-medium"
    }, "No Resolved Alerts"), /*#__PURE__*/ React.createElement("p", {
        className: "text-muted-foreground"
    }, "Resolved alerts will appear here."))))), /*#__PURE__*/ React.createElement(Dialog, {
        open: isEditDialogOpen,
        onOpenChange: setIsEditDialogOpen
    }, /*#__PURE__*/ React.createElement(DialogContent, {
        className: "max-w-md"
    }, /*#__PURE__*/ React.createElement(DialogHeader, null, /*#__PURE__*/ React.createElement(DialogTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Edit3, {
        className: "w-5 h-5"
    }), "Update Alert"), /*#__PURE__*/ React.createElement(DialogDescription, null, "Update the status and add resolution details for this alert.")), selectedAlert && /*#__PURE__*/ React.createElement("div", {
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "p-3 bg-muted rounded-lg"
    }, /*#__PURE__*/ React.createElement("h4", {
        className: "font-medium"
    }, selectedAlert.title), /*#__PURE__*/ React.createElement("p", {
        className: "text-sm text-muted-foreground"
    }, selectedAlert.description)), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, null, "Status"), /*#__PURE__*/ React.createElement(Select, {
        value: editForm.status,
        onValueChange: (value)=>setEditForm({
                ...editForm,
                status: value
            })
    }, /*#__PURE__*/ React.createElement(SelectTrigger, null, /*#__PURE__*/ React.createElement(SelectValue, null)), /*#__PURE__*/ React.createElement(SelectContent, null, /*#__PURE__*/ React.createElement(SelectItem, {
        value: "active"
    }, "Active"), /*#__PURE__*/ React.createElement(SelectItem, {
        value: "in_progress"
    }, "In Progress"), /*#__PURE__*/ React.createElement(SelectItem, {
        value: "resolved"
    }, "Resolved")))), editForm.status !== "active" && /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, null, "Resolution Notes"), /*#__PURE__*/ React.createElement(Textarea, {
        value: editForm.resolution,
        onChange: (e)=>setEditForm({
                ...editForm,
                resolution: e.target.value
            }),
        placeholder: "Describe what action was taken or is being taken...",
        rows: 3
    })), editForm.status === "in_progress" && /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, null, "Estimated Completion Time"), /*#__PURE__*/ React.createElement(Input, {
        value: editForm.estimatedTime,
        onChange: (e)=>setEditForm({
                ...editForm,
                estimatedTime: e.target.value
            }),
        placeholder: "e.g., 2 hours, 30 minutes"
    }))), /*#__PURE__*/ React.createElement(DialogFooter, null, /*#__PURE__*/ React.createElement(Button, {
        variant: "outline",
        onClick: ()=>setIsEditDialogOpen(false)
    }, /*#__PURE__*/ React.createElement(X, {
        className: "w-4 h-4 mr-2"
    }), "Cancel"), /*#__PURE__*/ React.createElement(Button, {
        onClick: handleSaveAlert
    }, /*#__PURE__*/ React.createElement(Save, {
        className: "w-4 h-4 mr-2"
    }), "Save Changes")))));
};
export default AlertManagement;
