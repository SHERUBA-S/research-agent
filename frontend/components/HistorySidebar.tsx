'use client';

import React from 'react';
import { ResearchSummaryResponse } from '@/types';
import { StatusBadge } from './StatusBadge';
import { formatRelativeTime } from '@/lib/utils';
import { X, History, Trash2, Search, ArrowRight, ExternalLink } from 'lucide-react';

interface HistorySidebarProps {
  isOpen: boolean;
  onClose: () => void;
  history: ResearchSummaryResponse[];
  activeId?: string | null;
  onSelect: (id: string) => void;
  onDelete: (id: string, e: React.MouseEvent) => void;
  isLoading: boolean;
}

export const HistorySidebar: React.FC<HistorySidebarProps> = ({
  isOpen,
  onClose,
  history,
  activeId,
  onSelect,
  onDelete,
  isLoading,
}) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex justify-end">
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-slate-950/70 backdrop-blur-sm transition-opacity"
        onClick={onClose}
      />

      {/* Drawer Panel */}
      <div className="relative w-full max-w-md bg-slate-900 border-l border-slate-800 h-full shadow-2xl flex flex-col z-10 animate-in slide-in-from-right duration-300">
        {/* Header */}
        <div className="p-4 sm:p-5 border-b border-slate-800 flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="p-2 rounded-lg bg-brand-500/10 text-brand-400 border border-brand-500/20">
              <History className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-base font-bold text-white">Research History</h2>
              <p className="text-xs text-slate-400">
                {history.length} past research {history.length === 1 ? 'task' : 'tasks'}
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* List Content */}
        <div className="flex-1 overflow-y-auto p-4 space-y-3">
          {isLoading && (
            <div className="p-6 text-center text-xs text-slate-400">
              Loading history...
            </div>
          )}

          {!isLoading && history.length === 0 && (
            <div className="p-8 text-center space-y-3">
              <div className="w-12 h-12 mx-auto rounded-full bg-slate-800/80 flex items-center justify-center text-slate-500">
                <Search className="w-6 h-6" />
              </div>
              <p className="text-sm font-medium text-slate-300">No research history yet</p>
              <p className="text-xs text-slate-500 max-w-xs mx-auto">
                Your completed and pending research reports will be automatically saved here.
              </p>
            </div>
          )}

          {!isLoading &&
            history.map((item) => {
              const isActive = activeId === item.id;

              return (
                <div
                  key={item.id}
                  onClick={() => {
                    onSelect(item.id);
                    onClose();
                  }}
                  className={`group relative p-3.5 rounded-xl border transition-all duration-150 cursor-pointer ${
                    isActive
                      ? 'bg-brand-600/15 border-brand-500/40 shadow-sm'
                      : 'bg-slate-800/50 hover:bg-slate-800/90 border-slate-800 hover:border-slate-700'
                  }`}
                >
                  <div className="flex items-start justify-between gap-2 mb-1.5">
                    <StatusBadge status={item.status} size="sm" />
                    <span className="text-[11px] text-slate-500">
                      {formatRelativeTime(item.createdAt)}
                    </span>
                  </div>

                  <h3 className="text-sm font-semibold text-slate-100 group-hover:text-brand-300 transition-colors line-clamp-2">
                    {item.title || item.query}
                  </h3>

                  <p className="text-xs text-slate-400 italic line-clamp-1 mt-1">
                    "{item.query}"
                  </p>

                  <div className="mt-2.5 pt-2 border-t border-slate-800/60 flex items-center justify-between text-[11px] text-slate-400">
                    <span>
                      {item.sourcesCount} {item.sourcesCount === 1 ? 'source' : 'sources'}
                    </span>

                    <button
                      onClick={(e) => onDelete(item.id, e)}
                      className="opacity-0 group-hover:opacity-100 p-1 rounded text-slate-500 hover:text-rose-400 hover:bg-rose-500/10 transition-all"
                      title="Delete research"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              );
            })}
        </div>
      </div>
    </div>
  );
};
