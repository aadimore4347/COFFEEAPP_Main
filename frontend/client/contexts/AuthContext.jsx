import React, { createContext, useContext, useState, useEffect } from "react";
import { apiClient, tokenManager } from "@/lib/api";
import { initializeMQTT, mqttClient } from "@/lib/mqtt";
import { USER_ROLES, DEMO_CREDENTIALS } from "@/config";
import { generateDemoUsers } from "@/config/machines";
import { dataManager } from "@/lib/dataManager";
// Import new backend API
import backendAPI from "@/lib/backendApi";

const defaultContextValue = {
  user: null,
  login: async () => false,
  logout: () => {},
  isLoading: true,
  isAuthenticated: false,
  register: async () => false,
  refreshToken: async () => false,
};

const AuthContext = createContext(defaultContextValue);

export const useAuth = () => {
  const context = useContext(AuthContext);
  return context;
};

const isDemoMode = () => {
  const hostname = window.location.hostname;
  return (
    hostname.includes(".fly.dev") ||
    hostname.includes(".netlify.app") ||
    hostname.includes(".vercel.app") ||
    hostname.includes("builder.io") ||
    (hostname.includes("localhost") === false && hostname !== "127.0.0.1")
  );
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [demoMode] = useState(isDemoMode());

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const storedUser = localStorage.getItem("coffee_auth_user");
        const token = tokenManager.getToken();

        if (storedUser && token && !tokenManager.isTokenExpired(token)) {
          const userData = JSON.parse(storedUser);
          setUser(userData);
          backendAPI.setToken(token);

          try {
            dataManager.getAllMachinesFromSharedStorage();
            dataManager.ensureUserDataPersistence(userData.role, userData.officeName);
            console.log('✅ AUTH: Data persistence restored for returning user');
          } catch (error) {
            console.warn('Failed to initialize data persistence on auth restoration:', error);
          }

          initializeMQTT().then((connected) => {
            if (connected) {
              // MQTT initialized for authenticated user
            }
          });
        } else {
          // Try to refresh token if expired
          if (token && tokenManager.isTokenExpired(token)) {
            try {
              await refreshTokenFromStorage();
            } catch (error) {
              console.warn('Token refresh failed, logging out:', error);
              logout();
            }
          } else {
            tokenManager.removeToken();
          }
        }
      } catch (error) {
        console.error('Auth initialization error:', error);
        logout();
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  // Refresh token from storage
  const refreshTokenFromStorage = async () => {
    try {
      const refreshToken = localStorage.getItem("coffee_auth_refresh_token");
      if (!refreshToken) throw new Error('No refresh token');

      const response = await backendAPI.refreshToken(refreshToken);
      if (response.accessToken) {
        backendAPI.setToken(response.accessToken);
        tokenManager.setToken(response.accessToken);
        
        if (response.refreshToken) {
          localStorage.setItem("coffee_auth_refresh_token", response.refreshToken);
        }
        
        return true;
      }
      return false;
    } catch (error) {
      console.error('Token refresh failed:', error);
      throw error;
    }
  };

  const login = async (username, password) => {
    setIsLoading(true);

    try {
      // Try backend authentication first
      if (!demoMode) {
        try {
          const response = await backendAPI.login({ username, password });
          
          if (response.accessToken) {
            const userData = {
              id: response.user.id || response.user.username,
              username: response.user.username,
              name: response.user.name || response.user.username,
              role: response.user.role || 'FACILITY',
              city: response.user.city || 'Unknown',
              officeName: response.user.officeName || 'Unknown Office',
              email: response.user.email,
            };

            // Store tokens
            backendAPI.setToken(response.accessToken);
            tokenManager.setToken(response.accessToken);
            
            if (response.refreshToken) {
              localStorage.setItem("coffee_auth_refresh_token", response.refreshToken);
            }

            setUser(userData);
            localStorage.setItem("coffee_auth_user", JSON.stringify(userData));

            try {
              dataManager.getAllMachinesFromSharedStorage();
              dataManager.ensureUserDataPersistence(userData.role, userData.officeName);
              console.log('✅ LOGIN: Backend authentication successful');
            } catch (error) {
              console.warn('Failed to initialize data persistence on login:', error);
            }

            await initializeMQTT();
            return true;
          }
        } catch (backendError) {
          console.warn('Backend authentication failed, falling back to demo mode:', backendError);
          // Fall back to demo mode if backend is unavailable
        }
      }

      // Demo mode authentication (existing logic)
      const registeredUsers = JSON.parse(
        localStorage.getItem("registeredUsers") || "[]",
      );
      const foundUser = registeredUsers.find(
        (user) => user.username === username && user.password === password,
      );

      if (foundUser) {
        const userData = {
          id: foundUser.username,
          username: foundUser.username,
          name: foundUser.name,
          role: foundUser.role,
          city: foundUser.city,
          officeName: foundUser.officeName,
        };

        setUser(userData);
        localStorage.setItem("coffee_auth_user", JSON.stringify(userData));
        localStorage.setItem("coffee_auth_token", "simple_token_" + Date.now());

        try {
          dataManager.getAllMachinesFromSharedStorage();
          dataManager.ensureUserDataPersistence(userData.role, userData.officeName);
          console.log('✅ LOGIN: Demo mode authentication successful');
        } catch (error) {
          console.warn('Failed to initialize data persistence on login:', error);
        }

        await initializeMQTT();
        setIsLoading(false);
        return true;
      }

      setIsLoading(false);
      return false;
    } catch (error) {
      console.error('Login error:', error);
      setIsLoading(false);
      return false;
    }
  };

  const register = async (userData) => {
    setIsLoading(true);

    try {
      // Try backend registration first
      if (!demoMode) {
        try {
          const response = await backendAPI.register(userData);
          
          if (response.success || response.user) {
            console.log('✅ REGISTER: Backend registration successful');
            setIsLoading(false);
            return true;
          }
        } catch (backendError) {
          console.warn('Backend registration failed, falling back to demo mode:', backendError);
          // Fall back to demo mode if backend is unavailable
        }
      }

      // Demo mode registration (existing logic)
      const registeredUsers = JSON.parse(
        localStorage.getItem("registeredUsers") || "[]",
      );

      const newUser = {
        username: userData.username,
        password: userData.password,
        name: userData.name,
        role: userData.role || "FACILITY",
        city: userData.city || "Unknown",
        officeName: userData.officeName || "Unknown Office",
      };

      registeredUsers.push(newUser);
      localStorage.setItem("registeredUsers", JSON.stringify(registeredUsers));

      console.log('✅ REGISTER: Demo mode registration successful');
      setIsLoading(false);
      return true;
    } catch (error) {
      console.error('Registration error:', error);
      setIsLoading(false);
      return false;
    }
  };

  const logout = () => {
    setUser(null);
    backendAPI.setToken(null);
    tokenManager.removeToken();
    localStorage.removeItem("coffee_auth_user");
    localStorage.removeItem("coffee_auth_refresh_token");
    localStorage.removeItem("coffee_auth_token");
    
    if (mqttClient) {
      mqttClient.disconnect();
    }
    
    console.log('✅ LOGOUT: User logged out successfully');
  };

  const refreshToken = async () => {
    try {
      return await refreshTokenFromStorage();
    } catch (error) {
      console.error('Token refresh failed:', error);
      logout();
      return false;
    }
  };

  const contextValue = {
    user,
    login,
    logout,
    register,
    refreshToken,
    isLoading,
    isAuthenticated: !!user,
    demoMode,
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};
