// import { ApiService } from "./ApiService";
// import type { StudentAllocation } from "../models/StudentAllocation";
// import type { OptionalCourse } from "../models/OptionalCourse";

// interface CoursePreference {
//     courseId: number;
//     priority: number;
// }

// interface CoursePackage {
//     id: number;
//     name: string;
//     courses: OptionalCourse[];
// }

// interface PreferenceDTO {
//     priority: number;
//     studentId: number;
//     courseId: number;
// }

// export class AllocationService extends ApiService {
//     constructor(baseUrl: string) {
//         super(baseUrl, "/api/allocation");
//     }

//     async getYears(): Promise<number[]> {
//         return new Promise((resolve) => {
//             setTimeout(() => {
//                 resolve([1, 2, 3]);
//             }, 300);
//         });
//     }

//     async getSpecializations(): Promise<string[]> {
//         return new Promise((resolve) => {
//             setTimeout(() => {
//                 resolve([
//                     "Computer Science",
//                     "Software Engineering",
//                     "Information Systems",
//                     "Artificial Intelligence",
//                 ]);
//             }, 300);
//         });
//     }

//     async assignOptionals(
//         // year: number,
//         // specialization: string
//     ): Promise<StudentAllocation[]> {
//         return new Promise((resolve) => {
//             setTimeout(() => {
//                 resolve(this.getMockedAllocations(/*year, specialization*/));
//             }, 1000);
//         });
//     }

//     private getMockedAllocations(
//         // year: number,
//         // specialization: string
//     ): StudentAllocation[] {
//         return [
//             {
//                 student: {
//                     id: 1,
//                     firstName: "Ion",
//                     lastName: "Popescu",
//                     email: "ion.popescu@student.uaic.ro",
//                     studentNumber: "A12345",
//                 },
//                 assignedCourses: [
//                     {
//                         id: 101,
//                         name: "Advanced Algorithms",
//                         code: "CS301",
//                         maxStudents: 30,
//                     },
//                     {
//                         id: 102,
//                         name: "Machine Learning",
//                         code: "CS302",
//                         maxStudents: 25,
//                     },
//                 ],
//             },
//             {
//                 student: {
//                     id: 2,
//                     firstName: "Maria",
//                     lastName: "Ionescu",
//                     email: "maria.ionescu@student.uaic.ro",
//                     studentNumber: "A12346",
//                 },
//                 assignedCourses: [
//                     {
//                         id: 103,
//                         name: "Web Development",
//                         code: "CS303",
//                         maxStudents: 28,
//                     },
//                     {
//                         id: 104,
//                         name: "Database Systems",
//                         code: "CS304",
//                         maxStudents: 32,
//                     },
//                 ],
//             },
//             {
//                 student: {
//                     id: 3,
//                     firstName: "Andrei",
//                     lastName: "Vasilescu",
//                     email: "andrei.vasilescu@student.uaic.ro",
//                     studentNumber: "A12347",
//                 },
//                 assignedCourses: [
//                     {
//                         id: 101,
//                         name: "Advanced Algorithms",
//                         code: "CS301",
//                         maxStudents: 30,
//                     },
//                     {
//                         id: 105,
//                         name: "Software Engineering",
//                         code: "CS305",
//                         maxStudents: 30,
//                     },
//                 ],
//             },
//         ];
//     }

//     async getOptionalCourses(/*academicYear: number, specialization: string*/): Promise<OptionalCourse[]> {
//         return new Promise((resolve) => {
//             setTimeout(() => {
//                 resolve([
//                     { id: 101, name: "Advanced Algorithms", code: "CS301", maxStudents: 30 },
//                     { id: 102, name: "Machine Learning", code: "CS302", maxStudents: 25 },
//                     { id: 103, name: "Web Development", code: "CS303", maxStudents: 28 },
//                     { id: 104, name: "Database Systems", code: "CS304", maxStudents: 32 },
//                     { id: 105, name: "Software Engineering", code: "CS305", maxStudents: 30 },
//                 ]);
//             }, 500);
//         });
//     }

//     async getCoursePackages(studentId: number): Promise<CoursePackage[]> {
//         try {
//             const response = await fetch(`${this.baseUrl}/api/students/${studentId}/optionals`, {
//                 method: 'GET',
//                 headers: {
//                     'Content-Type': 'application/json',
//                     'Authorization': `Bearer ${localStorage.getItem('authToken')}`
//                 }
//             });

//             if (!response.ok) {
//                 throw new Error(`HTTP ${response.status}`);
//             }

//             const courses = await response.json();
            
//             // Group courses by package (assuming courses have packageId)
//             const packageMap = new Map<number, OptionalCourse[]>();
//             courses.forEach((course: any) => {
//                 const packageId = course.packageId || 1; // Fallback for mock data
//                 if (!packageMap.has(packageId)) {
//                     packageMap.set(packageId, []);
//                 }
//                 packageMap.get(packageId)!.push(course);
//             });

//             // Convert to CoursePackage array
//             return Array.from(packageMap.entries()).map(([id, courses]) => ({
//                 id,
//                 name: `Package ${id}`,
//                 courses
//             }));
//         } catch (error) {
//             console.error('Failed to load courses from backend:', error);
//             // Fallback to mock data for development
//             return this.getMockPackages();
//         }
//     }

//     private getMockPackages(): CoursePackage[] {
//         return [
//             {
//                 id: 1,
//                 name: "Package 1",
//                 courses: [
//                     { id: 101, name: "Advanced Algorithms", code: "CS301", maxStudents: 30 },
//                     { id: 102, name: "Data Mining", code: "CS302", maxStudents: 25 },
//                     { id: 103, name: "Computational Geometry", code: "CS303", maxStudents: 20 }
//                 ]
//             },
//             {
//                 id: 2,
//                 name: "Package 2",
//                 courses: [
//                     { id: 201, name: "Machine Learning", code: "AI301", maxStudents: 28 },
//                     { id: 202, name: "Neural Networks", code: "AI302", maxStudents: 22 },
//                     { id: 203, name: "Computer Vision", code: "AI303", maxStudents: 24 }
//                 ]
//             },
//             {
//                 id: 3,
//                 name: "Package 3",
//                 courses: [
//                     { id: 301, name: "Software Architecture", code: "SE301", maxStudents: 32 },
//                     { id: 302, name: "DevOps Practices", code: "SE302", maxStudents: 26 },
//                     { id: 303, name: "Quality Assurance", code: "SE303", maxStudents: 28 }
//                 ]
//             }
//         ];
//     }

//     async saveStudentPreferences(studentId: number, packagePreferences: any[]): Promise<void> {
//         // Flatten package preferences to individual PreferenceDTOs
//         const preferenceDTOs: PreferenceDTO[] = [];
        
//         packagePreferences.forEach(pkg => {
//             pkg.coursePreferences.forEach((coursePref: any) => {
//                 preferenceDTOs.push({
//                     priority: coursePref.priority,
//                     studentId: studentId,
//                     courseId: coursePref.courseId
//                 });
//             });
//         });

//         console.log('Sending preferences to backend:', preferenceDTOs);
        
//         try {
//             const response = await fetch(`${this.baseUrl}/api/students/${studentId}/preferences`, {
//                 method: 'POST',
//                 headers: {
//                     'Content-Type': 'application/json',
//                     'Authorization': `Bearer ${localStorage.getItem('authToken')}`
//                 },
//                 body: JSON.stringify(preferenceDTOs)
//             });

//             if (!response.ok) {
//                 const errorText = await response.text();
//                 console.error('Backend error:', response.status, errorText);
//                 throw new Error(`Failed to save preferences: ${response.status} ${errorText}`);
//             }

//             console.log('Preferences saved successfully');
//         } catch (error) {
//             console.error('Save preferences error:', error);
//             throw error;
//         }
//     }
// }


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

interface BackendAllocation {
    studentId: number;
    studentName: string;
    allocatedCourseId: number;
    allocatedCourseName: string;
    preferenceRank: number;
}

export class AllocationService extends ApiService {
    constructor(baseUrl: string) {
        super(baseUrl, "/api/allocation");
    }

    async getYears(): Promise<number[]> {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve([1, 2, 3]);
            }, 300);
        });
    }

    async getSpecializations(): Promise<string[]> {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve([
                    "Computer Science",
                    "Software Engineering",
                    "Information Systems",
                    "Artificial Intelligence",
                ]);
            }, 300);
        });
    }

    async assignOptionals(): Promise<StudentAllocation[]> {
        try {
            console.log('Running allocation algorithm...');
            
            // Option 1: DEMO mode (empty request)
            const response = await fetch(`${this.baseUrl}/api/allocation/run`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                },
                body: JSON.stringify({
                    // Empty object triggers DEMO mode
                    // Or you can specify: "allocationStrategy": "GALE_SHAPLEY"
                })
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Backend error:', response.status, errorText);
                throw new Error(`Allocation failed: ${response.status}`);
            }

            const backendAllocations: BackendAllocation[] = await response.json();
            console.log('Received allocations from backend:', backendAllocations.length);
            
            if (backendAllocations.length === 0) {
                console.log('No allocations returned, using mock data');
                return this.getMockedAllocations();
            }
            
            return this.transformBackendToFrontendFormat(backendAllocations);
            
        } catch (error) {
            console.error('Failed to run allocation:', error);
            // Fallback to mock data for presentation
            console.log('Using mock data for demo purposes');
            return this.getMockedAllocations();
        }
    }

    private transformBackendToFrontendFormat(backendAllocations: BackendAllocation[]): StudentAllocation[] {
        // Group allocations by student
        const studentMap = new Map<number, StudentAllocation>();
        
        backendAllocations.forEach((allocation) => {
            const studentId = allocation.studentId;
            
            if (!studentMap.has(studentId)) {
                // Extract first and last name from studentName
                const nameParts = allocation.studentName?.split(' ') || [];
                const firstName = nameParts.length > 0 ? nameParts[0] : 'Student';
                const lastName = nameParts.length > 1 ? nameParts.slice(1).join(' ') : 'Unknown';
                
                studentMap.set(studentId, {
                    student: {
                        id: studentId,
                        firstName: firstName,
                        lastName: lastName,
                        email: `student${studentId}@uaic.ro`,
                        studentNumber: `S${studentId.toString().padStart(5, '0')}`
                    },
                    assignedCourses: []
                });
            }
            
            // Add course to student
            const studentAllocation = studentMap.get(studentId)!;
            studentAllocation.assignedCourses.push({
                id: allocation.allocatedCourseId,
                name: allocation.allocatedCourseName || `Course ${allocation.allocatedCourseId}`,
                code: `CS${allocation.allocatedCourseId.toString().padStart(3, '0')}`,
                maxStudents: 30
            });
        });
        
        return Array.from(studentMap.values());
    }

    private getMockedAllocations(): StudentAllocation[] {
        return [
            {
                student: {
                    id: 1,
                    firstName: "Ion",
                    lastName: "Popescu",
                    email: "ion.popescu@student.uaic.ro",
                    studentNumber: "A12345",
                },
                assignedCourses: [
                    {
                        id: 101,
                        name: "Advanced Algorithms",
                        code: "CS301",
                        maxStudents: 30,
                    },
                    {
                        id: 102,
                        name: "Machine Learning",
                        code: "CS302",
                        maxStudents: 25,
                    },
                ],
            },
            {
                student: {
                    id: 2,
                    firstName: "Maria",
                    lastName: "Ionescu",
                    email: "maria.ionescu@student.uaic.ro",
                    studentNumber: "A12346",
                },
                assignedCourses: [
                    {
                        id: 103,
                        name: "Web Development",
                        code: "CS303",
                        maxStudents: 28,
                    },
                    {
                        id: 104,
                        name: "Database Systems",
                        code: "CS304",
                        maxStudents: 32,
                    },
                ],
            },
        ];
    }

    async getOptionalCourses(): Promise<OptionalCourse[]> {
        try {
            // Try to fetch real courses from backend
            const response = await fetch(`${this.baseUrl}/api/courses/optional`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`
                }
            });
            
            if (response.ok) {
                return await response.json();
            }
        } catch (error) {
            console.log('Using mock courses:', error);
        }
        
        // Fallback to mock courses
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve([
                    { id: 101, name: "Advanced Algorithms", code: "CS301", maxStudents: 30 },
                    { id: 102, name: "Machine Learning", code: "CS302", maxStudents: 25 },
                    { id: 103, name: "Web Development", code: "CS303", maxStudents: 28 },
                    { id: 104, name: "Database Systems", code: "CS304", maxStudents: 32 },
                    { id: 105, name: "Software Engineering", code: "CS305", maxStudents: 30 },
                ]);
            }, 500);
        });
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
            
            // Group courses by package
            const packageMap = new Map<number, OptionalCourse[]>();
            courses.forEach((course: any) => {
                const packageId = course.packageId || 1;
                if (!packageMap.has(packageId)) {
                    packageMap.set(packageId, []);
                }
                packageMap.get(packageId)!.push(course);
            });

            return Array.from(packageMap.entries()).map(([id, courses]) => ({
                id,
                name: `Package ${id}`,
                courses
            }));
        } catch (error) {
            console.error('Failed to load courses from backend:', error);
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
            }
        ];
    }

    async saveStudentPreferences(studentId: number, packagePreferences: any[]): Promise<void> {
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
                throw new Error(`Failed to save preferences: ${response.status} ${errorText}`);
            }

            console.log('Preferences saved successfully');
        } catch (error) {
            console.error('Save preferences error:', error);
            throw error;
        }
    }
}