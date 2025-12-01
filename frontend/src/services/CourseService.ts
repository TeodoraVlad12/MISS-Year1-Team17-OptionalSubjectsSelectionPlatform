export interface Course {
  id: number;
  name: string;
  code: string;
}

export interface CourseRequirement {
  id: number;
  mandatoryId: number;
  mandatoryName: string;
  percentage: number;
}

export interface CreateRequirementRequest {
  mandatoryCourseId: number;
  percentage: number;
}

const API_BASE_URL = 'http://localhost:8080/api';

class CourseService {
  private async request<T>(url: string, options: RequestInit = {}): Promise<T> {
    const token = localStorage.getItem('authToken');
    
    const response = await fetch(`${API_BASE_URL}${url}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(token && { Authorization: `Bearer ${token}` }),
        ...options.headers,
      },
    });

    if (!response.ok) {
      let errorMessage = `HTTP error! status: ${response.status}`;
      try {
        const errorData = await response.json();
        if (errorData.error) {
          errorMessage = errorData.error;
        } else if (errorData.message) {
          errorMessage = errorData.message;
        }
      } catch {
        // If we can't parse the error response, use the default message
      }
      throw new Error(errorMessage);
    }

    // Handle responses with no content (like DELETE 204)
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      return null as T; // Return null for non-JSON responses (like DELETE)
    }

    const text = await response.text();
    if (!text) {
      return null as T; // Return null for empty responses
    }

    return JSON.parse(text);
  }

  async getOptionalCourses(): Promise<Course[]> {
    return this.request<Course[]>('/courses/optional');
  }

  async getMandatoryCourses(): Promise<Course[]> {
    return this.request<Course[]>('/courses/mandatory');
  }

  async getRequirements(optionalCourseId: number): Promise<CourseRequirement[]> {
    return this.request<CourseRequirement[]>(`/optional/requirements/${optionalCourseId}`);
  }

  async createRequirement(
    optionalCourseId: number, 
    requirement: CreateRequirementRequest
  ): Promise<CourseRequirement> {
    return this.request<CourseRequirement>(`/optional/requirements/${optionalCourseId}`, {
      method: 'POST',
      body: JSON.stringify(requirement),
    });
  }

  async updateRequirement(
    requirementId: number, 
    requirement: CreateRequirementRequest
  ): Promise<CourseRequirement> {
    return this.request<CourseRequirement>(`/optional/requirements/${requirementId}`, {
      method: 'PUT',
      body: JSON.stringify(requirement),
    });
  }

  async deleteRequirement(requirementId: number): Promise<void> {
    return this.request<void>(`/optional/requirements/${requirementId}`, {
      method: 'DELETE',
    });
  }
}

export default new CourseService();
