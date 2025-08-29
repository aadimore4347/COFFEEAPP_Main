import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { ArrowLeft, Home, Coffee, Settings, HelpCircle, Menu, X } from "lucide-react";
import { Link, useLocation } from "react-router-dom";
export default function FloatingNavigation({ className = "" }) {
    const [isOpen, setIsOpen] = useState(false);
    const location = useLocation();
    const navigationItems = [
        {
            icon: /*#__PURE__*/ React.createElement(Home, {
                className: "w-4 h-4"
            }),
            label: "Dashboard",
            href: "/dashboard"
        },
        {
            icon: /*#__PURE__*/ React.createElement(Coffee, {
                className: "w-4 h-4"
            }),
            label: "Machine",
            href: "/machine"
        },
        {
            icon: /*#__PURE__*/ React.createElement(Settings, {
                className: "w-4 h-4"
            }),
            label: "Settings",
            href: "#"
        },
        {
            icon: /*#__PURE__*/ React.createElement(HelpCircle, {
                className: "w-4 h-4"
            }),
            label: "Help",
            href: "#"
        }
    ];
    const toggleMenu = ()=>setIsOpen(!isOpen);
    const goBack = ()=>{
        window.history.back();
    };
    return /*#__PURE__*/ React.createElement("div", {
        className: `fixed bottom-6 right-6 z-50 ${className}`
    }, isOpen && /*#__PURE__*/ React.createElement("div", {
        className: "absolute bottom-16 right-0 space-y-2 animate-fadeIn"
    }, /*#__PURE__*/ React.createElement(Button, {
        variant: "secondary",
        size: "sm",
        onClick: goBack,
        className: "flex items-center gap-2 shadow-lg hover:scale-105 transition-transform backdrop-blur-sm bg-background/80"
    }, /*#__PURE__*/ React.createElement(ArrowLeft, {
        className: "w-4 h-4"
    }), "Back"), navigationItems.map((item, index)=>/*#__PURE__*/ React.createElement(Link, {
            key: index,
            to: item.href
        }, /*#__PURE__*/ React.createElement(Button, {
            variant: location.pathname === item.href ? "default" : "secondary",
            size: "sm",
            className: "flex items-center gap-2 shadow-lg hover:scale-105 transition-transform backdrop-blur-sm bg-background/80",
            onClick: ()=>setIsOpen(false)
        }, item.icon, item.label)))), /*#__PURE__*/ React.createElement(Button, {
        size: "icon",
        onClick: toggleMenu,
        className: `
          w-14 h-14 rounded-full shadow-xl hover:scale-110 transition-all duration-200 
          ${isOpen ? "bg-destructive hover:bg-destructive/90" : "bg-primary hover:bg-primary/90"}
        `
    }, isOpen ? /*#__PURE__*/ React.createElement(X, {
        className: "w-6 h-6"
    }) : /*#__PURE__*/ React.createElement(Menu, {
        className: "w-6 h-6"
    })));
}
// Quick Back Button that appears on scroll
export function QuickBackFab({ showOnScroll = true, className = "" }) {
    const [isVisible, setIsVisible] = useState(!showOnScroll);
    React.useEffect(()=>{
        if (!showOnScroll) return;
        const handleScroll = ()=>{
            setIsVisible(window.scrollY > 200);
        };
        window.addEventListener("scroll", handleScroll);
        return ()=>window.removeEventListener("scroll", handleScroll);
    }, [
        showOnScroll
    ]);
    const goBack = ()=>{
        window.history.back();
    };
    if (!isVisible) return null;
    return /*#__PURE__*/ React.createElement(Button, {
        size: "icon",
        onClick: goBack,
        className: `
        fixed bottom-6 left-6 z-50 w-12 h-12 rounded-full shadow-xl 
        hover:scale-110 transition-all duration-200 bg-secondary hover:bg-secondary/90
        animate-fadeIn ${className}
      `
    }, /*#__PURE__*/ React.createElement(ArrowLeft, {
        className: "w-5 h-5"
    }));
}
// Navigation Helper Component for any page
export function PageNavigation({ title, backUrl, backLabel = "Back", actions, className = "" }) {
    return /*#__PURE__*/ React.createElement("div", {
        className: `flex items-center justify-between p-4 border-b bg-background/80 backdrop-blur-sm ${className}`
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-3"
    }, backUrl ? /*#__PURE__*/ React.createElement(Link, {
        to: backUrl
    }, /*#__PURE__*/ React.createElement(Button, {
        variant: "ghost",
        size: "sm",
        className: "hover:scale-105 transition-transform"
    }, /*#__PURE__*/ React.createElement(ArrowLeft, {
        className: "w-4 h-4 mr-2"
    }), backLabel)) : /*#__PURE__*/ React.createElement(Button, {
        variant: "ghost",
        size: "sm",
        onClick: ()=>window.history.back(),
        className: "hover:scale-105 transition-transform"
    }, /*#__PURE__*/ React.createElement(ArrowLeft, {
        className: "w-4 h-4 mr-2"
    }), backLabel), title && /*#__PURE__*/ React.createElement(React.Fragment, null, /*#__PURE__*/ React.createElement("div", {
        className: "w-px h-6 bg-border"
    }), /*#__PURE__*/ React.createElement("h1", {
        className: "text-lg font-semibold"
    }, title))), actions && /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, actions));
}
