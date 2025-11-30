import type { Student } from "./Student";
import type { OptionalCourse } from "./OptionalCourse";

export interface StudentAllocation {
    studentId: number;
    studentName: string;
    allocatedCourseId: number | null;
    allocatedCourseName: string | null;
    preferenceRank: number | null;
}


/*
public class StudentAllocationDTO {
    private Long studentId;
    private String studentName;
    private Long allocatedCourseId;
    private String allocatedCourseName;
    private Integer preferenceRank;
}

*/