import React, { createContext, useContext, useState, useEffect } from "react";
import { tokenManager } from "@/lib/api";
import { initializeMQTT, mqttClient } from "@/lib/mqtt";
import { dataManager } from "@/lib/dataManager";
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
  return useContext(AuthContext);
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const token = tokenManager.getToken();
        if (token && !tokenManager.isTokenExpired(token)) {
          // If a valid token exists, try to fetch user data
          await refreshTokenAndUser(token);
        }
      } catch (error) {
        console.error('Auth initialization error:', error);
        // Clear tokens if init fails
        logout();
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  const refreshTokenAndUser = async (token) => {
    backendAPI.setToken(token);
    // In a real app, you would have an endpoint like /auth/me to get user data
    // For now, we will decode the token or retrieve user data from localStorage
    const storedUser = localStorage.getItem("coffee_auth_user");
    if (storedUser) {
        const userData = JSON.parse(storedUser);
        setUser(userData);
        dataManager.ensureUserDataPersistence(userData.role, userData.officeName);
        await initializeMQTT();
    } else {
        // If user data is not in local storage, the token is invalid.
        logout();
        throw new Error("No user data found for token.");
    }
  };

  const login = async (username, password) => {
    setIsLoading(true);
    try {
      const response = await backendAPI.login({ username, password });
      if (response.accessToken) {
        const userData = {
          id: response.user.id,
          username: response.user.username,
          name: response.user.name || response.user.username,
          role: response.user.role || 'FACILITY',
          facilityId: response.user.facilityId,
        };

        backendAPI.setToken(response.accessToken);
        tokenManager.setToken(response.accessToken);
        localStorage.setItem("coffee_auth_user", JSON.stringify(userData));

        setUser(userData);

        dataManager.ensureUserDataPersistence(userData.role, userData.facilityId);
        await initializeMQTT();

        setIsLoading(false);
        return true;
      }
    } catch (error) {
      console.error("Login failed:", error);
      setIsLoading(false);
      return false;
    }
    // Should not be reached, but as a fallback
    setIsLoading(false);
    return false;
  };

  const register = async (userData) => {
    setIsLoading(true);
    try {
      await backendAPI.register(userData);
      setIsLoading(false);
      return true;
    } catch (error) {
      console.error('Registration failed:', error);
      setIsLoading(false);
      return false;
    }
  };

  const logout = () => {
    setUser(null);
    backendAPI.setToken(null);
    tokenManager.removeToken();
    localStorage.removeItem("coffee_auth_user");

    if (mqttClient && mqttClient.connected) {
      mqttClient.end();
    }
    console.log('User logged out successfully');
  };

  const refreshToken = async () => {
    // This is a placeholder. A real implementation would call a refresh endpoint.
    // For this app, the token has a long expiry and we re-login if it expires.
    return false;
  };

  const contextValue = {
    user,
    login,
    logout,
    register,
    refreshToken,
    isLoading,
    isAuthenticated: !!user,
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};
