import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'AI Research Agent | Deep Autonomous Web Research',
  description: 'Production-grade AI Research Agent that executes autonomous web searches, gathers credible sources, extracts deep insights, and synthesizes structured reports with verifiable citations.',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" className="dark">
      <body className="min-h-screen text-slate-100 selection:bg-brand-500 selection:text-white">
        {children}
      </body>
    </html>
  );
}
