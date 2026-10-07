'use client';

import React, { useEffect, useState } from 'react';
import { Sparkles, History, Search, BookOpen, Activity } from 'lucide-react';
import { api } from '@/lib/api';

interface HeaderProps {
  onOpenHistory: () => void;
  onNewResearch: () => void;
  historyCount: number;
}

export const Header: React.FC<HeaderProps> = ({
  onOpenHistory,
  onNewResearch,
  historyCount,
}) => {
  const [backendOnline, setBackendOnline] = useState<boolean | null>(null);

  useEffect(() => {
    let mounted = true;
    const check = async () => {
      const isUp = await api.checkHealth();
      if (mounted) setBackendOnline(isUp);
    };
    check();
    const interval = setInterval(check, 20000);
    return () => {
      mounted = false;
      clearInterval(interval);
    };
  }, []);

  return (
    <header className="sticky top-0 z-40 w-full border-b border-slate-800/80 bg-slate-950/75 backdrop-blur-xl transition-all">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Logo & Title */}
        <div
          onClick={onNewResearch}
          className="flex items-center gap-3 cursor-pointer group select-none"
        >
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-brand-600 via-indigo-600 to-accent-500 flex items-center justify-center shadow-lg shadow-brand-500/20 group-hover:scale-105 transition-transform duration-200">
            <Sparkles className="w-5 h-5 text-white animate-pulse-subtle" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-bold text-lg tracking-tight text-white group-hover:text-brand-300 transition-colors">
                AI Research Agent
              </span>
              <span className="text-[10px] uppercase font-semibold tracking-wider px-1.5 py-0.5 rounded bg-brand-500/10 text-brand-400 border border-brand-500/20">
                Autonomous
              </span>
            </div>
            <p className="text-xs text-slate-400 hidden sm:block">
              Web Intelligence & Synthesis Engine
            </p>
          </div>
        </div>

        {/* Right Actions */}
        <div className="flex items-center gap-3">
          {/* Backend Status Indicator */}
          <div
            className="hidden md:flex items-center gap-2 px-2.5 py-1 rounded-full bg-slate-900/80 border border-slate-800 text-xs text-slate-300"
            title={backendOnline ? 'Spring Boot REST API is connected' : 'Checking backend connectivity'}
          >
            <span
              className={`w-2 h-2 rounded-full ${
                backendOnline === null
                  ? 'bg-amber-400 animate-pulse'
                  : backendOnline
                  ? 'bg-emerald-400 shadow-[0_0_8px_rgba(52,211,153,0.8)]'
                  : 'bg-rose-400'
              }`}
            />
            <span>{backendOnline ? 'API Connected' : 'Connecting...'}</span>
          </div>

          {/* New Research Button */}
          <button
            onClick={onNewResearch}
            className="flex items-center gap-1.5 px-3.5 py-1.5 text-sm font-medium rounded-lg bg-slate-800/80 hover:bg-slate-700/80 text-slate-200 hover:text-white border border-slate-700/60 transition-all duration-150"
          >
            <Search className="w-4 h-4 text-brand-400" />
            <span className="hidden sm:inline">New Research</span>
          </button>

          {/* History Toggle Button */}
          <button
            onClick={onOpenHistory}
            className="relative flex items-center gap-1.5 px-3.5 py-1.5 text-sm font-medium rounded-lg bg-brand-600/10 hover:bg-brand-600/20 text-brand-300 hover:text-brand-200 border border-brand-500/30 transition-all duration-150"
          >
            <History className="w-4 h-4 text-brand-400" />
            <span>History</span>
            {historyCount > 0 && (
              <span className="ml-1 px-1.5 py-0.2 rounded-full bg-brand-500 text-[11px] font-bold text-white min-w-[18px] text-center">
                {historyCount}
              </span>
            )}
          </button>
        </div>
      </div>
    </header>
  );
};
