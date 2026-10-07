'use client';

import React, { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { Header } from '@/components/Header';
import { ResearchReport } from '@/components/ResearchReport';
import { LoadingSkeleton } from '@/components/LoadingSkeleton';
import { HistorySidebar } from '@/components/HistorySidebar';
import { api } from '@/lib/api';
import { ResearchResponse, ResearchSummaryResponse, ApiError } from '@/types';
import { ArrowLeft, AlertCircle } from 'lucide-react';
import Link from 'next/link';

export default function ResearchDetailPage() {
  const params = useParams();
  const router = useRouter();
  const id = params?.id as string;

  const [research, setResearch] = useState<ResearchResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<ApiError | null>(null);
  const [history, setHistory] = useState<ResearchSummaryResponse[]>([]);
  const [isHistoryOpen, setIsHistoryOpen] = useState<boolean>(false);

  useEffect(() => {
    if (id) {
      loadResearch(id);
    }
    loadHistory();
  }, [id]);

  const loadResearch = async (researchId: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await api.getResearchById(researchId);
      setResearch(data);
    } catch (err: any) {
      setError({
        timestamp: new Date().toISOString(),
        status: err.status || 404,
        error: 'Report Not Found',
        message: err.message || 'Could not load research details for ID: ' + researchId,
      });
    } finally {
      setIsLoading(false);
    }
  };

  const loadHistory = async () => {
    try {
      const data = await api.getAllResearches();
      setHistory(data);
    } catch {
      // ignore
    }
  };

  return (
    <div className="min-h-screen flex flex-col justify-between">
      <Header
        onOpenHistory={() => setIsHistoryOpen(true)}
        onNewResearch={() => router.push('/')}
        historyCount={history.length}
      />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        {/* Back Link */}
        <div>
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 text-xs sm:text-sm font-medium text-slate-400 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>Back to Research Dashboard</span>
          </Link>
        </div>

        {/* Loading State */}
        {isLoading && <LoadingSkeleton />}

        {/* Error State */}
        {error && (
          <div className="max-w-2xl mx-auto p-6 rounded-2xl bg-rose-500/10 border border-rose-500/30 text-rose-200 text-center space-y-4">
            <AlertCircle className="w-10 h-10 text-rose-400 mx-auto" />
            <div>
              <h2 className="text-base font-bold text-rose-100">{error.error}</h2>
              <p className="text-xs text-rose-300/80 mt-1">{error.message}</p>
            </div>
            <Link
              href="/"
              className="inline-block px-4 py-2 text-xs font-semibold rounded-lg bg-slate-800 hover:bg-slate-700 text-white border border-slate-700 transition-all"
            >
              Start New Research
            </Link>
          </div>
        )}

        {/* Completed Report */}
        {!isLoading && research && (
          <ResearchReport
            research={research}
            onDelete={() => {
              router.push('/');
            }}
          />
        )}
      </main>

      <HistorySidebar
        isOpen={isHistoryOpen}
        onClose={() => setIsHistoryOpen(false)}
        history={history}
        activeId={research?.id}
        onSelect={(selectedId) => router.push(`/research/${selectedId}`)}
        onDelete={async (delId, e) => {
          e.stopPropagation();
          await api.deleteResearch(delId);
          setHistory((prev) => prev.filter((i) => i.id !== delId));
          if (delId === id) router.push('/');
        }}
        isLoading={false}
      />
    </div>
  );
}
