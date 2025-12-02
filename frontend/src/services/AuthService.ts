import { ApiService } from './ApiService';

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  token: string;
  role: 'STUDENT' | 'ADMIN';
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  matriculationNumber?: string;
  academicYear?: number;
  specialization?: string;
  groupNumber?: string;
}

export type RegisterRequest = {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  matriculationNumber?: string;
  academicYear?: number;
  specialization?: string;
  groupNumber?: string;
};

export type RegisterResponse = {
  success?: boolean;
  message?: string;
  monitorMessages?: string[];
  [key: string]: any;
};

export class AuthService extends ApiService {
  constructor(baseUrl: string) {
    super(baseUrl, '/api/auth');
  }

  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await fetch(`${this.baseUrl}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials),
    });

    if (!response.ok) {
      const err = await this.parseError(response);
      throw new Error(`Login failed: ${err}`);
    }

    return response.json();
  }

  private async parseError(response: Response): Promise<string> {
    try {
      const json = await response.json();
      if (json && (json.message || json.error)) return json.message || json.error;
      return JSON.stringify(json);
    } catch {
      try {
        return await response.text();
      } catch {
        return `HTTP ${response.status}`;
      }
    }
  }

  async register(payload: RegisterRequest): Promise<RegisterResponse> {
    const response = await fetch(`${this.baseUrl}/api/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });

    if (response.ok) {
      try {
        return (await response.json()) as RegisterResponse;
      } catch {
        return { success: true, message: 'Registered' };
      }
    }

    const err = await this.parseError(response);
    throw new Error(err || 'Registration failed');
  }

  async validateToken(token: string): Promise<boolean> {
    try {
      const response = await fetch(`${this.baseUrl}/api/auth/validate`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const result = await response.json();
        return result.valid === true;
      }
      return false;
    } catch (error) {
      console.error('Token validation failed:', error);
      return false;
    }
  }

  setAuthToken(token: string): void {
    localStorage.setItem('authToken', token);
  }

  getAuthToken(): string | null {
    return localStorage.getItem('authToken');
  }

  removeAuthToken(): void {
    localStorage.removeItem('authToken');
  }

  logout(): void {
    this.removeAuthToken();
    localStorage.removeItem('userInfo');
  }

  saveUserInfo(userInfo: Omit<LoginResponse, 'token'>): void {
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
  }

  getUserInfo(): Omit<LoginResponse, 'token'> | null {
    const userInfoStr = localStorage.getItem('userInfo');
    return userInfoStr ? JSON.parse(userInfoStr) : null;
  }
}

export type { LoginRequest, LoginResponse };
