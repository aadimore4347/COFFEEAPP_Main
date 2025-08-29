import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Slider } from "@/components/ui/slider";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Plus, CheckCircle, AlertTriangle } from "lucide-react";
export default function SupplyRefillModal({ isOpen, onClose, supply, onRefill, canEdit }) {
    const [refillAmount, setRefillAmount] = useState([
        0
    ]);
    const [isRefilling, setIsRefilling] = useState(false);
    const handleRefill = async ()=>{
        setIsRefilling(true);
        try {
            await onRefill(refillAmount[0]);
            setRefillAmount([
                0
            ]);
            onClose();
        } catch (error) {
            console.error("Failed to refill supply:", error);
        // Could show error toast here
        } finally{
            setIsRefilling(false);
        }
    };
    const newLevel = Math.min(100, supply.current + refillAmount[0]);
    const totalCost = refillAmount[0] / 100 * supply.cost;
    const getStatusColor = (level)=>{
        if (level > 70) return "text-green-600";
        if (level > 30) return "text-orange-500";
        return "text-red-500";
    };
    const getStatusIcon = (level)=>{
        if (level > 70) return /*#__PURE__*/ React.createElement(CheckCircle, {
            className: "w-4 h-4 text-green-600"
        });
        return /*#__PURE__*/ React.createElement(AlertTriangle, {
            className: "w-4 h-4 text-orange-500"
        });
    };
    return /*#__PURE__*/ React.createElement(Dialog, {
        open: isOpen,
        onOpenChange: onClose
    }, /*#__PURE__*/ React.createElement(DialogContent, {
        className: "sm:max-w-md"
    }, /*#__PURE__*/ React.createElement(DialogHeader, null, /*#__PURE__*/ React.createElement(DialogTitle, {
        className: "flex items-center gap-2"
    }, supply.icon, "Refill ", supply.name), /*#__PURE__*/ React.createElement(DialogDescription, null, "Adjust the supply levels for this ingredient")), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-6"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-between p-4 bg-muted/30 rounded-lg"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-1"
    }, /*#__PURE__*/ React.createElement("p", {
        className: "text-sm font-medium"
    }, "Current Level"), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, getStatusIcon(supply.current), /*#__PURE__*/ React.createElement("span", {
        className: `text-lg font-bold ${getStatusColor(supply.current)}`
    }, supply.current, "%"))), /*#__PURE__*/ React.createElement(Progress, {
        value: supply.current,
        className: "w-20 h-2"
    })), canEdit && /*#__PURE__*/ React.createElement(React.Fragment, null, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-between"
    }, /*#__PURE__*/ React.createElement("label", {
        className: "text-sm font-medium"
    }, "Refill Amount"), /*#__PURE__*/ React.createElement(Badge, {
        variant: "outline"
    }, refillAmount[0], "%")), /*#__PURE__*/ React.createElement(Slider, {
        value: refillAmount,
        onValueChange: setRefillAmount,
        max: 100 - supply.current,
        step: 5,
        className: "w-full"
    }), /*#__PURE__*/ React.createElement("div", {
        className: "flex justify-between text-xs text-muted-foreground"
    }, /*#__PURE__*/ React.createElement("span", null, "0%"), /*#__PURE__*/ React.createElement("span", null, 100 - supply.current, "% (Full)"))), refillAmount[0] > 0 && /*#__PURE__*/ React.createElement("div", {
        className: "p-4 bg-primary/5 border border-primary/20 rounded-lg space-y-3"
    }, /*#__PURE__*/ React.createElement("h4", {
        className: "text-sm font-medium text-primary"
    }, "Refill Preview"), /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-2 gap-4 text-sm"
    }, /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("p", {
        className: "text-muted-foreground"
    }, "After Refill"), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, getStatusIcon(newLevel), /*#__PURE__*/ React.createElement("span", {
        className: `font-bold ${getStatusColor(newLevel)}`
    }, newLevel, "%"))), /*#__PURE__*/ React.createElement("div", null, /*#__PURE__*/ React.createElement("p", {
        className: "text-muted-foreground"
    }, "Cost"), /*#__PURE__*/ React.createElement("p", {
        className: "font-bold text-primary"
    }, "$", totalCost.toFixed(2))))), /*#__PURE__*/ React.createElement("div", {
        className: "flex gap-2 pt-4"
    }, /*#__PURE__*/ React.createElement(Button, {
        variant: "outline",
        onClick: onClose,
        className: "flex-1",
        disabled: isRefilling
    }, "Cancel"), /*#__PURE__*/ React.createElement(Button, {
        onClick: handleRefill,
        disabled: refillAmount[0] === 0 || isRefilling,
        className: "flex-1"
    }, isRefilling ? /*#__PURE__*/ React.createElement(React.Fragment, null, /*#__PURE__*/ React.createElement("div", {
        className: "w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin mr-2"
    }), "Refilling...") : /*#__PURE__*/ React.createElement(React.Fragment, null, /*#__PURE__*/ React.createElement(Plus, {
        className: "w-4 h-4 mr-2"
    }), "Refill (", refillAmount[0], "%)")))))));
}
