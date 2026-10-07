import React from 'react';
import { Compass, Sparkles, Layers, ShieldCheck } from 'lucide-react';

export const Hero: React.FC = () => {
  return (
    <div className="relative pt-6 pb-4 sm:pt-10 sm:pb-8 text-center max-w-4xl mx-auto px-4">
      {/* Pill Badge */}
      <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-gradient-to-r from-brand-500/10 via-accent-500/10 to-indigo-500/10 border border-brand-500/30 text-xs sm:text-sm font-medium text-brand-300 mb-6 shadow-sm">
        <Sparkles className="w-4 h-4 text-brand-400" />
        <span>Deep Web Search & Grounded AI Synthesis</span>
      </div>

      {/* Main Headline */}
      <h1 className="text-4xl sm:text-5xl md:text-6xl font-extrabold tracking-tight leading-[1.15] text-white">
        Research anything. <br className="hidden sm:inline" />
        <span className="text-gradient">Understand everything.</span>
      </h1>

      {/* Supporting Subtitle */}
      <p className="mt-4 text-base sm:text-lg text-slate-400 max-w-2xl mx-auto leading-relaxed">
        Enter any complex question or domain. Our autonomous pipeline explores the live web, extracts authentic source evidence, and synthesizes structured, verifiable intelligence reports with exact citations.
      </p>

      {/* Feature Highlights */}
      <div className="mt-6 flex flex-wrap items-center justify-center gap-4 text-xs text-slate-400">
        <div className="flex items-center gap-1.5 bg-slate-900/60 px-3 py-1 rounded-full border border-slate-800">
          <Compass className="w-3.5 h-3.5 text-brand-400" />
          <span>Real-time Multi-Source Crawling</span>
        </div>
        <div className="flex items-center gap-1.5 bg-slate-900/60 px-3 py-1 rounded-full border border-slate-800">
          <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
          <span>Grounded Citations</span>
        </div>
        <div className="flex items-center gap-1.5 bg-slate-900/60 px-3 py-1 rounded-full border border-slate-800">
          <Layers className="w-3.5 h-3.5 text-accent-400" />
          <span>Structured Executive Summaries</span>
        </div>
      </div>
    </div>
  );
};
