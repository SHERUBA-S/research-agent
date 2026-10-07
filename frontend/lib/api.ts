import { ResearchRequest, ResearchResponse, ResearchSummaryResponse, ApiError } from '@/types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

class ApiClient {
  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const url = `${API_BASE_URL}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    try {
      const response = await fetch(url, {
        ...options,
        headers,
      });

      if (!response.ok) {
        let errorData: ApiError;
        try {
          errorData = await response.json();
        } catch {
          errorData = {
            timestamp: new Date().toISOString(),
            status: response.status,
            error: response.statusText || 'Request Failed',
            message: `Server returned status ${response.status} (${response.statusText})`,
          };
        }
        throw errorData;
      }

      return (await response.json()) as T;
    } catch (err: any) {
      if (err.message && err.status) {
        throw err;
      }
      throw {
        timestamp: new Date().toISOString(),
        status: 500,
        error: 'Network / Connection Error',
        message: err.message || 'Unable to connect to backend server. Ensure backend is running on port 8080.',
      } as ApiError;
    }
  }

  async conductResearch(query: string): Promise<ResearchResponse> {
    const body: ResearchRequest = { query };
    return this.request<ResearchResponse>('/research', {
      method: 'POST',
      body: JSON.stringify(body),
    });
  }

  async getResearchById(id: string): Promise<ResearchResponse> {
    return this.request<ResearchResponse>(`/research/${id}`, {
      method: 'GET',
    });
  }

  async getAllResearches(): Promise<ResearchSummaryResponse[]> {
    return this.request<ResearchSummaryResponse[]>('/research', {
      method: 'GET',
    });
  }

  async deleteResearch(id: string): Promise<void> {
    await this.request<{ success: boolean; message: string }>(`/research/${id}`, {
      method: 'DELETE',
    });
  }

  async checkHealth(): Promise<boolean> {
    try {
      const res = await this.request<{ status: string }>('/health', {
        method: 'GET',
      });
      return res.status === 'UP';
    } catch {
      return false;
    }
  }
}

export const api = new ApiClient();
