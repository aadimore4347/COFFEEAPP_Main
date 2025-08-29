import React from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Coffee, Gauge, Zap } from "lucide-react";
export default function MachineAnalytics({ machineId, machineName, className }) {
    return /*#__PURE__*/ React.createElement("div", {
        className: `space-y-6 ${className}`
    }, /*#__PURE__*/ React.createElement(Card, null, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Gauge, {
        className: "w-5 h-5"
    }), "System Health")), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 md:grid-cols-2 gap-6"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "text-center p-6 bg-gradient-to-br from-green-50 to-green-100 rounded-xl"
    }, /*#__PURE__*/ React.createElement(Gauge, {
        className: "w-8 h-8 text-green-600 mx-auto mb-2"
    }), /*#__PURE__*/ React.createElement("div", {
        className: "text-2xl font-bold text-green-600"
    }, "15.2 bar"), /*#__PURE__*/ React.createElement("div", {
        className: "text-sm text-muted-foreground"
    }, "Pressure")), /*#__PURE__*/ React.createElement("div", {
        className: "text-center p-6 bg-gradient-to-br from-yellow-50 to-yellow-100 rounded-xl"
    }, /*#__PURE__*/ React.createElement(Zap, {
        className: "w-8 h-8 text-yellow-600 mx-auto mb-2"
    }), /*#__PURE__*/ React.createElement("div", {
        className: "text-2xl font-bold text-yellow-600"
    }, "85%"), /*#__PURE__*/ React.createElement("div", {
        className: "text-sm text-muted-foreground"
    }, "Power"))))), /*#__PURE__*/ React.createElement(Card, null, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Coffee, {
        className: "w-5 h-5"
    }), "Usage Summary")), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 md:grid-cols-3 gap-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "text-center p-4 bg-primary/10 rounded-lg"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "text-2xl font-bold text-primary"
    }, "147"), /*#__PURE__*/ React.createElement("div", {
        className: "text-sm text-muted-foreground"
    }, "Cups Today")), /*#__PURE__*/ React.createElement("div", {
        className: "text-center p-4 bg-blue-50 rounded-lg"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "text-2xl font-bold text-blue-600"
    }, "1,023"), /*#__PURE__*/ React.createElement("div", {
        className: "text-sm text-muted-foreground"
    }, "Cups This Week")), /*#__PURE__*/ React.createElement("div", {
        className: "text-center p-4 bg-green-50 rounded-lg"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "text-2xl font-bold text-green-600"
    }, "4,356"), /*#__PURE__*/ React.createElement("div", {
        className: "text-sm text-muted-foreground"
    }, "Cups This Month"))))));
}
