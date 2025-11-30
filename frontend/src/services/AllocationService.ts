import { ApiService } from "./ApiService";
import type { StudentAllocation } from "../models/StudentAllocation";
import type { OptionalCourse } from "../models/OptionalCourse";

interface CoursePreference {
    courseId: number;
    priority: number;
}

interface CoursePackage {
    id: number;
    name: string;
    courses: OptionalCourse[];
}

interface PreferenceDTO {
    priority: number;
    studentId: number;
    courseId: number;
}

export class AllocationService extends ApiService {
    constructor(baseUrl: string) {
        super(baseUrl, "/api/allocation");
    }

    async getYears(): Promise<number[]> {
        try {
            const response = await fetch(`${this.baseUrl}/api/students/years`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                },
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Backend error:', response.status, errorText);
                throw new Error(`Failed to get years: ${response.status} ${errorText}`);
            }
            return response.body ? await response.json() : [];
        } catch (error) {
            console.error('Get years error:', error);
            throw error;
        }
    }

    async getSpecializations(): Promise<string[]> {
        try {
            const response = await fetch(`${this.baseUrl}/api/students/specializations`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                },
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Backend error:', response.status, errorText);
                throw new Error(`Failed to get specializations: ${response.status} ${errorText}`);
            }
            return response.body ? await response.json() : [];
        } catch (error) {
            console.error('Get specializations error:', error);
            throw error;
        }
    }

    async assignOptionals(
        year: number,
        specialization: string
    ): Promise<StudentAllocation[]> {
        try {
            const response = await fetch(`${this.baseUrl}/api/allocation/run`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                },
                body: JSON.stringify({ year, specialization })
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Backend error:', response.status, errorText);
                throw new Error(`Failed to run allocation: ${response.status} ${errorText}`);
            }
            return response.body ? await response.json() : [];
        } catch (error) {
            console.error('Run allocation error:', error);
            throw error;
        }
    }
    
    async getCoursePackages(studentId: number): Promise<CoursePackage[]> {
        try {
            const response = await fetch(`${this.baseUrl}/api/students/${studentId}/optionals`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const courses = await response.json();
            
            // Group courses by package (assuming courses have packageId)
            const packageMap = new Map<number, OptionalCourse[]>();
            courses.forEach((course: any) => {
                const packageId = course.packageId || 1; // Fallback for mock data
                if (!packageMap.has(packageId)) {
                    packageMap.set(packageId, []);
                }
                packageMap.get(packageId)!.push(course);
            });

            // Convert to CoursePackage array
            return Array.from(packageMap.entries()).map(([id, courses]) => ({
                id,
                name: `Package ${id}`,
                courses
            }));
        } catch (error) {
            console.error('Failed to load courses from backend:', error);
            // Fallback to mock data for development
            return this.getMockPackages();
        }
    }

    private getMockPackages(): CoursePackage[] {
        return [
            {
                id: 1,
                name: "Package 1",
                courses: [
                    { id: 101, name: "Advanced Algorithms", code: "CS301", maxStudents: 30 },
                    { id: 102, name: "Data Mining", code: "CS302", maxStudents: 25 },
                    { id: 103, name: "Computational Geometry", code: "CS303", maxStudents: 20 }
                ]
            },
            {
                id: 2,
                name: "Package 2",
                courses: [
                    { id: 201, name: "Machine Learning", code: "AI301", maxStudents: 28 },
                    { id: 202, name: "Neural Networks", code: "AI302", maxStudents: 22 },
                    { id: 203, name: "Computer Vision", code: "AI303", maxStudents: 24 }
                ]
            },
            {
                id: 3,
                name: "Package 3",
                courses: [
                    { id: 301, name: "Software Architecture", code: "SE301", maxStudents: 32 },
                    { id: 302, name: "DevOps Practices", code: "SE302", maxStudents: 26 },
                    { id: 303, name: "Quality Assurance", code: "SE303", maxStudents: 28 }
                ]
            }
        ];
    }

    async saveStudentPreferences(studentId: number, packagePreferences: any[]): Promise<void> {
        // Flatten package preferences to individual PreferenceDTOs
        const preferenceDTOs: PreferenceDTO[] = [];
        
        packagePreferences.forEach(pkg => {
            pkg.coursePreferences.forEach((coursePref: any) => {
                preferenceDTOs.push({
                    priority: coursePref.priority,
                    studentId: studentId,
                    courseId: coursePref.courseId
                });
            });
        });

        console.log('Sending preferences to backend:', preferenceDTOs);
        
        try {
            const response = await fetch(`${this.baseUrl}/api/students/${studentId}/preferences`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                },
                body: JSON.stringify(preferenceDTOs)
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Backend error:', response.status, errorText);
                throw new Error(`Failed to save preferences: ${response.status} ${errorText}`);
            }

            console.log('Preferences saved successfully');
        } catch (error) {
            console.error('Save preferences error:', error);
            throw error;
        }
    }
}
