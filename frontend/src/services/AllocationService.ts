import { ApiService } from "./ApiService";
import type { StudentAllocation } from "../models/StudentAllocation";

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
        year: number,
        specialization: string
    ): Promise<StudentAllocation[]> {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve(this.getMockedAllocations(year, specialization));
            }, 1000);
        });
    }

    private getMockedAllocations(
        year: number,
        specialization: string
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
}
