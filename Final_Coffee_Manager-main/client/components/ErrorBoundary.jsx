import React from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { AlertTriangle, RefreshCw } from "lucide-react";
export class ErrorBoundary extends React.Component {
    static getDerivedStateFromError(error) {
        return {
            hasError: true,
            error
        };
    }
    componentDidCatch(error, errorInfo) {
        console.error("Error boundary caught an error:", error, errorInfo);
    }
    render() {
        if (this.state.hasError) {
            if (this.props.fallback) {
                return this.props.fallback;
            }
            return /*#__PURE__*/ React.createElement("div", {
                className: "min-h-screen flex items-center justify-center p-4"
            }, /*#__PURE__*/ React.createElement(Card, {
                className: "max-w-md w-full"
            }, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement(CardTitle, {
                className: "flex items-center gap-2 text-destructive"
            }, /*#__PURE__*/ React.createElement(AlertTriangle, {
                className: "w-5 h-5"
            }), "Something went wrong"), /*#__PURE__*/ React.createElement(CardDescription, null, "An error occurred while loading the application")), /*#__PURE__*/ React.createElement(CardContent, {
                className: "space-y-4"
            }, this.state.error && /*#__PURE__*/ React.createElement("div", {
                className: "text-sm text-muted-foreground bg-muted/30 p-3 rounded"
            }, /*#__PURE__*/ React.createElement("strong", null, "Error:"), " ", this.state.error.message), /*#__PURE__*/ React.createElement(Button, {
                onClick: ()=>window.location.reload(),
                className: "w-full"
            }, /*#__PURE__*/ React.createElement(RefreshCw, {
                className: "w-4 h-4 mr-2"
            }), "Reload Page"))));
        }
        return this.props.children;
    }
    constructor(props){
        super(props);
        this.state = {
            hasError: false
        };
    }
}
export default ErrorBoundary;
