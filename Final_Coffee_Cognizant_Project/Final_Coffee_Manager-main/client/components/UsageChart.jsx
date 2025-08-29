import React from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Coffee } from "lucide-react";
export default function UsageChart({ className }) {
    return /*#__PURE__*/ React.createElement(Card, {
        className: className
    }, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Coffee, {
        className: "w-5 h-5"
    }), "Total Cups Served")), /*#__PURE__*/ React.createElement(CardContent, null, /*#__PURE__*/ React.createElement("div", {
        className: "text-center space-y-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "text-4xl font-bold text-primary"
    }, "245"), /*#__PURE__*/ React.createElement("p", {
        className: "text-muted-foreground"
    }, "Total cups served today"))));
}
