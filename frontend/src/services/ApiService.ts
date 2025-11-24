export class ApiService {
    protected baseUrl: string;
    protected methodUrl: string;

    constructor(baseUrl: string, methodUrl: string) {
        this.baseUrl = baseUrl;
        this.methodUrl = methodUrl;
    }

    protected getFullUrl(): string {
        return `${this.baseUrl}${this.methodUrl}`;
    }

    protected async get<T>(params?: Record<string, string>): Promise<T> {
        const url = new URL(this.getFullUrl());
        if (params) {
            Object.entries(params).forEach(([key, value]) => {
                url.searchParams.append(key, value);
            });
        }

        const response = await fetch(url.toString(), {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return response.json();
    }

    protected async post<T, R>(data: T): Promise<R> {
        const response = await fetch(this.getFullUrl(), {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return response.json();
    }

    protected async delete<T>(): Promise<T> {
        const response = await fetch(this.getFullUrl(), {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return response.json();
    }
}
