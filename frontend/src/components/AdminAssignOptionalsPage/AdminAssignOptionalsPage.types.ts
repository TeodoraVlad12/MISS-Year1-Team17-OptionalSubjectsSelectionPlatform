import type { StudentAllocation } from "../../models/StudentAllocation";

export interface AdminAssignOptionalsPageProps {}

export interface AdminAssignOptionalsPageState {
    selectedYear: number;
    selectedSpecialization: string;
    allocations: StudentAllocation[];
    loading: boolean;
    error: string | null;
}
