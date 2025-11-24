import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { useServices } from '../services/ServicesContext';
import type { LoginResponse, LoginRequest } from '../services/AuthService';

interface AuthContextType {
  user: Omit<LoginResponse, 'token'> | null;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<Omit<LoginResponse, 'token'> | null>(null);
  const [loading, setLoading] = useState(true);
  const { authService } = useServices();

  useEffect(() => {
    const initializeAuth = async () => {
      const token = authService.getAuthToken();
      const savedUserInfo = authService.getUserInfo();

      if (token && savedUserInfo) {
        try {
          // Validate token with backend
          const isValid = await authService.validateToken(token);
          if (isValid) {
            setUser(savedUserInfo);
          } else {
            // Token is invalid, clear storage
            authService.logout();
          }
        } catch (error) {
          console.error('Token validation failed:', error);
          authService.logout();
        }
      }
      
      setLoading(false);
    };

    initializeAuth();
  }, [authService]);

  const login = async (credentials: LoginRequest): Promise<void> => {
    const response = await authService.login(credentials);
    
    // Store token and user info
    authService.setAuthToken(response.token);
    
    const userInfo = {
      role: response.role,
      userId: response.userId,
      email: response.email,
      firstName: response.firstName,
      lastName: response.lastName,
      matriculationNumber: response.matriculationNumber,
      academicYear: response.academicYear,
      specialization: response.specialization,
      groupNumber: response.groupNumber
    };
    
    authService.saveUserInfo(userInfo);
    setUser(userInfo);
  };

  const logout = () => {
    authService.logout();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
