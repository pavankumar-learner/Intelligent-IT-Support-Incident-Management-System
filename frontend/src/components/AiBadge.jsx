import React from 'react';
import { Sparkles } from 'lucide-react';

const AiBadge = ({ confidence }) => {
  const percentage = confidence ? Math.round(confidence * 100) : 85;
  return (
    <span className="inline-flex items-center space-x-1 px-2 py-0.5 rounded text-[11px] font-medium bg-indigo-500/10 text-indigo-300 border border-indigo-500/20" title={`AI Predicted with ${percentage}% confidence`}>
      <Sparkles className="w-3 h-3 text-indigo-400" />
      <span>AI ({percentage}%)</span>
    </span>
  );
};

export default AiBadge;
