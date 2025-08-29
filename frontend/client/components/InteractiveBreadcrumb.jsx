import React from "react";
import { Link, useLocation } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Home, ArrowLeft, ChevronRight, Settings, LayoutDashboard } from "lucide-react";
export default function InteractiveBreadcrumb({ items, showBackButton = true, backUrl, onBack, className = "" }) {
    const location = useLocation();
    // Auto-generate breadcrumbs based on current path if not provided
    const defaultItems = React.useMemo(()=>{
        const pathItems = [
            {
                label: "Dashboard",
                href: "/dashboard",
                icon: /*#__PURE__*/ React.createElement(LayoutDashboard, {
                    className: "w-4 h-4"
                })
            }
        ];
        if (location.pathname === "/machine") {
            pathItems.push({
                label: "Machine Management",
                current: true,
                icon: /*#__PURE__*/ React.createElement(Settings, {
                    className: "w-4 h-4"
                })
            });
        }
        return pathItems;
    }, [
        location.pathname
    ]);
    const breadcrumbItems = items || defaultItems;
    const defaultBackUrl = breadcrumbItems.length > 1 ? breadcrumbItems[breadcrumbItems.length - 2]?.href : "/dashboard";
    const handleBack = ()=>{
        if (onBack) {
            onBack();
        } else if (backUrl || defaultBackUrl) {
            window.history.back();
        }
    };
    return /*#__PURE__*/ React.createElement("div", {
        className: `flex items-center gap-4 ${className}`
    }, showBackButton && /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, backUrl || defaultBackUrl ? /*#__PURE__*/ React.createElement(Link, {
        to: backUrl || defaultBackUrl || "/dashboard"
    }, /*#__PURE__*/ React.createElement(Button, {
        variant: "ghost",
        size: "sm",
        className: "hover:scale-105 transition-all duration-200 hover:bg-primary/10"
    }, /*#__PURE__*/ React.createElement(ArrowLeft, {
        className: "w-4 h-4 mr-2"
    }), "Back")) : /*#__PURE__*/ React.createElement(Button, {
        variant: "ghost",
        size: "sm",
        onClick: handleBack,
        className: "hover:scale-105 transition-all duration-200 hover:bg-primary/10"
    }, /*#__PURE__*/ React.createElement(ArrowLeft, {
        className: "w-4 h-4 mr-2"
    }), "Back"), /*#__PURE__*/ React.createElement("div", {
        className: "w-px h-6 bg-border"
    })), /*#__PURE__*/ React.createElement("nav", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Link, {
        to: "/",
        className: "hover:scale-105 transition-transform"
    }, /*#__PURE__*/ React.createElement(Button, {
        variant: "ghost",
        size: "sm",
        className: "gap-2"
    }, /*#__PURE__*/ React.createElement(Home, {
        className: "w-4 h-4"
    }), /*#__PURE__*/ React.createElement("span", {
        className: "hidden sm:inline"
    }, "Home"))), breadcrumbItems.map((item, index)=>/*#__PURE__*/ React.createElement(React.Fragment, {
            key: index
        }, /*#__PURE__*/ React.createElement(ChevronRight, {
            className: "w-4 h-4 text-muted-foreground"
        }), item.current ? /*#__PURE__*/ React.createElement(Badge, {
            variant: "secondary",
            className: "gap-2"
        }, item.icon, item.label) : item.href ? /*#__PURE__*/ React.createElement(Link, {
            to: item.href
        }, /*#__PURE__*/ React.createElement(Button, {
            variant: "ghost",
            size: "sm",
            className: "gap-2 hover:scale-105 transition-transform"
        }, item.icon, item.label)) : /*#__PURE__*/ React.createElement("span", {
            className: "flex items-center gap-2 text-sm text-muted-foreground"
        }, item.icon, item.label)))));
}
// Quick Back Button Component for simple use cases
export function QuickBackButton({ to, label = "Back", className = "", onClick }) {
    return /*#__PURE__*/ React.createElement("div", {
        className: className
    }, to ? /*#__PURE__*/ React.createElement(Link, {
        to: to
    }, /*#__PURE__*/ React.createElement(Button, {
        variant: "ghost",
        size: "sm",
        className: "hover:scale-105 transition-all duration-200 hover:bg-primary/10 group"
    }, /*#__PURE__*/ React.createElement(ArrowLeft, {
        className: "w-4 h-4 mr-2 group-hover:-translate-x-1 transition-transform"
    }), label)) : /*#__PURE__*/ React.createElement(Button, {
        variant: "ghost",
        size: "sm",
        onClick: onClick || (()=>window.history.back()),
        className: "hover:scale-105 transition-all duration-200 hover:bg-primary/10 group"
    }, /*#__PURE__*/ React.createElement(ArrowLeft, {
        className: "w-4 h-4 mr-2 group-hover:-translate-x-1 transition-transform"
    }), label));
}
// Navigation Helper Hook
export function useNavigationHistory() {
    const [canGoBack, setCanGoBack] = React.useState(false);
    React.useEffect(()=>{
        setCanGoBack(window.history.length > 1);
    }, []);
    const goBack = ()=>{
        if (canGoBack) {
            window.history.back();
        }
    };
    const goForward = ()=>{
        window.history.forward();
    };
    return {
        canGoBack,
        goBack,
        goForward
    };
}
