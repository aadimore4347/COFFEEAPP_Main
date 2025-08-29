import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { CheckCircle, Wrench, Droplets, Filter, Settings, Clock, Zap } from "lucide-react";
import { motion } from "framer-motion";
const QuickActionFab = ()=>{
    const [isOpen, setIsOpen] = useState(false);
    const quickActions = [
        {
            id: "filter",
            title: "Replace Filter",
            description: "Mark filter as replaced",
            icon: /*#__PURE__*/ React.createElement(Filter, {
                className: "w-4 h-4"
            }),
            color: "bg-red-500 hover:bg-red-600",
            action: ()=>{
                // Handle filter replacement
                console.log("Filter replaced");
                setIsOpen(false);
            },
            urgent: true
        },
        {
            id: "refill",
            title: "Refill Supplies",
            description: "Mark supplies as refilled",
            icon: /*#__PURE__*/ React.createElement(Droplets, {
                className: "w-4 h-4"
            }),
            color: "bg-blue-500 hover:bg-blue-600",
            action: ()=>{
                // Handle supply refill
                console.log("Supplies refilled");
                setIsOpen(false);
            }
        },
        {
            id: "clean",
            title: "Complete Cleaning",
            description: "Mark cleaning as done",
            icon: /*#__PURE__*/ React.createElement(Settings, {
                className: "w-4 h-4"
            }),
            color: "bg-green-500 hover:bg-green-600",
            action: ()=>{
                // Handle cleaning completion
                console.log("Cleaning completed");
                setIsOpen(false);
            }
        },
        {
            id: "maintenance",
            title: "Maintenance Done",
            description: "Complete maintenance task",
            icon: /*#__PURE__*/ React.createElement(Wrench, {
                className: "w-4 h-4"
            }),
            color: "bg-orange-500 hover:bg-orange-600",
            action: ()=>{
                // Handle maintenance completion
                console.log("Maintenance completed");
                setIsOpen(false);
            }
        }
    ];
    const urgentActions = quickActions.filter((action)=>action.urgent);
    return /*#__PURE__*/ React.createElement("div", {
        className: "fixed bottom-20 right-4 z-40"
    }, /*#__PURE__*/ React.createElement(TooltipProvider, null, /*#__PURE__*/ React.createElement(Popover, {
        open: isOpen,
        onOpenChange: setIsOpen
    }, /*#__PURE__*/ React.createElement(Tooltip, null, /*#__PURE__*/ React.createElement(TooltipTrigger, {
        asChild: true
    }, /*#__PURE__*/ React.createElement(PopoverTrigger, {
        asChild: true
    }, /*#__PURE__*/ React.createElement("div", {
        className: "relative"
    }, /*#__PURE__*/ React.createElement(motion.div, {
        whileHover: {
            scale: 1.1
        },
        whileTap: {
            scale: 0.9
        }
    }, /*#__PURE__*/ React.createElement(Button, {
        size: "lg",
        className: "rounded-full w-14 h-14 bg-orange-500 hover:bg-orange-600 shadow-lg hover:shadow-xl transition-all duration-300"
    }, /*#__PURE__*/ React.createElement(Zap, {
        className: "w-6 h-6"
    }))), urgentActions.length > 0 && /*#__PURE__*/ React.createElement(Badge, {
        variant: "destructive",
        className: "absolute -top-2 -right-2 w-6 h-6 rounded-full p-0 flex items-center justify-center animate-pulse"
    }, urgentActions.length)))), /*#__PURE__*/ React.createElement(TooltipContent, {
        side: "left"
    }, /*#__PURE__*/ React.createElement("p", null, "Quick Actions"))), /*#__PURE__*/ React.createElement(PopoverContent, {
        side: "left",
        className: "w-80 p-0 mr-4",
        sideOffset: 8
    }, /*#__PURE__*/ React.createElement(Card, {
        className: "border-0 shadow-lg"
    }, /*#__PURE__*/ React.createElement(CardHeader, {
        className: "pb-3"
    }, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2 text-lg"
    }, /*#__PURE__*/ React.createElement(Zap, {
        className: "w-5 h-5 text-orange-500"
    }), "Quick Actions"), /*#__PURE__*/ React.createElement("p", {
        className: "text-sm text-muted-foreground"
    }, "Quickly resolve common machine issues")), /*#__PURE__*/ React.createElement(CardContent, {
        className: "space-y-2"
    }, quickActions.map((action, index)=>/*#__PURE__*/ React.createElement(motion.div, {
            key: action.id,
            initial: {
                opacity: 0,
                x: -20
            },
            animate: {
                opacity: 1,
                x: 0
            },
            transition: {
                duration: 0.2,
                delay: index * 0.05
            }
        }, /*#__PURE__*/ React.createElement(Button, {
            variant: "ghost",
            className: "w-full justify-start h-auto p-3 hover:bg-muted/50",
            onClick: action.action
        }, /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-3 w-full"
        }, /*#__PURE__*/ React.createElement("div", {
            className: `p-2 rounded-full ${action.color} text-white`
        }, action.icon), /*#__PURE__*/ React.createElement("div", {
            className: "flex-1 text-left"
        }, /*#__PURE__*/ React.createElement("div", {
            className: "flex items-center gap-2"
        }, /*#__PURE__*/ React.createElement("p", {
            className: "font-medium text-sm"
        }, action.title), action.urgent && /*#__PURE__*/ React.createElement(Badge, {
            variant: "destructive",
            className: "text-xs px-1.5 py-0"
        }, "URGENT")), /*#__PURE__*/ React.createElement("p", {
            className: "text-xs text-muted-foreground"
        }, action.description)), /*#__PURE__*/ React.createElement(CheckCircle, {
            className: "w-4 h-4 text-muted-foreground"
        }))))), /*#__PURE__*/ React.createElement("div", {
        className: "pt-2 border-t"
    }, /*#__PURE__*/ React.createElement(Button, {
        variant: "outline",
        size: "sm",
        className: "w-full",
        onClick: ()=>setIsOpen(false)
    }, /*#__PURE__*/ React.createElement(Clock, {
        className: "w-3 h-3 mr-2"
    }), "View All Alerts"))))))));
};
export default QuickActionFab;
