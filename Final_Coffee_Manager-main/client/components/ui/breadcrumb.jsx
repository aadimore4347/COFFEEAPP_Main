import * as React from "react";
import { Slot } from "@radix-ui/react-slot";
import { ChevronRight, MoreHorizontal } from "lucide-react";
import { cn } from "@/lib/utils";
const Breadcrumb = /*#__PURE__*/ React.forwardRef(({ ...props }, ref)=>/*#__PURE__*/ React.createElement("nav", {
        ref: ref,
        "aria-label": "breadcrumb",
        ...props
    }));
Breadcrumb.displayName = "Breadcrumb";
const BreadcrumbList = /*#__PURE__*/ React.forwardRef(({ className, ...props }, ref)=>/*#__PURE__*/ React.createElement("ol", {
        ref: ref,
        className: cn("flex flex-wrap items-center gap-1.5 break-words text-sm text-muted-foreground sm:gap-2.5", className),
        ...props
    }));
BreadcrumbList.displayName = "BreadcrumbList";
const BreadcrumbItem = /*#__PURE__*/ React.forwardRef(({ className, ...props }, ref)=>/*#__PURE__*/ React.createElement("li", {
        ref: ref,
        className: cn("inline-flex items-center gap-1.5", className),
        ...props
    }));
BreadcrumbItem.displayName = "BreadcrumbItem";
const BreadcrumbLink = /*#__PURE__*/ React.forwardRef(({ asChild, className, ...props }, ref)=>{
    const Comp = asChild ? Slot : "a";
    return /*#__PURE__*/ React.createElement(Comp, {
        ref: ref,
        className: cn("transition-colors hover:text-foreground", className),
        ...props
    });
});
BreadcrumbLink.displayName = "BreadcrumbLink";
const BreadcrumbPage = /*#__PURE__*/ React.forwardRef(({ className, ...props }, ref)=>/*#__PURE__*/ React.createElement("span", {
        ref: ref,
        role: "link",
        "aria-disabled": "true",
        "aria-current": "page",
        className: cn("font-normal text-foreground", className),
        ...props
    }));
BreadcrumbPage.displayName = "BreadcrumbPage";
const BreadcrumbSeparator = ({ children, className, ...props })=>/*#__PURE__*/ React.createElement("li", {
        role: "presentation",
        "aria-hidden": "true",
        className: cn("[&>svg]:size-3.5", className),
        ...props
    }, children ?? /*#__PURE__*/ React.createElement(ChevronRight, null));
BreadcrumbSeparator.displayName = "BreadcrumbSeparator";
const BreadcrumbEllipsis = ({ className, ...props })=>/*#__PURE__*/ React.createElement("span", {
        role: "presentation",
        "aria-hidden": "true",
        className: cn("flex h-9 w-9 items-center justify-center", className),
        ...props
    }, /*#__PURE__*/ React.createElement(MoreHorizontal, {
        className: "h-4 w-4"
    }), /*#__PURE__*/ React.createElement("span", {
        className: "sr-only"
    }, "More"));
BreadcrumbEllipsis.displayName = "BreadcrumbElipssis";
export { Breadcrumb, BreadcrumbList, BreadcrumbItem, BreadcrumbLink, BreadcrumbPage, BreadcrumbSeparator, BreadcrumbEllipsis };
