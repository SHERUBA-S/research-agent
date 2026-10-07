'use client';

import React, { useState } from 'react';
import { ResearchResponse } from '@/types';
import { StatusBadge } from './StatusBadge';
import { SourcesList } from './SourcesList';
import { formatDate, copyText } from '@/lib/utils';
import {
  FileText,
  Lightbulb,
  CheckCircle2,
  Share2,
  Download,
  Copy,
  Check,
  Calendar,
  Sparkles,
  Layers,
  ChevronRight,
  TrendingUp,
  BookmarkCheck,
} from 'lucide-react';

interface ResearchReportProps {
  research: ResearchResponse;
  onDelete?: (id: string) => void;
}

export const ResearchReport: React.FC<ResearchReportProps> = ({ research, onDelete }) => {
  const [copied, setCopied] = useState(false);

  // Helper to scroll to a specific source when citation number is clicked
  const handleCitationClick = (citationNum: number) => {
    const el = document.getElementById(`source-${citationNum}`);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'center' });
      el.classList.add('ring-2', 'ring-brand-400');
      setTimeout(() => el.classList.remove('ring-2', 'ring-brand-400'), 2500);
    }
  };

  // Convert report to formatted Markdown text
  const generateMarkdown = (): string => {
    let md = `# ${research.title || research.query}\n\n`;
    md += `**Research Query:** ${research.query}\n`;
    md += `**Status:** ${research.status} | **Date:** ${formatDate(research.createdAt)}\n\n`;

    if (research.summary) {
      md += `## Executive Summary\n\n${research.summary}\n\n`;
    }

    if (research.keyFindings && research.keyFindings.length > 0) {
      md += `## Key Findings\n\n`;
      research.keyFindings.forEach((f) => {
        md += `- ${f}\n`;
      });
      md += `\n`;
    }

    if (research.analysis) {
      md += `## Detailed Analysis\n\n${research.analysis}\n\n`;
    }

    if (research.importantInsights && research.importantInsights.length > 0) {
      md += `## Important Insights\n\n`;
      research.importantInsights.forEach((i) => {
        md += `- ${i}\n`;
      });
      md += `\n`;
    }

    if (research.sources && research.sources.length > 0) {
      md += `## Sources & Citations\n\n`;
      research.sources.forEach((s) => {
        md += `[${s.citationNumber}] [${s.title}](${s.url}) - *${s.domain}*\n`;
        if (s.snippet) md += `   > ${s.snippet}\n\n`;
      });
    }

    return md;
  };

  const handleCopyReport = async () => {
    const md = generateMarkdown();
    const success = await copyText(md);
    if (success) {
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  const handleDownloadMarkdown = () => {
    const md = generateMarkdown();
    const blob = new Blob([md], { type: 'text/markdown;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `research-${research.id.substring(0, 8)}.md`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  // Helper to render text with interactive clickable citation badges [1], [2]
  const renderTextWithCitations = (text: string) => {
    if (!text) return null;
    const parts = text.split(/(\[\d+\])/g);

    return parts.map((part, idx) => {
      const match = part.match(/\[(\d+)\]/);
      if (match) {
        const citationNum = parseInt(match[1], 10);
        return (
          <button
            key={idx}
            type="button"
            onClick={() => handleCitationClick(citationNum)}
            className="inline-flex items-center mx-0.5 px-1.5 py-0.2 rounded bg-brand-500/20 text-brand-300 hover:bg-brand-500 hover:text-white border border-brand-500/30 text-xs font-mono font-bold transition-all cursor-pointer"
            title={`View Source #${citationNum}`}
          >
            [{citationNum}]
          </button>
        );
      }
      return <span key={idx}>{part}</span>;
    });
  };

  return (
    <article className="w-full max-w-4xl mx-auto space-y-8 animate-fadeIn">
      {/* Report Header Card */}
      <div className="glass-panel rounded-2xl p-6 sm:p-8 border border-slate-800 shadow-2xl relative overflow-hidden">
        {/* Subtle background glow */}
        <div className="absolute top-0 right-0 w-96 h-96 bg-brand-600/10 rounded-full blur-3xl -z-10 pointer-events-none" />

        <div className="flex flex-col md:flex-row md:items-start justify-between gap-4">
          <div className="space-y-2 flex-1">
            <div className="flex flex-wrap items-center gap-2 text-xs">
              <StatusBadge status={research.status} size="sm" />
              <span className="text-slate-500">•</span>
              <div className="flex items-center gap-1 text-slate-400">
                <Calendar className="w-3.5 h-3.5" />
                <span>{formatDate(research.createdAt)}</span>
              </div>
              {research.sources && (
                <>
                  <span className="text-slate-500">•</span>
                  <span className="text-slate-400">
                    {research.sources.length} sources analyzed
                  </span>
                </>
              )}
            </div>

            <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight leading-snug">
              {research.title || research.query}
            </h1>

            <div className="flex items-start gap-2 pt-1 text-xs sm:text-sm text-slate-400 font-medium">
              <span className="text-brand-400 font-semibold uppercase tracking-wider text-[11px] shrink-0">
                Query:
              </span>
              <span className="italic text-slate-300">"{research.query}"</span>
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex items-center gap-2 self-start pt-1">
            <button
              onClick={handleCopyReport}
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium rounded-lg bg-slate-800/80 hover:bg-slate-700/80 text-slate-200 border border-slate-700 transition-all"
              title="Copy markdown report"
            >
              {copied ? (
                <>
                  <Check className="w-3.5 h-3.5 text-emerald-400" />
                  <span className="text-emerald-400">Copied</span>
                </>
              ) : (
                <>
                  <Copy className="w-3.5 h-3.5" />
                  <span>Copy Report</span>
                </>
              )}
            </button>

            <button
              onClick={handleDownloadMarkdown}
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium rounded-lg bg-slate-800/80 hover:bg-slate-700/80 text-slate-200 border border-slate-700 transition-all"
              title="Download as Markdown"
            >
              <Download className="w-3.5 h-3.5 text-brand-400" />
              <span>Export .md</span>
            </button>
          </div>
        </div>
      </div>

      {/* Error state alert if research failed */}
      {research.status === 'FAILED' && research.errorMessage && (
        <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-300 text-sm">
          <p className="font-semibold mb-1">Research task encountered an error:</p>
          <p className="text-xs text-rose-400/90 font-mono">{research.errorMessage}</p>
        </div>
      )}

      {/* 1. Executive Summary */}
      {research.summary && (
        <section className="glass-panel rounded-2xl p-6 sm:p-8 border border-slate-800 shadow-lg">
          <div className="flex items-center gap-2.5 pb-4 mb-4 border-b border-slate-800/80">
            <div className="w-8 h-8 rounded-lg bg-brand-500/10 flex items-center justify-center text-brand-400 border border-brand-500/20">
              <FileText className="w-4 h-4" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-slate-100">
                Executive Summary
              </h2>
              <p className="text-xs text-slate-400">
                High-level strategic briefing and core takeaway
              </p>
            </div>
          </div>

          <div className="prose prose-invert max-w-none text-slate-300 leading-relaxed text-sm sm:text-base space-y-4">
            {research.summary.split('\n\n').map((paragraph, i) => (
              <p key={i} className="leading-relaxed">
                {renderTextWithCitations(paragraph)}
              </p>
            ))}
          </div>
        </section>
      )}

      {/* 2. Key Findings */}
      {research.keyFindings && research.keyFindings.length > 0 && (
        <section className="glass-panel rounded-2xl p-6 sm:p-8 border border-slate-800 shadow-lg">
          <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-800/80">
            <div className="w-8 h-8 rounded-lg bg-emerald-500/10 flex items-center justify-center text-emerald-400 border border-emerald-500/20">
              <CheckCircle2 className="w-4 h-4" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-slate-100">
                Key Findings
              </h2>
              <p className="text-xs text-slate-400">
                Direct factual observations synthesized from analyzed literature
              </p>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-3.5">
            {research.keyFindings.map((finding, idx) => (
              <div
                key={idx}
                className="p-4 rounded-xl bg-slate-900/60 border border-slate-800/80 flex items-start gap-3 hover:border-slate-700 transition-colors"
              >
                <div className="flex items-center justify-center w-5 h-5 rounded-full bg-emerald-500/10 text-emerald-400 text-xs font-bold shrink-0 mt-0.5">
                  {idx + 1}
                </div>
                <div className="text-xs sm:text-sm text-slate-200 leading-relaxed">
                  {renderTextWithCitations(finding)}
                </div>
              </div>
            ))}
          </div>
        </section>
      )}

      {/* 3. Detailed Analysis */}
      {research.analysis && (
        <section className="glass-panel rounded-2xl p-6 sm:p-8 border border-slate-800 shadow-lg">
          <div className="flex items-center gap-2.5 pb-4 mb-4 border-b border-slate-800/80">
            <div className="w-8 h-8 rounded-lg bg-indigo-500/10 flex items-center justify-center text-indigo-400 border border-indigo-500/20">
              <Layers className="w-4 h-4" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-slate-100">
                Detailed Analysis
              </h2>
              <p className="text-xs text-slate-400">
                In-depth investigation, domain dynamics, and comprehensive evaluation
              </p>
            </div>
          </div>

          <div className="space-y-4 text-slate-300 text-sm sm:text-base leading-relaxed">
            {research.analysis.split('\n\n').map((para, idx) => {
              if (para.startsWith('### ') || para.startsWith('## ')) {
                return (
                  <h3
                    key={idx}
                    className="text-base sm:text-lg font-bold text-slate-100 pt-2 pb-1 border-b border-slate-800"
                  >
                    {para.replace(/^#+\s/, '')}
                  </h3>
                );
              }
              return (
                <p key={idx} className="leading-relaxed text-slate-300">
                  {renderTextWithCitations(para)}
                </p>
              );
            })}
          </div>
        </section>
      )}

      {/* 4. Important Insights */}
      {research.importantInsights && research.importantInsights.length > 0 && (
        <section className="glass-panel rounded-2xl p-6 sm:p-8 border border-slate-800 shadow-lg">
          <div className="flex items-center gap-2.5 pb-4 mb-5 border-b border-slate-800/80">
            <div className="w-8 h-8 rounded-lg bg-accent-500/10 flex items-center justify-center text-accent-400 border border-accent-500/20">
              <Lightbulb className="w-4 h-4" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-slate-100">
                Important Insights & Implications
              </h2>
              <p className="text-xs text-slate-400">
                Forward-looking perspectives and actionable intelligence
              </p>
            </div>
          </div>

          <div className="space-y-3">
            {research.importantInsights.map((insight, idx) => (
              <div
                key={idx}
                className="p-4 rounded-xl bg-gradient-to-r from-accent-950/20 via-slate-900/60 to-slate-900/60 border border-accent-500/20 flex items-start gap-3.5"
              >
                <div className="p-1 rounded-md bg-accent-500/20 text-accent-300 shrink-0 mt-0.5">
                  <TrendingUp className="w-3.5 h-3.5" />
                </div>
                <p className="text-xs sm:text-sm text-slate-200 leading-relaxed font-medium">
                  {renderTextWithCitations(insight)}
                </p>
              </div>
            ))}
          </div>
        </section>
      )}

      {/* 5. Sources & Citations */}
      {research.sources && research.sources.length > 0 && (
        <section className="glass-panel rounded-2xl p-6 sm:p-8 border border-slate-800 shadow-lg">
          <SourcesList sources={research.sources} />
        </section>
      )}
    </article>
  );
};
