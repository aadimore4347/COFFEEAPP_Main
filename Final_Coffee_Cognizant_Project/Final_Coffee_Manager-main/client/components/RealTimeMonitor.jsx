import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Activity, Zap, Gauge, Wifi, WifiOff, Coffee, Users, TrendingUp, Clock } from 'lucide-react';
export default function RealTimeMonitor({ onActivityUpdate }) {
    const [data, setData] = useState({
        isOnline: true,
        currentOrder: null,
        queueLength: 0,
        pressure: 15,
        powerUsage: 75,
        cupsToday: 127,
        lastActivity: 'Just now'
    });
    const [isLive, setIsLive] = useState(false);
    // Simulate real-time updates
    useEffect(()=>{
        if (!isLive) return;
        const interval = setInterval(()=>{
            setData((prev)=>{
                const orders = [
                    'Espresso',
                    'Cappuccino',
                    'Americano',
                    'Latte',
                    'Mocha'
                ];
                const newData = {
                    ...prev,
                    currentOrder: Math.random() > 0.7 ? orders[Math.floor(Math.random() * orders.length)] : null,
                    queueLength: Math.floor(Math.random() * 5),
                    pressure: 14 + Math.random() * 3,
                    powerUsage: 70 + Math.random() * 20,
                    cupsToday: prev.cupsToday + (Math.random() > 0.8 ? 1 : 0),
                    lastActivity: Math.random() > 0.5 ? 'Just now' : `${Math.floor(Math.random() * 5) + 1}m ago`
                };
                onActivityUpdate?.(newData);
                return newData;
            });
        }, 2000);
        return ()=>clearInterval(interval);
    }, [
        isLive,
        onActivityUpdate
    ]);
    const getPressureColor = (pressure)=>{
        if (pressure >= 14 && pressure <= 16) return 'text-green-600';
        return 'text-orange-500';
    };
    return /*#__PURE__*/ React.createElement(Card, {
        className: "relative overflow-hidden"
    }, /*#__PURE__*/ React.createElement(CardHeader, null, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center justify-between"
    }, /*#__PURE__*/ React.createElement(CardTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Activity, {
        className: `w-5 h-5 ${isLive ? 'text-green-500 animate-pulse' : 'text-muted-foreground'}`
    }), "Real-Time Monitor"), /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Badge, {
        variant: data.isOnline ? 'default' : 'destructive',
        className: "gap-1"
    }, data.isOnline ? /*#__PURE__*/ React.createElement(Wifi, {
        className: "w-3 h-3"
    }) : /*#__PURE__*/ React.createElement(WifiOff, {
        className: "w-3 h-3"
    }), data.isOnline ? 'Online' : 'Offline'), /*#__PURE__*/ React.createElement(Button, {
        variant: isLive ? 'destructive' : 'default',
        size: "sm",
        onClick: ()=>setIsLive(!isLive)
    }, isLive ? 'Stop' : 'Start', " Live")))), /*#__PURE__*/ React.createElement(CardContent, {
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 md:grid-cols-2 gap-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Coffee, {
        className: "w-4 h-4"
    }), /*#__PURE__*/ React.createElement("span", {
        className: "text-sm font-medium"
    }, "Current Order")), /*#__PURE__*/ React.createElement("div", {
        className: "text-lg font-bold"
    }, data.currentOrder ? /*#__PURE__*/ React.createElement("span", {
        className: "text-primary animate-pulse"
    }, data.currentOrder) : /*#__PURE__*/ React.createElement("span", {
        className: "text-muted-foreground"
    }, "Idle"))), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Users, {
        className: "w-4 h-4"
    }), /*#__PURE__*/ React.createElement("span", {
        className: "text-sm font-medium"
    }, "Queue")), /*#__PURE__*/ React.createElement("div", {
        className: "text-lg font-bold"
    }, data.queueLength > 0 ? /*#__PURE__*/ React.createElement("span", {
        className: "text-orange-500"
    }, data.queueLength, " orders") : /*#__PURE__*/ React.createElement("span", {
        className: "text-green-600"
    }, "Empty")))), /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-2 gap-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Gauge, {
        className: "w-4 h-4"
    }), /*#__PURE__*/ React.createElement("span", {
        className: "text-sm font-medium"
    }, "Pressure")), /*#__PURE__*/ React.createElement("div", {
        className: `text-lg font-bold ${getPressureColor(data.pressure)}`
    }, data.pressure.toFixed(1), " bar")), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Zap, {
        className: "w-4 h-4"
    }), /*#__PURE__*/ React.createElement("span", {
        className: "text-sm font-medium"
    }, "Power Usage")), /*#__PURE__*/ React.createElement("div", {
        className: "text-lg font-bold text-primary"
    }, data.powerUsage.toFixed(0), "%")), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(TrendingUp, {
        className: "w-4 h-4"
    }), /*#__PURE__*/ React.createElement("span", {
        className: "text-sm font-medium"
    }, "Cups Today")), /*#__PURE__*/ React.createElement("div", {
        className: "text-lg font-bold text-primary"
    }, data.cupsToday))), /*#__PURE__*/ React.createElement("div", {
        className: "pt-4 border-t"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2 text-sm text-muted-foreground"
    }, /*#__PURE__*/ React.createElement(Clock, {
        className: "w-4 h-4"
    }), /*#__PURE__*/ React.createElement("span", null, "Last activity: ", data.lastActivity))), isLive && /*#__PURE__*/ React.createElement("div", {
        className: "absolute top-0 right-0 w-2 h-full bg-gradient-to-b from-green-500 to-green-600 opacity-20 animate-pulse"
    })));
}
