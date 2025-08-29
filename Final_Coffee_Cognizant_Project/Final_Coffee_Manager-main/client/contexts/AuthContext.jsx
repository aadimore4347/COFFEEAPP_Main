import React, { createContext, useContext, useState, useEffect } from "react";
import { apiClient, tokenManager } from "@/lib/api";
import { initializeMQTT, mqttClient } from "@/lib/mqtt";
import { USER_ROLES, DEMO_CREDENTIALS } from "@/config";
import { generateDemoUsers } from "@/config/machines";
import { dataManager } from "@/lib/dataManager";

const defaultContextValue = {
  user: null,
  login: async () => false,
  logout: () => {},
  isLoading: true,
  isAuthenticated: false,
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
    const storedUser = localStorage.getItem("coffee_auth_user");
    const token = tokenManager.getToken();

    if (storedUser && token && !tokenManager.isTokenExpired(token)) {
      setUser(JSON.parse(storedUser));

      try {
        const userData = JSON.parse(storedUser);
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
      tokenManager.removeToken();
    }

    setIsLoading(false);
  }, []);

  const login = async (username, password) => {
    setIsLoading(true);

    try {
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
          console.log('✅ LOGIN: Data persistence established for user');
        } catch (error) {
          console.warn('Failed to initialize data persistence on login:', error);
        }

        await initializeMQTT();

        setIsLoading(false);
        return true;
      }

      return performMockLogin(username, password);
    } catch (error) {
      return performMockLogin(username, password);
    }
  };

  const performMockLogin = async (username, password) => {
    const mockUsers = generateDemoUsers();

    const foundUser = mockUsers.find((u) => u.username === username);
    const isValidPassword = password === DEMO_CREDENTIALS.DEFAULT_PASSWORD ||
                           (DEMO_CREDENTIALS.ALLOW_USERNAME_AS_PASSWORD && password === username);

    if (foundUser && isValidPassword) {
      const mockToken = btoa(
        JSON.stringify({
          sub: foundUser.username,
          role: foundUser.role,
          exp: Math.floor(Date.now() / 1000) + 24 * 60 * 60,
        }),
      );

      tokenManager.setToken(mockToken);

      const userData = {
        id: foundUser.id,
        username: foundUser.username,
        name: foundUser.name,
        role: foundUser.role,
        city: foundUser.city,
        officeName: foundUser.officeName,
      };

      setUser(userData);
      localStorage.setItem("coffee_auth_user", JSON.stringify(userData));

      try {
        dataManager.getAllMachinesFromSharedStorage();
        dataManager.ensureUserDataPersistence(userData.role, userData.officeName);
        console.log('✅ DEMO LOGIN: Data persistence established for demo user');
      } catch (error) {
        console.warn('Failed to initialize data persistence on demo login:', error);
      }

      await initializeMQTT();

      setIsLoading(false);
      return true;
    }

    setIsLoading(false);
    return false;
  };

  const logout = async () => {
    if (!demoMode) {
      try {
        await apiClient.logout();
      } catch (error) {
        // ignore
      }
    }

    setUser(null);
    tokenManager.removeToken();

    mqttClient.disconnect();
  };

  const isAuthenticated = !!user && !!tokenManager.getToken();

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        logout,
        isLoading,
        isAuthenticated,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
