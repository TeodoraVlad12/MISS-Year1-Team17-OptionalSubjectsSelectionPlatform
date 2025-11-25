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

    async assignOptionals(
        // year: number,
        // specialization: string
    ): Promise<StudentAllocation[]> {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve(this.getMockedAllocations(/*year, specialization*/));
            }, 1000);
        });
    }

    private getMockedAllocations(
        // year: number,
        // specialization: string
    ): StudentAllocation[] {
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
            {
                student: {
                    id: 3,
                    firstName: "Andrei",
                    lastName: "Vasilescu",
                    email: "andrei.vasilescu@student.uaic.ro",
                    studentNumber: "A12347",
                },
                assignedCourses: [
                    {
                        id: 101,
                        name: "Advanced Algorithms",
                        code: "CS301",
                        maxStudents: 30,
                    },
                    {
                        id: 105,
                        name: "Software Engineering",
                        code: "CS305",
                        maxStudents: 30,
                    },
                ],
            },
        ];
    }

    async getOptionalCourses(/*academicYear: number, specialization: string*/): Promise<OptionalCourse[]> {
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

    async saveStudentPreferences(preferences: CoursePreference[]): Promise<void> {
        return new Promise((resolve) => {
            setTimeout(() => {
                console.log('Saving preferences:', preferences);
                resolve();
            }, 800);
        });
    }

    async getCoursePackages(academicYear: number, specialization: string): Promise<CoursePackage[]> {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve([
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
                    },
                    // {
                    //     id: 4,
                    //     name: "Package 4",
                    //     courses: [
                    //         { id: 401, name: "Frontend Frameworks", code: "WEB301", maxStudents: 35 },
                    //         { id: 402, name: "Backend Systems", code: "WEB302", maxStudents: 30 },
                    //         { id: 403, name: "Mobile Development", code: "WEB303", maxStudents: 25 }
                    //     ]
                    // },
                    // {
                    //     id: 5,
                    //     name: "Package 5",
                    //     courses: [
                    //         { id: 501, name: "Advanced SQL", code: "DB301", maxStudents: 28 },
                    //         { id: 502, name: "NoSQL Databases", code: "DB302", maxStudents: 24 },
                    //         { id: 503, name: "Data Warehousing", code: "DB303", maxStudents: 22 }
                    //     ]
                    // },
                    // {
                    //     id: 6,
                    //     name: "Package 6",
                    //     courses: [
                    //         { id: 601, name: "Cybersecurity", code: "SEC301", maxStudents: 26 },
                    //         { id: 602, name: "Network Protocols", code: "NET301", maxStudents: 24 },
                    //         { id: 603, name: "Cryptography", code: "SEC302", maxStudents: 20 }
                    //     ]
                    // }
                ]);
            }, 600);
        });
    }
}
