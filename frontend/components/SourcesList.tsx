'use client';

import React, { useState } from 'react';
import { SourceDto } from '@/types';
import { Globe, ExternalLink, Check, Copy, BookOpen } from 'lucide-react';
import { copyText } from '@/lib/utils';

interface SourcesListProps {
  sources: SourceDto[];
}

export const SourcesList: React.FC<SourcesListProps> = ({ sources }) => {
  const [copiedId, setCopiedId] = useState<string | null>(null);

  if (!sources || sources.length === 0) {
    return null;
  }

  const handleCopy = async (id: string, url: string) => {
    const success = await copyText(url);
    if (success) {
      setCopiedId(id);
      setTimeout(() => setCopiedId(null), 2000);
    }
  };

  return (
    <div id="sources-section" className="space-y-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <BookOpen className="w-5 h-5 text-brand-400" />
          <h3 className="text-lg font-bold text-slate-100">
            Research Sources & Citations
          </h3>
        </div>
        <span className="text-xs font-semibold px-2.5 py-1 rounded-full bg-slate-800 text-slate-300 border border-slate-700">
          {sources.length} Verified {sources.length === 1 ? 'Source' : 'Sources'}
        </span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {sources.map((source) => (
          <div
            key={source.id || source.url}
            id={`source-${source.citationNumber}`}
            className="glass-panel glass-panel-hover rounded-xl p-4 flex flex-col justify-between group transition-all duration-200"
          >
            <div>
              {/* Header: Citation # and Domain */}
              <div className="flex items-center justify-between gap-2 mb-2">
                <div className="flex items-center gap-2">
                  <span className="flex items-center justify-center w-6 h-6 rounded-lg bg-brand-600/20 text-brand-300 border border-brand-500/30 text-xs font-bold font-mono">
                    [{source.citationNumber}]
                  </span>
                  <div className="flex items-center gap-1.5 text-xs text-slate-400 font-medium truncate">
                    <Globe className="w-3.5 h-3.5 text-slate-500 shrink-0" />
                    <span className="truncate">{source.domain || 'web source'}</span>
                  </div>
                </div>

                {/* Copy Link Button */}
                <button
                  onClick={() => handleCopy(source.id, source.url)}
                  className="p-1 rounded-md text-slate-400 hover:text-slate-200 hover:bg-slate-800/80 transition-colors"
                  title="Copy source URL"
                >
                  {copiedId === source.id ? (
                    <Check className="w-3.5 h-3.5 text-emerald-400" />
                  ) : (
                    <Copy className="w-3.5 h-3.5" />
                  )}
                </button>
              </div>

              {/* Title with Link */}
              <a
                href={source.url}
                target="_blank"
                rel="noopener noreferrer"
                className="text-sm font-semibold text-slate-100 hover:text-brand-300 transition-colors line-clamp-2 inline-flex items-start gap-1 group/link"
              >
                <span>{source.title || source.url}</span>
                <ExternalLink className="w-3.5 h-3.5 text-slate-500 group-hover/link:text-brand-400 shrink-0 mt-0.5" />
              </a>

              {/* Snippet */}
              {source.snippet && (
                <p className="mt-2 text-xs text-slate-400 line-clamp-3 leading-relaxed border-l-2 border-slate-700/60 pl-2.5">
                  {source.snippet}
                </p>
              )}
            </div>

            {/* Bottom link preview */}
            <div className="mt-3 pt-2.5 border-t border-slate-800/60 flex items-center justify-between text-[11px] text-slate-500">
              <span className="truncate max-w-[220px] font-mono opacity-70">
                {source.url}
              </span>
              <a
                href={source.url}
                target="_blank"
                rel="noopener noreferrer"
                className="text-brand-400 hover:text-brand-300 font-medium hover:underline inline-flex items-center gap-1"
              >
                Visit Link
              </a>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
