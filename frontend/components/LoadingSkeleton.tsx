'use client';

import React, { useEffect, useState } from 'react';
import { Search, Globe, Cpu, FileText, CheckCircle, Loader2 } from 'lucide-react';

const STEPS = [
  { icon: Search, label: 'Formulating research queries & strategy' },
  { icon: Globe, label: 'Executing live web searches across top sources' },
  { icon: FileText, label: 'Extracting full-text content & deduplicating evidence' },
  { icon: Cpu, label: 'Synthesizing comprehensive AI research report with citations' },
];

export const LoadingSkeleton: React.FC = () => {
  const [activeStep, setActiveStep] = useState(0);

  useEffect(() => {
    const timer1 = setTimeout(() => setActiveStep(1), 2500);
    const timer2 = setTimeout(() => setActiveStep(2), 6500);
    const timer3 = setTimeout(() => setActiveStep(3), 11500);

    return () => {
      clearTimeout(timer1);
      clearTimeout(timer2);
      clearTimeout(timer3);
    };
  }, []);

  return (
    <div className="w-full max-w-4xl mx-auto space-y-6">
      {/* Progress Pipeline Card */}
      <div className="glass-panel rounded-2xl p-6 border border-brand-500/20 bg-slate-900/60 shadow-xl shadow-brand-500/5">
        <div className="flex items-center gap-2 mb-4 text-brand-300 font-semibold text-sm">
          <Loader2 className="w-4 h-4 animate-spin text-brand-400" />
          <span>Autonomous Research Pipeline in Progress...</span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          {STEPS.map((step, idx) => {
            const Icon = step.icon;
            const isDone = idx < activeStep;
            const isCurrent = idx === activeStep;

            return (
              <div
                key={idx}
                className={`p-3.5 rounded-xl border transition-all duration-300 ${
                  isCurrent
                    ? 'bg-brand-600/10 border-brand-500/40 text-brand-200 ring-1 ring-brand-500/30 shadow-lg shadow-brand-500/10'
                    : isDone
                    ? 'bg-emerald-500/5 border-emerald-500/20 text-slate-300'
                    : 'bg-slate-900/40 border-slate-800 text-slate-500'
                }`}
              >
                <div className="flex items-center gap-2 mb-1.5">
                  {isDone ? (
                    <CheckCircle className="w-4 h-4 text-emerald-400" />
                  ) : isCurrent ? (
                    <Loader2 className="w-4 h-4 text-brand-400 animate-spin" />
                  ) : (
                    <Icon className="w-4 h-4 text-slate-500" />
                  )}
                  <span className="text-xs font-semibold uppercase tracking-wider">
                    Step {idx + 1}
                  </span>
                </div>
                <p className="text-xs font-medium line-clamp-2 leading-relaxed">
                  {step.label}
                </p>
              </div>
            );
          })}
        </div>
      </div>

      {/* Shimmer Placeholder Content */}
      <div className="glass-panel rounded-2xl p-6 sm:p-8 space-y-6 border border-slate-800 animate-pulse">
        {/* Header Skeleton */}
        <div className="space-y-3 pb-6 border-b border-slate-800">
          <div className="h-4 w-28 bg-slate-800 rounded-full" />
          <div className="h-8 w-3/4 bg-slate-700/60 rounded-lg" />
          <div className="h-4 w-1/3 bg-slate-800 rounded-md" />
        </div>

        {/* Executive Summary Skeleton */}
        <div className="space-y-3">
          <div className="h-5 w-48 bg-slate-800 rounded" />
          <div className="h-4 w-full bg-slate-800/70 rounded" />
          <div className="h-4 w-11/12 bg-slate-800/70 rounded" />
          <div className="h-4 w-4/5 bg-slate-800/70 rounded" />
        </div>

        {/* Key Findings Skeleton */}
        <div className="space-y-3">
          <div className="h-5 w-40 bg-slate-800 rounded" />
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div className="h-20 bg-slate-800/50 rounded-xl border border-slate-800" />
            <div className="h-20 bg-slate-800/50 rounded-xl border border-slate-800" />
            <div className="h-20 bg-slate-800/50 rounded-xl border border-slate-800" />
            <div className="h-20 bg-slate-800/50 rounded-xl border border-slate-800" />
          </div>
        </div>

        {/* Sources Skeleton */}
        <div className="space-y-3 pt-4 border-t border-slate-800">
          <div className="h-5 w-36 bg-slate-800 rounded" />
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div className="h-24 bg-slate-800/40 rounded-xl border border-slate-800" />
            <div className="h-24 bg-slate-800/40 rounded-xl border border-slate-800" />
          </div>
        </div>
      </div>
    </div>
  );
};
