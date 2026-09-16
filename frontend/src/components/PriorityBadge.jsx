import React from 'react';

const PriorityBadge = ({ priority }) => {
  switch (priority) {
    case 'LOW':
      return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-500/10 text-slate-400 border border-slate-500/20">LOW</span>;
    case 'MEDIUM':
      return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-sky-500/10 text-sky-400 border border-sky-500/20">MEDIUM</span>;
    case 'HIGH':
      return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-orange-500/10 text-orange-400 border border-orange-500/20">HIGH</span>;
    case 'CRITICAL':
      return (
        <span className="inline-flex items-center space-x-1 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-500/20 text-rose-400 border border-rose-500/40 animate-pulse">
          <span className="w-1.5 h-1.5 rounded-full bg-rose-500"></span>
          <span>CRITICAL</span>
        </span>
      );
    default:
      return <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-slate-500/10 text-slate-400">{priority}</span>;
  }
};

export default PriorityBadge;
