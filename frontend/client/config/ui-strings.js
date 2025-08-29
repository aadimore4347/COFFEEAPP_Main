// UI Strings and Messages Configuration (JavaScript)
// Converted from TypeScript to JS

// ========================================
// AUTHENTICATION STRINGS
// ========================================
export const AUTH_STRINGS = {
  LOGIN_WELCOME: "Welcome Back",
  LOGIN_SUBTITLE: "Sign in to your coffee management account",
  USERNAME_LABEL: "Username",
  USERNAME_PLACEHOLDER: "Enter your username",
  PASSWORD_LABEL: "Password",
  PASSWORD_PLACEHOLDER: "Enter your password",
  SIGN_IN_BUTTON: "Sign In",
  CREATE_ACCOUNT_LINK: "Create Account",
  FORGOT_PASSWORD_LINK: "Forgot Password?",

  SIGNUP_WELCOME: "Create Account",
  SIGNUP_SUBTITLE: "Join the coffee management system",
  NAME_LABEL: "Full Name",
  NAME_PLACEHOLDER: "Enter your full name",
  CONFIRM_PASSWORD_LABEL: "Confirm Password",
  CONFIRM_PASSWORD_PLACEHOLDER: "Confirm your password",
  ROLE_LABEL: "Role",
  ROLE_PLACEHOLDER: "Select your role",
  CITY_LABEL: "City",
  CITY_PLACEHOLDER: "Select your city",
  OFFICE_LABEL: "Office",
  OFFICE_PLACEHOLDER: "Select your office",
  CREATE_ACCOUNT_BUTTON: "Create Account",
  ALREADY_HAVE_ACCOUNT: "Already have an account?",
  SIGN_IN_LINK: "Sign In",

  ROLE_TECHNICIAN: "Technician",
  ROLE_ADMIN: "Administrator",

  LOGIN_SUCCESS: "Successfully logged in",
  LOGOUT_SUCCESS: "Successfully logged out",
  SIGNUP_SUCCESS: "Account created successfully",
  INVALID_CREDENTIALS: "Invalid username or password",
  ACCOUNT_EXISTS: "Account already exists",
  FIELDS_REQUIRED: "Please fill in all required fields",
};

// ========================================
// NAVIGATION STRINGS
// ========================================
export const NAV_STRINGS = {
  HOME: "Home",
  DASHBOARD: "Dashboard",
  MACHINES: "Machines",
  ALERTS: "Alerts",
  REPORTS: "Reports",
  SETTINGS: "Settings",
  PROFILE: "Profile",
  LOGOUT: "Logout",
  BACK: "Back",
  MENU: "Menu",
  CLOSE: "Close",
};

// ========================================
// MACHINE MANAGEMENT STRINGS
// ========================================
export const MACHINE_STRINGS = {
  MACHINE: "Machine",
  MACHINES: "Machines",
  MACHINE_DETAILS: "Machine Details",
  MACHINE_STATUS: "Machine Status",
  MACHINE_OVERVIEW: "Machine Overview",

  STATUS_OPERATIONAL: "Operational",
  STATUS_MAINTENANCE: "Maintenance",
  STATUS_OFFLINE: "Offline",

  POWER_ONLINE: "Online",
  POWER_OFFLINE: "Offline",

  ELECTRICITY_AVAILABLE: "Available",
  ELECTRICITY_UNAVAILABLE: "Unavailable",

  SUPPLIES: "Supplies",
  SUPPLY_LEVELS: "Supply Levels",
  REFILL_SUPPLIES: "Refill Supplies",
  RECENT_REFILLS: "Recent Refills",
  WATER_TANK: "Water Tank",
  MILK_CONTAINER: "Milk Container",
  COFFEE_BEANS: "Coffee Beans",
  SUGAR_CONTAINER: "Sugar Container",

  EDIT_MACHINE: "Edit Machine",
  SAVE_CHANGES: "Save Changes",
  CANCEL_CHANGES: "Cancel",
  ADD_MACHINE: "Add Machine",
  DELETE_MACHINE: "Delete Machine",
  REFRESH_DATA: "Refresh Data",

  MACHINE_NAME: "Machine Name",
  MACHINE_NAME_PLACEHOLDER: "e.g., Coffee Station Alpha",
  LOCATION: "Location",
  LOCATION_PLACEHOLDER: "e.g., Building A2, Ground Floor",
  OFFICE: "Office",
  OFFICE_PLACEHOLDER: "e.g., Hinjewadi IT Park",
  FLOOR: "Floor",
  FLOOR_PLACEHOLDER: "e.g., Floor 3",
  NOTES: "Notes",
  NOTES_PLACEHOLDER: "Add any notes or observations...",

  MAINTENANCE: "Maintenance",
  LAST_MAINTENANCE: "Last Maintenance",
  NEXT_MAINTENANCE: "Next Maintenance",
  FILTER_STATUS: "Filter Status",
  CLEANING_STATUS: "Cleaning Status",
  PRESSURE: "Pressure",

  FILTER_GOOD: "Good",
  FILTER_NEEDS_REPLACEMENT: "Needs Replacement",
  FILTER_CRITICAL: "Critical",

  CLEANING_CLEAN: "Clean",
  CLEANING_NEEDS_CLEANING: "Needs Cleaning",
  CLEANING_OVERDUE: "Overdue",

  USAGE: "Usage",
  DAILY_CUPS: "Daily Cups",
  WEEKLY_CUPS: "Weekly Cups",
  MONTHLY_CUPS: "Monthly Cups",
};

// ========================================
// ALERT STRINGS
// ========================================
export const ALERT_STRINGS = {
  ALERTS: "Alerts",
  ALERT: "Alert",
  NO_ALERTS: "No active alerts",
  VIEW_ALL_ALERTS: "View All Alerts",
  ACTIVE_ALERTS: "Active Alerts",
  RESOLVED_ALERTS: "Resolved Alerts",

  MARK_RESOLVED: "Mark as Resolved",
  DISMISS_ALERT: "Dismiss",
  RESOLVE_ALL: "Resolve All",

  LOW_SUPPLY_ALERT: "Low Supply Alert",
  MAINTENANCE_ALERT: "Maintenance Required",
  POWER_OUTAGE_ALERT: "Power Outage",
  FILTER_REPLACEMENT_ALERT: "Filter Replacement Required",
  CLEANING_ALERT: "Cleaning Required",

  LOW_WATER: "Water level is running low",
  LOW_MILK: "Milk level is running low",
  LOW_COFFEE: "Coffee beans level is running low",
  LOW_SUGAR: "Sugar level is running low",
  FILTER_REPLACEMENT_REQUIRED: "Water filter needs replacement",
  CLEANING_REQUIRED: "Machine requires cleaning",
  POWER_OUTAGE: "Machine has lost power",
  MAINTENANCE_DUE: "Scheduled maintenance is due",

  PRIORITY_LOW: "Low",
  PRIORITY_MEDIUM: "Medium",
  PRIORITY_HIGH: "High",
  PRIORITY_CRITICAL: "Critical",
};

// ========================================
// FORM STRINGS
// ========================================
export const FORM_STRINGS = {
  SAVE: "Save",
  CANCEL: "Cancel",
  SUBMIT: "Submit",
  RESET: "Reset",
  EDIT: "Edit",
  DELETE: "Delete",
  ADD: "Add",
  UPDATE: "Update",
  CONFIRM: "Confirm",

  REQUIRED_FIELD: "This field is required",
  INVALID_EMAIL: "Please enter a valid email address",
  PASSWORD_TOO_SHORT: "Password must be at least 6 characters",
  PASSWORDS_DONT_MATCH: "Passwords do not match",
  INVALID_NUMBER: "Please enter a valid number",
  VALUE_TOO_LOW: "Value is too low",
  VALUE_TOO_HIGH: "Value is too high",

  LOADING: "Loading...",
  SAVING: "Saving...",
  SAVED: "Saved",
  ERROR: "Error",
  SUCCESS: "Success",
};

// ========================================
// DASHBOARD STRINGS
// ========================================
export const DASHBOARD_STRINGS = {
  WELCOME: "Welcome to Coffee Manager",
  DASHBOARD_OVERVIEW: "Dashboard Overview",
  MACHINE_SUMMARY: "Machine Summary",
  ALERT_SUMMARY: "Alert Summary",
  RECENT_ACTIVITY: "Recent Activity",
  QUICK_ACTIONS: "Quick Actions",

  TOTAL_MACHINES: "Total Machines",
  OPERATIONAL_MACHINES: "Operational",
  MAINTENANCE_MACHINES: "In Maintenance",
  OFFLINE_MACHINES: "Offline",

  TOTAL_ALERTS: "Total Alerts",
  CRITICAL_ALERTS: "Critical",
  WARNING_ALERTS: "Warnings",

  TODAY: "Today",
  THIS_WEEK: "This Week",
  THIS_MONTH: "This Month",
  LAST_24_HOURS: "Last 24 Hours",
};

// ========================================
// GENERAL UI STRINGS
// ========================================
export const UI_STRINGS = {
  OK: "OK",
  YES: "Yes",
  NO: "No",
  CONTINUE: "Continue",
  CANCEL: "Cancel",
  CLOSE: "Close",
  SEARCH: "Search",
  FILTER: "Filter",
  SORT: "Sort",
  REFRESH: "Refresh",

  LOADING: "Loading...",
  ERROR: "Error",
  SUCCESS: "Success",
  WARNING: "Warning",
  INFO: "Information",
  EMPTY: "No data available",

  NOW: "Now",
  JUST_NOW: "Just now",
  MINUTES_AGO: (n) => `${n} minute${n !== 1 ? 's' : ''} ago`,
  HOURS_AGO: (n) => `${n} hour${n !== 1 ? 's' : ''} ago`,
  DAYS_AGO: (n) => `${n} day${n !== 1 ? 's' : ''} ago`,

  PERCENT: "%",
  PSI: "PSI",
  CUPS: "cups",
  LITERS: "L",
  MILLILITERS: "mL",

  UPLOAD: "Upload",
  DOWNLOAD: "Download",
  EXPORT: "Export",
  IMPORT: "Import",
};

// ========================================
// ERROR STRINGS
// ========================================
export const ERROR_STRINGS = {
  NETWORK_ERROR: "Network connection failed",
  SERVER_ERROR: "Server error occurred",
  TIMEOUT_ERROR: "Request timed out",

  AUTH_REQUIRED: "Authentication required",
  ACCESS_DENIED: "Access denied",
  SESSION_EXPIRED: "Session has expired",

  INVALID_INPUT: "Invalid input provided",
  MISSING_REQUIRED_FIELDS: "Please fill in all required fields",
  INVALID_FORMAT: "Invalid format",

  MACHINE_NOT_FOUND: "Machine not found",
  MACHINE_OFFLINE: "Machine is currently offline",
  COMMUNICATION_ERROR: "Communication error with machine",

  SAVE_FAILED: "Failed to save data",
  LOAD_FAILED: "Failed to load data",
  DELETE_FAILED: "Failed to delete item",
  UPDATE_FAILED: "Failed to update data",

  UNKNOWN_ERROR: "An unknown error occurred",
  TRY_AGAIN: "Please try again",
  CONTACT_SUPPORT: "Please contact support if the problem persists",
};

// ========================================
// SUCCESS STRINGS
// ========================================
export const SUCCESS_STRINGS = {
  SAVE_SUCCESS: "Data saved successfully",
  UPDATE_SUCCESS: "Updated successfully",
  DELETE_SUCCESS: "Deleted successfully",
  UPLOAD_SUCCESS: "Upload completed successfully",
  SYNC_SUCCESS: "Data synchronized successfully",
  OPERATION_SUCCESS: "Operation completed successfully",
};

// ========================================
// CONFIRMATION STRINGS
// ========================================
export const CONFIRMATION_STRINGS = {
  DELETE_MACHINE: "Are you sure you want to delete this machine?",
  DELETE_ALERT: "Are you sure you want to delete this alert?",
  RESET_DATA: "Are you sure you want to reset all data?",
  LOGOUT_CONFIRM: "Are you sure you want to log out?",
  UNSAVED_CHANGES: "You have unsaved changes. Are you sure you want to leave?",
  DISCARD_CHANGES: "Discard changes?",
};

// ========================================
// EXPORT ALL STRINGS
// ========================================
export const ALL_STRINGS = {
  AUTH: AUTH_STRINGS,
  NAV: NAV_STRINGS,
  MACHINE: MACHINE_STRINGS,
  ALERT: ALERT_STRINGS,
  FORM: FORM_STRINGS,
  DASHBOARD: DASHBOARD_STRINGS,
  UI: UI_STRINGS,
  ERROR: ERROR_STRINGS,
  SUCCESS: SUCCESS_STRINGS,
  CONFIRMATION: CONFIRMATION_STRINGS,
};

export default ALL_STRINGS;
