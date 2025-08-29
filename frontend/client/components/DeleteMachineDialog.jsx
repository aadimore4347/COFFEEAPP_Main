import React from 'react';
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog";
import { Badge } from "@/components/ui/badge";
import { AlertTriangle, Coffee } from "lucide-react";
export default function DeleteMachineDialog({ isOpen, onClose, onConfirm, machine, isLoading = false }) {
    if (!machine) return null;
    return /*#__PURE__*/ React.createElement(AlertDialog, {
        open: isOpen,
        onOpenChange: onClose
    }, /*#__PURE__*/ React.createElement(AlertDialogContent, {
        className: "max-w-md"
    }, /*#__PURE__*/ React.createElement(AlertDialogHeader, null, /*#__PURE__*/ React.createElement(AlertDialogTitle, {
        className: "flex items-center gap-2 text-destructive"
    }, /*#__PURE__*/ React.createElement(AlertTriangle, {
        className: "h-5 w-5"
    }), "Delete Machine Permanently"), /*#__PURE__*/ React.createElement(AlertDialogDescription, {
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "text-sm text-muted-foreground"
    }, "Are you sure you want to permanently delete this coffee machine? This action cannot be undone."), /*#__PURE__*/ React.createElement("div", {
        className: "bg-gray-50 rounded-lg p-4 border border-gray-200"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-3 mb-3"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "p-2 bg-orange-100 rounded-full"
    }, /*#__PURE__*/ React.createElement(Coffee, {
        className: "h-4 w-4 text-orange-600"
    })), /*#__PURE__*/ React.createElement("div", {
        className: "flex-1"
    }, /*#__PURE__*/ React.createElement("h4", {
        className: "font-semibold text-gray-900"
    }, machine.name), /*#__PURE__*/ React.createElement("p", {
        className: "text-sm text-gray-600 font-mono"
    }, machine.id)), /*#__PURE__*/ React.createElement(Badge, {
        className: machine.status === "operational" ? "bg-green-100 text-green-700" : machine.status === "maintenance" ? "bg-orange-100 text-orange-700" : "bg-red-100 text-red-700"
    }, machine.status)), (machine.location || machine.office || machine.floor) && /*#__PURE__*/ React.createElement("div", {
        className: "text-sm text-gray-600 space-y-1"
    }, machine.location && /*#__PURE__*/ React.createElement("div", null, "Location: ", /*#__PURE__*/ React.createElement("span", {
        className: "font-medium"
    }, machine.location)), machine.office && /*#__PURE__*/ React.createElement("div", null, "Office: ", /*#__PURE__*/ React.createElement("span", {
        className: "font-medium"
    }, machine.office)), machine.floor && /*#__PURE__*/ React.createElement("div", null, "Floor: ", /*#__PURE__*/ React.createElement("span", {
        className: "font-medium"
    }, machine.floor)))), /*#__PURE__*/ React.createElement("div", {
        className: "bg-red-50 border border-red-200 rounded-lg p-3"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-start gap-2"
    }, /*#__PURE__*/ React.createElement(AlertTriangle, {
        className: "h-4 w-4 text-red-600 mt-0.5 flex-shrink-0"
    }), /*#__PURE__*/ React.createElement("div", {
        className: "text-sm text-red-800"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "font-medium mb-1"
    }, "This will permanently:"), /*#__PURE__*/ React.createElement("ul", {
        className: "list-disc list-inside space-y-1 text-xs"
    }, /*#__PURE__*/ React.createElement("li", null, "Remove all machine data and history"), /*#__PURE__*/ React.createElement("li", null, "Delete all usage and maintenance records"), /*#__PURE__*/ React.createElement("li", null, "Remove any pending alerts or notifications"), /*#__PURE__*/ React.createElement("li", null, "Make the machine unavailable to all users"))))))), /*#__PURE__*/ React.createElement(AlertDialogFooter, null, /*#__PURE__*/ React.createElement(AlertDialogCancel, {
        disabled: isLoading
    }, "Cancel"), /*#__PURE__*/ React.createElement(AlertDialogAction, {
        onClick: onConfirm,
        disabled: isLoading,
        className: "bg-destructive hover:bg-destructive/90 text-destructive-foreground"
    }, isLoading ? "Deleting..." : "Delete Permanently"))));
}
