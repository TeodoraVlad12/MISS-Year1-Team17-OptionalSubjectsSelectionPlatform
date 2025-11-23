import type { ReactNode } from "react";

export interface Route {
    path: string;
    element: ReactNode;
    errorElement?: ReactNode;
}
