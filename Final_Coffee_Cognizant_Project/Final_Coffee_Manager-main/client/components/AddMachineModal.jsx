import React, { useState, useEffect } from 'react';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Coffee, MapPin, Building, Zap } from 'lucide-react';
import { MACHINE_STATUS, FILTER_STATUS, CLEANING_STATUS, SUPPLY_TYPES, SUPPLY_NAMES, SUPPLY_DEFAULTS, VALIDATION } from '@/config';
import { ALL_STRINGS } from '@/config/ui-strings';
import { getAllOfficeNames } from '@/config/machines';
export default function AddMachineModal({ isOpen, onClose, onSubmit, selectedOffice, isLoading = false }) {
    const [formData, setFormData] = useState({
        name: '',
        location: '',
        office: selectedOffice || '',
        status: MACHINE_STATUS.OPERATIONAL,
        supplies: {
            [SUPPLY_TYPES.WATER]: SUPPLY_DEFAULTS.DEFAULT_LEVEL,
            [SUPPLY_TYPES.MILK]: SUPPLY_DEFAULTS.DEFAULT_LEVEL,
            [SUPPLY_TYPES.COFFEE_BEANS]: SUPPLY_DEFAULTS.DEFAULT_LEVEL,
            [SUPPLY_TYPES.SUGAR]: SUPPLY_DEFAULTS.DEFAULT_LEVEL
        },
        maintenance: {
            filterStatus: FILTER_STATUS.GOOD,
            cleaningStatus: CLEANING_STATUS.CLEAN,
            pressure: 15
        },
        notes: ''
    });
    const handleSubmit = async (e)=>{
        e.preventDefault();
        try {
            // Use selectedOffice if provided, otherwise use form data
            const finalFormData = {
                ...formData,
                office: selectedOffice || formData.office
            };
            await onSubmit(finalFormData);
        } catch (error) {
            console.error('Failed to add machine:', error);
        }
    };
    const handleInputChange = (field, value)=>{
        setFormData((prev)=>({
                ...prev,
                [field]: value
            }));
    };
    const handleSupplyChange = (supply, value)=>{
        setFormData((prev)=>({
                ...prev,
                supplies: {
                    ...prev.supplies,
                    [supply]: Math.max(0, Math.min(100, value))
                }
            }));
    };
    const handleMaintenanceChange = (field, value)=>{
        setFormData((prev)=>({
                ...prev,
                maintenance: {
                    ...prev.maintenance,
                    [field]: value
                }
            }));
    };
    const isFormValid = formData.name.trim() && formData.location.trim() && (formData.office.trim() || selectedOffice);
    // Reset form when modal opens
    useEffect(()=>{
        if (isOpen) {
            setFormData({
                name: '',
                location: '',
                office: selectedOffice || '',
                status: MACHINE_STATUS.OPERATIONAL,
                supplies: {
                    [SUPPLY_TYPES.WATER]: SUPPLY_DEFAULTS.DEFAULT_LEVEL,
                    [SUPPLY_TYPES.MILK]: SUPPLY_DEFAULTS.DEFAULT_LEVEL,
                    [SUPPLY_TYPES.COFFEE_BEANS]: SUPPLY_DEFAULTS.DEFAULT_LEVEL,
                    [SUPPLY_TYPES.SUGAR]: SUPPLY_DEFAULTS.DEFAULT_LEVEL
                },
                maintenance: {
                    filterStatus: FILTER_STATUS.GOOD,
                    cleaningStatus: CLEANING_STATUS.CLEAN,
                    pressure: 15
                },
                notes: ''
            });
        }
    }, [
        isOpen,
        selectedOffice
    ]);
    return /*#__PURE__*/ React.createElement(Dialog, {
        open: isOpen,
        onOpenChange: onClose
    }, /*#__PURE__*/ React.createElement(DialogContent, {
        className: "max-w-2xl max-h-[90vh] overflow-y-auto"
    }, /*#__PURE__*/ React.createElement(DialogHeader, null, /*#__PURE__*/ React.createElement(DialogTitle, {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Coffee, {
        className: "w-5 h-5 text-primary"
    }), ALL_STRINGS.MACHINE.ADD_MACHINE), /*#__PURE__*/ React.createElement(DialogDescription, null, selectedOffice ? `Configure a new coffee machine for ${selectedOffice}. Fill in all the required details.` : 'Configure a new coffee machine. Fill in all the required details.')), /*#__PURE__*/ React.createElement("form", {
        onSubmit: handleSubmit,
        className: "space-y-6"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("h3", {
        className: "text-lg font-semibold flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Building, {
        className: "w-4 h-4"
    }), "Basic Information"), /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 md:grid-cols-2 gap-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        htmlFor: "name"
    }, ALL_STRINGS.MACHINE.MACHINE_NAME, " *"), /*#__PURE__*/ React.createElement(Input, {
        id: "name",
        value: formData.name,
        onChange: (e)=>handleInputChange('name', e.target.value),
        placeholder: ALL_STRINGS.MACHINE.MACHINE_NAME_PLACEHOLDER,
        required: true,
        maxLength: VALIDATION.MACHINE_NAME_MAX_LENGTH
    })), !selectedOffice && /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        htmlFor: "office"
    }, ALL_STRINGS.MACHINE.OFFICE, " *"), /*#__PURE__*/ React.createElement(Select, {
        value: formData.office,
        onValueChange: (value)=>handleInputChange('office', value)
    }, /*#__PURE__*/ React.createElement(SelectTrigger, null, /*#__PURE__*/ React.createElement(SelectValue, {
        placeholder: ALL_STRINGS.MACHINE.OFFICE_PLACEHOLDER
    })), /*#__PURE__*/ React.createElement(SelectContent, null, getAllOfficeNames().map((officeName)=>/*#__PURE__*/ React.createElement(SelectItem, {
            key: officeName,
            value: officeName
        }, officeName))))), selectedOffice && /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        htmlFor: "office"
    }, ALL_STRINGS.MACHINE.OFFICE), /*#__PURE__*/ React.createElement(Input, {
        id: "office",
        value: selectedOffice,
        disabled: true,
        className: "bg-muted text-muted-foreground"
    }), /*#__PURE__*/ React.createElement("p", {
        className: "text-xs text-muted-foreground"
    }, "Machine will be added to ", selectedOffice))), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        htmlFor: "location"
    }, ALL_STRINGS.MACHINE.LOCATION, " *"), /*#__PURE__*/ React.createElement(Input, {
        id: "location",
        value: formData.location,
        onChange: (e)=>handleInputChange('location', e.target.value),
        placeholder: ALL_STRINGS.MACHINE.LOCATION_PLACEHOLDER,
        required: true
    })), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        htmlFor: "status"
    }, "Initial Status"), /*#__PURE__*/ React.createElement(Select, {
        value: formData.status,
        onValueChange: (value)=>handleInputChange('status', value)
    }, /*#__PURE__*/ React.createElement(SelectTrigger, null, /*#__PURE__*/ React.createElement(SelectValue, null)), /*#__PURE__*/ React.createElement(SelectContent, null, /*#__PURE__*/ React.createElement(SelectItem, {
        value: MACHINE_STATUS.OPERATIONAL
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "w-2 h-2 bg-green-500 rounded-full"
    }), ALL_STRINGS.MACHINE.STATUS_OPERATIONAL)), /*#__PURE__*/ React.createElement(SelectItem, {
        value: MACHINE_STATUS.MAINTENANCE
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "w-2 h-2 bg-orange-500 rounded-full"
    }), ALL_STRINGS.MACHINE.STATUS_MAINTENANCE)), /*#__PURE__*/ React.createElement(SelectItem, {
        value: MACHINE_STATUS.OFFLINE
    }, /*#__PURE__*/ React.createElement("div", {
        className: "flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "w-2 h-2 bg-red-500 rounded-full"
    }), ALL_STRINGS.MACHINE.STATUS_OFFLINE)))))), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("h3", {
        className: "text-lg font-semibold flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(Zap, {
        className: "w-4 h-4"
    }), ALL_STRINGS.MACHINE.SUPPLY_LEVELS, " (", ALL_STRINGS.UI.PERCENT, ")"), /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-2 md:grid-cols-4 gap-4"
    }, Object.entries(formData.supplies).map(([supply, value])=>/*#__PURE__*/ React.createElement("div", {
            key: supply,
            className: "space-y-2"
        }, /*#__PURE__*/ React.createElement(Label, {
            htmlFor: supply
        }, SUPPLY_NAMES[supply] || supply), /*#__PURE__*/ React.createElement(Input, {
            id: supply,
            type: "number",
            min: "0",
            max: "100",
            value: value,
            onChange: (e)=>handleSupplyChange(supply, parseInt(e.target.value) || 0)
        }))))), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("h3", {
        className: "text-lg font-semibold flex items-center gap-2"
    }, /*#__PURE__*/ React.createElement(MapPin, {
        className: "w-4 h-4"
    }), ALL_STRINGS.MACHINE.MAINTENANCE, " Configuration"), /*#__PURE__*/ React.createElement("div", {
        className: "grid grid-cols-1 md:grid-cols-2 gap-4"
    }, /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        htmlFor: "filterStatus"
    }, ALL_STRINGS.MACHINE.FILTER_STATUS), /*#__PURE__*/ React.createElement(Select, {
        value: formData.maintenance.filterStatus,
        onValueChange: (value)=>handleMaintenanceChange('filterStatus', value)
    }, /*#__PURE__*/ React.createElement(SelectTrigger, null, /*#__PURE__*/ React.createElement(SelectValue, null)), /*#__PURE__*/ React.createElement(SelectContent, null, /*#__PURE__*/ React.createElement(SelectItem, {
        value: FILTER_STATUS.GOOD
    }, ALL_STRINGS.MACHINE.FILTER_GOOD), /*#__PURE__*/ React.createElement(SelectItem, {
        value: FILTER_STATUS.NEEDS_REPLACEMENT
    }, ALL_STRINGS.MACHINE.FILTER_NEEDS_REPLACEMENT), /*#__PURE__*/ React.createElement(SelectItem, {
        value: FILTER_STATUS.CRITICAL
    }, ALL_STRINGS.MACHINE.FILTER_CRITICAL)))), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        htmlFor: "cleaningStatus"
    }, ALL_STRINGS.MACHINE.CLEANING_STATUS), /*#__PURE__*/ React.createElement(Select, {
        value: formData.maintenance.cleaningStatus,
        onValueChange: (value)=>handleMaintenanceChange('cleaningStatus', value)
    }, /*#__PURE__*/ React.createElement(SelectTrigger, null, /*#__PURE__*/ React.createElement(SelectValue, null)), /*#__PURE__*/ React.createElement(SelectContent, null, /*#__PURE__*/ React.createElement(SelectItem, {
        value: CLEANING_STATUS.CLEAN
    }, ALL_STRINGS.MACHINE.CLEANING_CLEAN), /*#__PURE__*/ React.createElement(SelectItem, {
        value: CLEANING_STATUS.NEEDS_CLEANING
    }, ALL_STRINGS.MACHINE.CLEANING_NEEDS_CLEANING), /*#__PURE__*/ React.createElement(SelectItem, {
        value: CLEANING_STATUS.OVERDUE
    }, ALL_STRINGS.MACHINE.CLEANING_OVERDUE)))), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-2"
    }, /*#__PURE__*/ React.createElement(Label, {
        htmlFor: "pressure"
    }, ALL_STRINGS.MACHINE.PRESSURE, " (bar)"), /*#__PURE__*/ React.createElement(Input, {
        id: "pressure",
        type: "number",
        min: SUPPLY_DEFAULTS.MIN_PRESSURE,
        max: SUPPLY_DEFAULTS.MAX_PRESSURE,
        value: formData.maintenance.pressure,
        onChange: (e)=>handleMaintenanceChange('pressure', parseInt(e.target.value) || 15)
    })))), /*#__PURE__*/ React.createElement("div", {
        className: "space-y-4"
    }, /*#__PURE__*/ React.createElement("h3", {
        className: "text-lg font-semibold"
    }, "Installation ", ALL_STRINGS.MACHINE.NOTES), /*#__PURE__*/ React.createElement(Textarea, {
        value: formData.notes,
        onChange: (e)=>handleInputChange('notes', e.target.value),
        placeholder: ALL_STRINGS.MACHINE.NOTES_PLACEHOLDER,
        className: "min-h-[80px]",
        maxLength: VALIDATION.NOTES_MAX_LENGTH
    }), /*#__PURE__*/ React.createElement("div", {
        className: "text-xs text-gray-500 text-right"
    }, formData.notes.length, "/", VALIDATION.NOTES_MAX_LENGTH)), /*#__PURE__*/ React.createElement(DialogFooter, {
        className: "gap-3"
    }, /*#__PURE__*/ React.createElement(Button, {
        type: "button",
        variant: "outline",
        onClick: onClose
    }, ALL_STRINGS.FORM.CANCEL), /*#__PURE__*/ React.createElement(Button, {
        type: "submit",
        disabled: !isFormValid || isLoading,
        className: "bg-gradient-to-r from-primary to-primary/80"
    }, isLoading ? ALL_STRINGS.FORM.SAVING : ALL_STRINGS.MACHINE.ADD_MACHINE)))));
}
