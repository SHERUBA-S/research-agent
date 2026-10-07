'use client';

import React, { useState, useRef, useEffect } from 'react';
import { Search, Loader2, ArrowRight, CornerDownLeft, Sparkles, X } from 'lucide-react';

interface ResearchInputProps {
  onSubmit: (query: string) => void;
  isLoading: boolean;
  initialQuery?: string;
}

const QUICK_PROMPTS = [
  'Applications of AI in healthcare',
  'Latest trends in artificial intelligence',
  'How does quantum computing work?',
  'Impact of cloud computing on software development',
];

export const ResearchInput: React.FC<ResearchInputProps> = ({
  onSubmit,
  isLoading,
  initialQuery = '',
}) => {
  const [query, setQuery] = useState(initialQuery);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    if (initialQuery) {
      setQuery(initialQuery);
    }
  }, [initialQuery]);

  const handleSubmit = (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    const trimmed = query.trim();
    if (!trimmed || isLoading) return;
    onSubmit(trimmed);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === 'Enter' && (e.metaKey || e.ctrlKey)) {
      e.preventDefault();
      handleSubmit();
    }
  };

  const handleSelectPrompt = (prompt: string) => {
    setQuery(prompt);
    if (textareaRef.current) {
      textareaRef.current.focus();
    }
  };

  const handleClear = () => {
    setQuery('');
    if (textareaRef.current) {
      textareaRef.current.focus();
    }
  };

  return (
    <div className="w-full max-w-3xl mx-auto">
      <form onSubmit={handleSubmit} className="relative">
        <div className="relative glass-panel rounded-2xl p-2.5 sm:p-3.5 transition-all duration-300 focus-within:border-brand-500/50 focus-within:ring-2 focus-within:ring-brand-500/20 focus-within:shadow-[0_0_30px_rgba(59,130,246,0.15)]">
          {/* Textarea */}
          <div className="relative flex items-start">
            <div className="pt-2.5 pl-2 sm:pl-3 text-brand-400">
              <Search className="w-5 h-5 opacity-70" />
            </div>
            <textarea
              ref={textareaRef}
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={handleKeyDown}
              disabled={isLoading}
              rows={3}
              placeholder="What would you like to research? (e.g. Next-gen cancer immunotherapy, State of fusion energy...)"
              className="w-full bg-transparent pl-3 pr-10 pt-2 pb-2 text-slate-100 placeholder:text-slate-500 text-sm sm:text-base focus:outline-none resize-none disabled:opacity-50"
            />
            {query.length > 0 && !isLoading && (
              <button
                type="button"
                onClick={handleClear}
                className="absolute right-2 top-2 p-1.5 rounded-lg text-slate-400 hover:text-slate-200 hover:bg-slate-800 transition-colors"
                title="Clear input"
              >
                <X className="w-4 h-4" />
              </button>
            )}
          </div>

          {/* Bottom Bar: Action Button & Keyboard Hint */}
          <div className="mt-2 pt-2 border-t border-slate-800/80 flex items-center justify-between gap-3 px-2">
            <div className="flex items-center gap-2 text-xs text-slate-400">
              <span className="hidden sm:inline">Press</span>
              <kbd className="hidden sm:inline-flex items-center gap-0.5 px-1.5 py-0.5 rounded bg-slate-800 text-[10px] text-slate-300 border border-slate-700 font-mono">
                ⌘ / Ctrl + Enter
              </kbd>
              <span className="hidden sm:inline">to start research</span>
            </div>

            <button
              type="submit"
              disabled={!query.trim() || isLoading}
              className="relative inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-gradient-to-r from-brand-600 via-indigo-600 to-accent-600 hover:from-brand-500 hover:to-accent-500 text-white font-medium text-sm shadow-lg shadow-brand-600/25 hover:shadow-brand-600/40 transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:shadow-none select-none"
            >
              {isLoading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  <span>Researching...</span>
                </>
              ) : (
                <>
                  <span>Start Research</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </div>
        </div>
      </form>

      {/* Quick Prompts */}
      <div className="mt-4">
        <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-400 mb-2.5">
          <Sparkles className="w-3.5 h-3.5 text-brand-400" />
          <span>Quick Research Prompts:</span>
        </div>
        <div className="flex flex-wrap gap-2">
          {QUICK_PROMPTS.map((prompt) => (
            <button
              key={prompt}
              type="button"
              onClick={() => handleSelectPrompt(prompt)}
              disabled={isLoading}
              className="text-xs px-3 py-1.5 rounded-lg bg-slate-900/80 hover:bg-slate-800 text-slate-300 hover:text-white border border-slate-800 hover:border-slate-700 transition-all text-left disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {prompt}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};
