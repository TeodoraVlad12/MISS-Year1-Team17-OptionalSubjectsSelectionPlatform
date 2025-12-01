export interface GradeUploadResult {
    totalRows: number;
    inserted: number;
    updated: number;
    skipped: number;
    summary: string;
}

export class GradeService {
    private baseUrl: string;

    constructor(baseUrl: string) {
        this.baseUrl = baseUrl;
    }

    /**
     * Upload CSV file with student grades
     * @param file - CSV file containing grades
     * @param overwrite - Whether to overwrite existing grades
     * @returns Upload result with statistics
     */
    async uploadGrades(file: File, overwrite: boolean = false): Promise<GradeUploadResult> {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('overwrite', overwrite.toString());

        const token = localStorage.getItem('authToken');
        if (!token) {
            throw new Error('No authentication token found');
        }

        try {
            const response = await fetch(`${this.baseUrl}/api/grades/upload`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Upload failed: ${response.status} - ${errorText}`);
            }

            return await response.json();
        } catch (error) {
            console.error('Grade upload error:', error);
            throw error;
        }
    }

    /**
     * Validate CSV file format before upload
     * @param file - CSV file to validate
     * @returns Validation result
     */
    validateCsvFile(file: File): { isValid: boolean; error?: string } {
        // Check file type
        if (!file.name.toLowerCase().endsWith('.csv')) {
            return {
                isValid: false,
                error: 'File must be a CSV file (.csv extension)'
            };
        }

        // Check file size (max 10MB)
        const maxSize = 10 * 1024 * 1024; // 10MB
        if (file.size > maxSize) {
            return {
                isValid: false,
                error: 'File size must be less than 10MB'
            };
        }

        // Check if file is empty
        if (file.size === 0) {
            return {
                isValid: false,
                error: 'File is empty'
            };
        }

        return { isValid: true };
    }
}
