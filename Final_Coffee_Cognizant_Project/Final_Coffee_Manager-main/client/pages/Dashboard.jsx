import React, { useState } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import StepNavigation from "@/components/StepNavigation";
import { Coffee, MapPin, Building, Layers, Settings, LogOut, ChevronRight, Eye, Edit3, ArrowLeft } from "lucide-react";
export default function Dashboard() {
    const { user, logout } = useAuth();
    const [currentStep, setCurrentStep] = useState(0);
    const [selectedLocation, setSelectedLocation] = useState("");
    const [selectedOffice, setSelectedOffice] = useState("");
    const [selectedFloor, setSelectedFloor] = useState("");
    const [selectedMachine, setSelectedMachine] = useState("");
    const locations = [
        "New York",
        "Los Angeles",
        "Chicago",
        "Houston"
    ];
    const offices = [
        "Main Office",
        "North Branch",
        "South Branch",
        "East Wing"
    ];
    const floors = [
        "Ground Floor",
        "1st Floor",
        "2nd Floor",
        "3rd Floor"
    ];
    const machines = [
        "Machine A-001",
        "Machine A-002",
        "Machine B-001",
        "Machine C-001"
    ];
    const steps = [
        {
            id: "location",
            title: "Select Location",
            description: "Choose your city location",
            icon: /*#__PURE__*/ React.createElement(MapPin, {
                className: "w-5 h-5"
            }),
            completed: !!selectedLocation
        },
        {
            id: "office",
            title: "Select Office",
            description: "Choose your office building",
            icon: /*#__PURE__*/ React.createElement(Building, {
                className: "w-5 h-5"
            }),
            completed: !!selectedOffice
        },
        {
            id: "floor",
            title: "Select Floor",
            description: "Choose your floor level",
            icon: /*#__PURE__*/ React.createElement(Layers, {
                className: "w-5 h-5"
            }),
            completed: !!selectedFloor
        },
        {
            id: "machine",
            title: "Select Machine",
            description: "Choose the coffee machine",
            icon: /*#__PURE__*/ React.createElement(Coffee, {
                className: "w-5 h-5"
            }),
            completed: !!selectedMachine
        }
    ];
    const handleStepSelection = (stepIndex, value)=>{
        switch(stepIndex){
            case 0:
                setSelectedLocation(value);
                setSelectedOffice("");
                setSelectedFloor("");
                setSelectedMachine("");
                break;
            case 1:
                setSelectedOffice(value);
                setSelectedFloor("");
                setSelectedMachine("");
                break;
            case 2:
                setSelectedFloor(value);
                setSelectedMachine("");
                break;
            case 3:
                setSelectedMachine(value);
                break;
        }
        setCurrentStep(stepIndex + 1);
    };
    const getOptionsForStep = (stepIndex)=>{
        switch(stepIndex){
            case 0:
                return locations;
            case 1:
                return offices;
            case 2:
                return floors;
            case 3:
                return machines;
            default:
                return [];
        }
    };
    const getCurrentSelection = (stepIndex)=>{
        switch(stepIndex){
            case 0:
                return selectedLocation;
            case 1:
                return selectedOffice;
            case 2:
                return selectedFloor;
            case 3:
                return selectedMachine;
            default:
                return "";
        }
    };
    const allStepsCompleted = steps.every((step)=>step.completed);
    const handleStepBack = ()=>{
        if (currentStep > 0) {
            setCurrentStep(currentStep - 1);
            // Clear subsequent selections
            switch(currentStep - 1){
                case 0:
                    setSelectedOffice("");
                    setSelectedFloor("");
                    setSelectedMachine("");
                    break;
                case 1:
                    setSelectedFloor("");
                    setSelectedMachine("");
                    break;
                case 2:
                    setSelectedMachine("");
                    break;
            }
        }
    };
    const handleStepReset = ()=>{
        setCurrentStep(0);
        setSelectedLocation("");
        setSelectedOffice("");
        setSelectedFloor("");
        setSelectedMachine("");
    };
    const stepLabels = steps.map((step)=>step.title);
    return /*#__PURE__*/ React.createElement("div", {
        className: "min-h-screen bg-gradient-to-br from-coffee-light/30 to-background"
    }, /*#__PURE__*/ React.createElement("header", {
        className: "border-b bg-white/80 backdrop-blur-sm"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "container mx-auto px-4 h-16 flex items-center justify-between"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-3"
    }, /*#__PURE__*/ React.createElement(Button, {
        variant: "ghost",
        size: "sm",
        onClick: logout,
        className: "hover:scale-105 transition-all duration-200 hover:bg-primary/10 group"
    }, /*#__PURE__*/ React.createElement(ArrowLeft, {
        className: "w-4 h-4 mr-2 group-hover:-translate-x-1 transition-transform"
    }), /*#__PURE__*/ React.createElement("span", {
        className: "hidden sm:inline"
    }, "Sign Out"), /*#__PURE__*/ React.createElement("span", {
        className: "sm:hidden"
    }, "Out")), /*#__PURE__*/ React.createElement("div", {
        className: "w-px h-6 bg-border"
    }), /*#__PURE__*/ React.createElement("div", {
        className: "w-8 h-8 bg-primary rounded-lg flex items-center justify-center"
    }, /*#__PURE__*/ React.createElement(Coffee, {
        className: "w-5 h-5 text-primary-foreground"
    })), /*#__PURE__*/ React.createElement("h1", {
        className: "text-xl font-bold text-coffee-brown"
    }, "Coffee Manager")), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Badge, {
        variant: user?.role === "technician" ? "default" : "secondary",
        className: "gap-1"
    }, user?.role === "technician" ? /*#__PURE__*/ React.createElement(Edit3, {
        className: "w-3 h-3"
    }) : /*#__PURE__*/ React.createElement(Eye, {
        className: "w-3 h-3"
    }), user?.name)), /*#__PURE__*/ React.createElement(Button, {
        variant: "ghost",
        size: "sm",
        onClick: logout
    }, /*#__PURE__*/ React.createElement(LogOut, {
        className: "w-4 h-4"
    }))))), /*#__PURE__*/ React.createElement("main", {
        className: "container mx-auto px-4 py-8"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "max-w-4xl mx-auto space-y-8"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "text-center space-y-2"
    }, /*#__PURE__*/ React.createElement("h2", {
        className: "text-3xl font-bold text-coffee-brown"
    }, "Machine Selection"), /*#__PURE__*/ React.createElement("p", {
        className: "text-muted-foreground"
    }, "Follow the steps below to select and manage your coffee machine")), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-center"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center space-x-4"
    }, steps.map((step, index)=>/*#__PURE__*/ React.createElement(React.Fragment, {
            key: step.id
        }, /*#__PURE__*/ React.createElement("div", {
            className: "flex flex-col items-center space-y-2"
        }, /*#__PURE__*/ React.createElement("div", {
            className: `w-12 h-12 rounded-full flex items-center justify-center transition-colors ${step.completed ? "bg-primary text-primary-foreground" : index === currentStep ? "bg-primary/20 text-primary border-2 border-primary" : "bg-muted text-muted-foreground"}`
        }, step.icon), /*#__PURE__*/ React.createElement("div", {
            className: "text-center"
        }, /*#__PURE__*/ React.createElement("p", {
            className: "text-sm font-medium"
        }, step.title), /*#__PURE__*/ React.createElement("p", {
            className: "text-xs text-muted-foreground"
        }, step.description))), index < steps.length - 1 && /*#__PURE__*/ React.createElement(ChevronRight, {
            className: "w-5 h-5 text-muted-foreground"
        }))))), /*#__PURE__*/ React.createElement(StepNavigation, {
        currentStep: currentStep,
        totalSteps: steps.length,
        onBack: handleStepBack,
        onReset: handleStepReset,
        stepLabels: stepLabels,
        canGoBack: currentStep > 0,
        canGoNext: false
    }), currentStep < steps.length && /*#__PURE__*/ React.createElement(Card, null, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, steps[currentStep].icon, steps[currentStep].title), /*#__PURE__*/ React.createElement(CardDescription, null, steps[currentStep].description)), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 md:grid-cols-2 gap-3"
    }, getOptionsForStep(currentStep).map((option)=>/*#__PURE__*/ React.createElement(Button, {
            key: option,
            variant: "outline",
            className: "h-auto p-4 justify-start text-left",
            onClick: ()=>handleStepSelection(currentStep, option)
        }, /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("p", {
            className: "font-medium"
        }, option), /*#__PURE__*/ React.createElement("p", {
            className: "text-sm text-muted-foreground"
        }, "Click to select this", " ", steps[currentStep].title.toLowerCase()))))))), (selectedLocation || selectedOffice || selectedFloor || selectedMachine) && /*#__PURE__*/ React.createElement(Card, null, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, null, "Current Selection"), /*#__PURE__*/ React.createElement(CardDescription, null, "Your selected path through the system")), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, selectedLocation && /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(MapPin, {
        className: "w-4 h-4"
    }), " Location:", " ", selectedLocation), selectedOffice && /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Building, {
        className: "w-4 h-4"
    }), " Office: ", selectedOffice), selectedFloor && /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Layers, {
        className: "w-4 h-4"
    }), " Floor: ", selectedFloor), selectedMachine && /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Coffee, {
        className: "w-4 h-4"
    }), " Machine: ", selectedMachine)))), allStepsCompleted && /*#__PURE__*/ React.createElement(Card, {
        className: "border-primary"
    }, /*#__PURE__*/ React.createElement(CardContent, {
        className: "pt-6"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "text-center space-y-4"
    }, /*#__PURE__*/ React.createElement("h3", {
        className: "text-xl font-semibold text-coffee-brown"
    }, "Ready to Manage Machine"), /*#__PURE__*/ React.createElement("p", {
        className: "text-muted-foreground"
    }, "You can now", " ", user?.role === "technician" ? "edit and manage" : "view", " ", "the selected coffee machine data."), /*#__PURE__*/ React.createElement(Link, {
        to: "/machine"
    }, /*#__PURE__*/ React.createElement(Button, {
        size: "lg",
        className: "w-full sm:w-auto"
    }, /*#__PURE__*/ React.createElement(Settings, {
        className: "w-4 h-4 mr-2"
    }), user?.role === "technician" ? "Manage Machine" : "View Machine Data"))))))));
}
