export type ResearchStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';

export interface SourceDto {
  id: string;
  title: string;
  url: string;
  domain: string;
  snippet: string;
  citationNumber: number;
}

export interface ResearchResponse {
  id: string;
  query: string;
  status: ResearchStatus;
  title: string;
  summary: string;
  keyFindings: string[];
  analysis: string;
  importantInsights: string[];
  sources: SourceDto[];
  errorMessage?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ResearchSummaryResponse {
  id: string;
  query: string;
  title: string;
  status: ResearchStatus;
  sourcesCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface ResearchRequest {
  query: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path?: string;
  validationErrors?: string[];
}
