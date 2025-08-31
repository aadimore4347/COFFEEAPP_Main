import React, { createContext, useContext, useState, useEffect } from "react";
import { tokenManager } from "@/lib/api";
import { initializeMQTT, mqttClient } from "@/lib/mqtt";
import backendAPI from "@/lib/backendApi";

const defaultContextValue = {
  user: null,
  login: async () => false,
  logout: () => {},
  isLoading: true,
  isAuthenticated: false,
  register: async () => false,
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
      const token = tokenManager.getToken();
      if (token && !tokenManager.isTokenExpired(token)) {
        try {
          // Token exists, validate it by fetching user profile
          backendAPI.setToken(token);
          const userData = await backendAPI.getMe();
          setUser(userData);
          await initializeMQTT();
        } catch (error) {
          console.error("Session validation failed:", error);
          // If token is invalid, log the user out
          logout();
        }
      }
      setIsLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (username, password) => {
    setIsLoading(true);
    try {
      const response = await backendAPI.login({ username, password });
      if (response.accessToken) {
        const userData = response.user;

        backendAPI.setToken(response.accessToken);
        tokenManager.setToken(response.accessToken);
        // We store the user object to avoid an extra /me call right after login
        localStorage.setItem("coffee_auth_user", JSON.stringify(userData));

        setUser(userData);
        await initializeMQTT();

        setIsLoading(false);
        return true;
      }
      // This path should ideally not be taken if API call is successful
      // but without a token. Included for robustness.
      setIsLoading(false);
      return false;
    } catch (error) {
      console.error("Login failed:", error);
      setIsLoading(false);
      return false;
    }
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
      // It's helpful to pass the error message to the component
      throw error;
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

  const contextValue = {
    user,
    login,
    logout,
    register,
    isLoading,
    isAuthenticated: !!user,
  };

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};
