'use client';

import React, { useState, useEffect } from 'react';
import { Header } from '@/components/Header';
import { Hero } from '@/components/Hero';
import { ResearchInput } from '@/components/ResearchInput';
import { ResearchReport } from '@/components/ResearchReport';
import { LoadingSkeleton } from '@/components/LoadingSkeleton';
import { HistorySidebar } from '@/components/HistorySidebar';
import { api } from '@/lib/api';
import { ResearchResponse, ResearchSummaryResponse, ApiError } from '@/types';
import { AlertCircle, RefreshCw, Sparkles } from 'lucide-react';

export default function Home() {
  const [query, setQuery] = useState<string>('');
  const [activeResearch, setActiveResearch] = useState<ResearchResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<ApiError | null>(null);
  const [history, setHistory] = useState<ResearchSummaryResponse[]>([]);
  const [isHistoryOpen, setIsHistoryOpen] = useState<boolean>(false);
  const [isHistoryLoading, setIsHistoryLoading] = useState<boolean>(false);

  // Load history on mount
  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    setIsHistoryLoading(true);
    try {
      const data = await api.getAllResearches();
      setHistory(data);
    } catch (err) {
      console.warn('Could not load history:', err);
    } finally {
      setIsHistoryLoading(false);
    }
  };

  const handleStartResearch = async (researchQuery: string) => {
    if (!researchQuery.trim() || isLoading) return;

    setIsLoading(true);
    setError(null);
    setActiveResearch(null);

    try {
      const result = await api.conductResearch(researchQuery);
      setActiveResearch(result);
      // Refresh history in background
      fetchHistory();
    } catch (err: any) {
      console.error('Research error:', err);
      setError({
        timestamp: err.timestamp || new Date().toISOString(),
        status: err.status || 500,
        error: err.error || 'Research Failed',
        message: err.message || 'An unexpected error occurred during research execution.',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleSelectHistoryItem = async (id: string) => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await api.getResearchById(id);
      setActiveResearch(data);
    } catch (err: any) {
      console.error('Failed to load research item:', err);
      setError({
        timestamp: new Date().toISOString(),
        status: err.status || 404,
        error: 'Load Error',
        message: err.message || 'Could not load the requested research report.',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeleteHistoryItem = async (id: string, e: React.MouseEvent) => {
    e.stopPropagation();
    try {
      await api.deleteResearch(id);
      setHistory((prev) => prev.filter((item) => item.id !== id));
      if (activeResearch?.id === id) {
        setActiveResearch(null);
      }
    } catch (err: any) {
      console.error('Delete error:', err);
      alert('Could not delete research task: ' + (err.message || 'Server error'));
    }
  };

  const handleNewResearch = () => {
    setActiveResearch(null);
    setError(null);
    setQuery('');
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="min-h-screen flex flex-col justify-between">
      {/* Top Navbar */}
      <Header
        onOpenHistory={() => setIsHistoryOpen(true)}
        onNewResearch={handleNewResearch}
        historyCount={history.length}
      />

      {/* Main Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8 sm:py-12 space-y-10">
        {/* Hero Banner (Always shown or styled depending on result state) */}
        {!activeResearch && !isLoading && <Hero />}

        {/* Research Input Bar */}
        <section className="transition-all duration-300">
          <ResearchInput
            onSubmit={handleStartResearch}
            isLoading={isLoading}
            initialQuery={query}
          />
        </section>

        {/* Error Alert Box */}
        {error && (
          <div className="max-w-3xl mx-auto p-5 rounded-2xl bg-rose-500/10 border border-rose-500/30 text-rose-200 shadow-xl space-y-3 animate-shake">
            <div className="flex items-start gap-3">
              <div className="p-1 rounded-lg bg-rose-500/20 text-rose-400 mt-0.5">
                <AlertCircle className="w-5 h-5" />
              </div>
              <div className="flex-1">
                <h3 className="text-sm font-bold text-rose-100">{error.error}</h3>
                <p className="text-xs sm:text-sm text-rose-300/90 mt-1 leading-relaxed">
                  {error.message}
                </p>
                {error.validationErrors && error.validationErrors.length > 0 && (
                  <ul className="mt-2 list-disc list-inside text-xs text-rose-300/80 space-y-0.5">
                    {error.validationErrors.map((ve, idx) => (
                      <li key={idx}>{ve}</li>
                    ))}
                  </ul>
                )}
              </div>
            </div>

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-rose-500/20">
              <button
                onClick={() => setError(null)}
                className="px-3 py-1.5 text-xs rounded-lg text-slate-300 hover:text-white hover:bg-slate-800 transition-colors"
              >
                Dismiss
              </button>
              <button
                onClick={() => {
                  setError(null);
                  if (activeResearch?.query) {
                    handleStartResearch(activeResearch.query);
                  }
                }}
                className="flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-medium rounded-lg bg-rose-600 hover:bg-rose-500 text-white shadow transition-all"
              >
                <RefreshCw className="w-3.5 h-3.5" />
                <span>Retry</span>
              </button>
            </div>
          </div>
        )}

        {/* Loading Skeleton */}
        {isLoading && <LoadingSkeleton />}

        {/* Completed Research Report */}
        {!isLoading && activeResearch && (
          <ResearchReport
            research={activeResearch}
            onDelete={(id) => {
              setHistory((prev) => prev.filter((item) => item.id !== id));
              setActiveResearch(null);
            }}
          />
        )}
      </main>

      {/* History Flyout Sidebar */}
      <HistorySidebar
        isOpen={isHistoryOpen}
        onClose={() => setIsHistoryOpen(false)}
        history={history}
        activeId={activeResearch?.id}
        onSelect={handleSelectHistoryItem}
        onDelete={handleDeleteHistoryItem}
        isLoading={isHistoryLoading}
      />

      {/* Footer */}
      <footer className="border-t border-slate-800/80 py-6 mt-16 text-center text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row items-center justify-between gap-3">
          <div className="flex items-center gap-2">
            <span className="font-semibold text-slate-400">AI Research Agent</span>
            <span>—</span>
            <span>Production Autonomous Research Engine</span>
          </div>
          <div className="text-slate-500">
            Powered by Google Gemini / OpenAI & Spring Boot REST API
          </div>
        </div>
      </footer>
    </div>
  );
}
