// Central Configuration System (JavaScript)
// Converted from TypeScript to JS: removed type annotations and `as const`

// ========================================
// ENVIRONMENT CONFIGURATION
// ========================================
export const ENV_CONFIG = {
  API_BASE_URL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api",
  STANDALONE_MODE: import.meta.env.VITE_STANDALONE_MODE !== "false",
  DEBUG: import.meta.env.VITE_DEBUG === "true" || import.meta.env.DEV,
  REQUEST_TIMEOUT: parseInt(import.meta.env.VITE_REQUEST_TIMEOUT || "10000"),
  TOKEN_EXPIRY_HOURS: parseInt(import.meta.env.VITE_TOKEN_EXPIRY_HOURS || "24"),
};

// ========================================
// USER ROLES AND AUTHENTICATION
// ========================================
export const USER_ROLES = {
  TECHNICIAN: "technician",
  ADMIN: "admin",
};

// Demo credentials for development (should be replaced with backend auth)
export const DEMO_CREDENTIALS = {
  DEFAULT_PASSWORD: import.meta.env.VITE_DEMO_PASSWORD || "password",
  ALLOW_USERNAME_AS_PASSWORD: import.meta.env.VITE_ALLOW_USERNAME_PASSWORD === "true",
};

// ========================================
// MACHINE STATUS AND STATES
// ========================================
export const MACHINE_STATUS = {
  OPERATIONAL: "operational",
  MAINTENANCE: "maintenance",
  OFFLINE: "offline",
};

export const POWER_STATUS = {
  ONLINE: "online",
  OFFLINE: "offline",
};

export const ELECTRICITY_STATUS = {
  AVAILABLE: "available",
  UNAVAILABLE: "unavailable",
};

export const FILTER_STATUS = {
  GOOD: "good",
  NEEDS_REPLACEMENT: "needs_replacement",
  CRITICAL: "critical",
};

export const CLEANING_STATUS = {
  CLEAN: "clean",
  NEEDS_CLEANING: "needs_cleaning",
  OVERDUE: "overdue",
};

// ========================================
// SUPPLY CONFIGURATION
// ========================================
export const SUPPLY_TYPES = {
  WATER: "water",
  MILK: "milk",
  COFFEE: "coffee",
  COFFEE_BEANS: "coffeeBeans",
  SUGAR: "sugar",
};

export const SUPPLY_NAMES = {
  WATER: "Water Tank",
  MILK: "Milk Container",
  COFFEE_BEANS: "Coffee Beans",
  SUGAR: "Sugar Container",
};

export const SUPPLY_DEFAULTS = {
  DEFAULT_LEVEL: parseInt(import.meta.env.VITE_DEFAULT_SUPPLY_LEVEL || "100"),
  MIN_PRESSURE: parseInt(import.meta.env.VITE_MIN_PRESSURE || "10"),
  MAX_PRESSURE: parseInt(import.meta.env.VITE_MAX_PRESSURE || "20"),
};

// ========================================
// ALERT THRESHOLDS
// ========================================
export const ALERT_THRESHOLDS = {
  LOW_SUPPLY_WARNING: parseInt(import.meta.env.VITE_LOW_SUPPLY_WARNING || "30"),
  LOW_SUPPLY_CRITICAL: parseInt(import.meta.env.VITE_LOW_SUPPLY_CRITICAL || "15"),
  MAINTENANCE_DUE_DAYS: parseInt(import.meta.env.VITE_MAINTENANCE_DUE_DAYS || "7"),
};

export const ALERT_TYPES = {
  LOW_SUPPLY: "low_supply",
  MAINTENANCE: "maintenance",
  POWER_OUTAGE: "power_outage",
  FILTER_REPLACEMENT: "filter_replacement",
  CLEANING_REQUIRED: "cleaning_required",
};

export const ALERT_PRIORITIES = {
  LOW: "low",
  MEDIUM: "medium",
  HIGH: "high",
  CRITICAL: "critical",
};

// ========================================
// NAVIGATION ROUTES
// ========================================
export const ROUTES = {
  HOME: "/",
  LOGIN: "/login",
  SIGNUP: "/signup",
  DASHBOARD: "/dashboard",
  MACHINE: "/machine",
  MACHINE_DETAIL: "/machine/:machineId",
  OFFICE: "/office/:officePath",
  OFFICE_OVERVIEW: "/office",
  NOT_FOUND: "*",
};

// Helper function to generate dynamic routes
export const generateRoute = {
  machineDetail: (machineId) => `/machine/${machineId}`,
  office: (officePath) => `/office/${officePath}`,
  officeSpecific: (officePath, machineId) =>
    machineId ? `/office/${officePath}/machine/${machineId}` : `/office/${officePath}`,
};

// ========================================
// STORAGE KEYS
// ========================================
export const STORAGE_KEYS = {
  MACHINES: "coffee_machines",
  SHARED_MACHINES: "coffee_shared_machines",
  LAST_SYNC: "coffee_last_sync",
  AUTH_TOKEN: "coffee_auth_token",
  AUTH_USER: "coffee_auth_user",
  REGISTERED_USERS: "registeredUsers",
};

// ========================================
// TIME AND DATE CONFIGURATION
// ========================================
export const TIME_CONFIG = {
  SYNC_INTERVAL_MS: parseInt(import.meta.env.VITE_SYNC_INTERVAL || "30000"),
  ALERT_REFRESH_MS: parseInt(import.meta.env.VITE_ALERT_REFRESH || "5000"),
  UI_ANIMATION_DELAY_MS: parseInt(import.meta.env.VITE_ANIMATION_DELAY || "100"),
  MAX_RECENT_REFILLS: parseInt(import.meta.env.VITE_MAX_RECENT_REFILLS || "10"),
};

// ========================================
// THEME AND STYLING
// ========================================
export const THEME_CONFIG = {
  BORDER_RADIUS: "var(--radius)",
  ANIMATION_DURATION: "200ms",
  BREAKPOINTS: {
    SM: "640px",
    MD: "768px",
    LG: "1024px",
    XL: "1280px",
    "2XL": "1536px",
  },
};

// Color mappings for status indicators
export const STATUS_COLORS = {
  operational: "green",
  maintenance: "orange",
  offline: "red",
  low: "blue",
  medium: "yellow",
  high: "orange",
  critical: "red",
};

// ========================================
// VALIDATION RULES
// ========================================
export const VALIDATION = {
  USERNAME_MIN_LENGTH: parseInt(import.meta.env.VITE_USERNAME_MIN || "3"),
  USERNAME_MAX_LENGTH: parseInt(import.meta.env.VITE_USERNAME_MAX || "20"),
  PASSWORD_MIN_LENGTH: parseInt(import.meta.env.VITE_PASSWORD_MIN || "6"),
  MACHINE_NAME_MIN_LENGTH: parseInt(import.meta.env.VITE_MACHINE_NAME_MIN || "3"),
  MACHINE_NAME_MAX_LENGTH: parseInt(import.meta.env.VITE_MACHINE_NAME_MAX || "50"),
  NOTES_MAX_LENGTH: parseInt(import.meta.env.VITE_NOTES_MAX || "500"),
};

// ========================================
// FEATURE FLAGS
// ========================================
export const FEATURES = {
  ENABLE_MQTT: import.meta.env.VITE_ENABLE_MQTT === "true",
  ENABLE_REAL_TIME_ALERTS: import.meta.env.VITE_ENABLE_REAL_TIME_ALERTS !== "false",
  ENABLE_DARK_MODE: import.meta.env.VITE_ENABLE_DARK_MODE !== "false",
  ENABLE_OFFLINE_MODE: import.meta.env.VITE_ENABLE_OFFLINE_MODE !== "false",
  ENABLE_DEBUG_TOOLS: import.meta.env.VITE_ENABLE_DEBUG_TOOLS === "true" || import.meta.env.DEV,
};

// ========================================
// API ENDPOINTS
// ========================================
export const API_ENDPOINTS = {
  // Authentication
  SIGNIN: "/auth/signin",
  SIGNUP: "/auth/signup",
  SIGNOUT: "/auth/signout",

  // Machines
  MACHINES: "/machines",
  MACHINE_BY_ID: (id) => `/machines/${id}`,
  MACHINE_BY_MACHINE_ID: (machineId) => `/machines/machine/${machineId}`,
  MACHINE_SUPPLIES: (id) => `/machines/${id}/supplies`,
  DELETE_MACHINE: (id) => `/machines/${id}`,

  // Locations
  LOCATIONS: "/machines/locations",
  OFFICES: "/machines/offices",
  FLOORS: "/machines/floors",
  MACHINES_BY_LOCATION: "/machines/by-location-office-floor",

  // Monitoring
  LOW_SUPPLY_MACHINES: "/machines/low-supplies",
  MAINTENANCE_NEEDED: "/machines/maintenance-needed",
};

// ========================================
// ERROR MESSAGES
// ========================================
export const ERROR_MESSAGES = {
  NETWORK_ERROR: "Network connection failed. Please check your internet connection.",
  AUTHENTICATION_FAILED: "Invalid username or password.",
  AUTHENTICATION_REQUIRED: "Please log in to continue.",
  MACHINE_NOT_FOUND: "Machine not found.",
  UNAUTHORIZED: "You don't have permission to perform this action.",
  VALIDATION_FAILED: "Please check your input and try again.",
  SAVE_FAILED: "Failed to save changes. Please try again.",
  LOAD_FAILED: "Failed to load data. Please refresh the page.",
};

// ========================================
// SUCCESS MESSAGES
// ========================================
export const SUCCESS_MESSAGES = {
  MACHINE_SAVED: "Machine data saved successfully.",
  SUPPLIES_UPDATED: "Supply levels updated successfully.",
  REFILL_COMPLETED: "Refill completed successfully.",
  MACHINE_DELETED: "Machine deleted permanently.",
  ALERT_RESOLVED: "Alert marked as resolved.",
  LOGIN_SUCCESS: "Logged in successfully.",
  LOGOUT_SUCCESS: "Logged out successfully.",
  REGISTRATION_SUCCESS: "Account created successfully.",
};

// Export all configuration as a single object for easy import
export const CONFIG = {
  ENV: ENV_CONFIG,
  USER_ROLES,
  DEMO_CREDENTIALS,
  MACHINE_STATUS,
  POWER_STATUS,
  ELECTRICITY_STATUS,
  FILTER_STATUS,
  CLEANING_STATUS,
  SUPPLY_TYPES,
  SUPPLY_NAMES,
  SUPPLY_DEFAULTS,
  ALERT_THRESHOLDS,
  ALERT_TYPES,
  ALERT_PRIORITIES,
  ROUTES,
  STORAGE_KEYS,
  TIME: TIME_CONFIG,
  THEME: THEME_CONFIG,
  STATUS_COLORS,
  VALIDATION,
  FEATURES,
  API_ENDPOINTS,
  ERROR_MESSAGES,
  SUCCESS_MESSAGES,
};

export default CONFIG;
