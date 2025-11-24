import type { Student } from "./Student";
import type { OptionalCourse } from "./OptionalCourse";

export interface StudentAllocation {
    student: Student;
    assignedCourses: OptionalCourse[];
}
