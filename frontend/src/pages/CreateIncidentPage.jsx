import React, { useState } from 'react';
import { createIncidentApi, predictCategoryApi, predictPriorityApi, checkDuplicateApi } from '../services/api';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Sparkles, AlertTriangle, CheckCircle2, ArrowRight, LifeBuoy, FileText } from 'lucide-react';

const CATEGORIES = [
  { value: 'Hardware', label: 'Hardware' },
  { value: 'Software', label: 'Software' },
  { value: 'Network', label: 'Network' },
  { value: 'Email', label: 'Email' },
  { value: 'Access_Login', label: 'Access / Login' },
  { value: 'Security', label: 'Security' },
  { value: 'Database', label: 'Database' },
  { value: 'Printer', label: 'Printer' },
  { value: 'Other', label: 'Other' },
];

const PRIORITIES = [
  { value: 'LOW', label: 'Low' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'HIGH', label: 'High' },
  { value: 'CRITICAL', label: 'Critical' },
];

const CreateIncidentPage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('Hardware');
  const [priority, setPriority] = useState('MEDIUM');
  const [location, setLocation] = useState('HQ Main Building');
  const [department, setDepartment] = useState(user?.department || 'IT Operations');

  const [aiLoading, setAiLoading] = useState(false);
  const [aiCategoryResult, setAiCategoryResult] = useState(null);
  const [aiPriorityResult, setAiPriorityResult] = useState(null);

  const [dupResult, setDupResult] = useState(null);
  const [dupLoading, setDupLoading] = useState(false);

  const [submitting, setSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  // Handle AI Auto-Predict Category & Priority
  const handleAiAutoPredict = async () => {
    if (!title.trim()) {
      setErrorMessage('Please enter a title before triggering AI prediction.');
      return;
    }
    setErrorMessage('');
    setAiLoading(true);
    try {
      const [catRes, priRes] = await Promise.all([
        predictCategoryApi(title, description),
        predictPriorityApi(title, description, category)
      ]);

      setAiCategoryResult(catRes.data);
      setAiPriorityResult(priRes.data);

      if (catRes.data.prediction) {
        setCategory(catRes.data.prediction);
      }
      if (priRes.data.prediction) {
        setPriority(priRes.data.prediction);
      }
    } catch (err) {
      console.error('AI Auto-Predict error:', err);
    } finally {
      setAiLoading(false);
    }
  };

  // Handle Live Duplicate Ticket Check
  const handleCheckDuplicates = async () => {
    if (!title.trim()) return;
    setDupLoading(true);
    try {
      const res = await checkDuplicateApi(title, description, 0.65);
      setDupResult(res.data);
    } catch (err) {
      console.error('Duplicate Check error:', err);
    } finally {
      setDupLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setSubmitting(true);

    try {
      const payload = {
        title,
        description,
        category,
        priority,
        location,
        department
      };

      const res = await createIncidentApi(payload);
      setSuccessMessage(`Incident ticket ${res.data.ticketNumber} created successfully!`);
      setTimeout(() => {
        navigate('/my-tickets');
      }, 1500);
    } catch (err) {
      setErrorMessage(err.response?.data?.message || 'Failed to submit incident ticket.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 glass-panel">
        <h1 className="text-2xl font-bold text-white tracking-tight">Report IT Incident</h1>
        <p className="text-sm text-slate-400 mt-1">Submit an IT issue ticket with real-time NLP classification & duplicate detection.</p>
      </div>

      {successMessage && (
        <div className="p-4 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-sm flex items-center space-x-2">
          <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />
          <span>{successMessage}</span>
        </div>
      )}

      {errorMessage && (
        <div className="p-4 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-400 text-sm flex items-center space-x-2">
          <AlertTriangle className="w-5 h-5 text-rose-400 shrink-0" />
          <span>{errorMessage}</span>
        </div>
      )}

      {/* Duplicate Incident Alert Banner */}
      {dupResult && dupResult.isDuplicate && (
        <div className="p-5 rounded-2xl bg-amber-500/10 border border-amber-500/30 text-amber-300 space-y-2 relative overflow-hidden">
          <div className="flex items-center space-x-2 font-bold text-amber-400">
            <AlertTriangle className="w-5 h-5" />
            <span>Potential Duplicate Incident Detected!</span>
          </div>
          <p className="text-xs text-amber-200/90 leading-relaxed">
            {dupResult.message} Title match: <span className="font-semibold text-white">"{dupResult.matchedTitle}"</span>.
          </p>
          <p className="text-xs text-amber-300/70">You can still proceed with submitting your ticket if this is a separate issue.</p>
        </div>
      )}

      <form onSubmit={handleSubmit} className="glass-panel p-8 rounded-3xl border border-slate-800 space-y-6">
        {/* Title */}
        <div>
          <div className="flex items-center justify-between mb-2">
            <label className="text-xs font-semibold text-slate-300 uppercase tracking-wider">Incident Title *</label>
            <button
              type="button"
              onClick={handleAiAutoPredict}
              disabled={aiLoading}
              className="inline-flex items-center space-x-1.5 text-xs font-semibold text-indigo-400 hover:text-indigo-300 bg-indigo-500/10 hover:bg-indigo-500/20 px-3 py-1 rounded-lg border border-indigo-500/30 transition disabled:opacity-60"
            >
              <Sparkles className={`w-3.5 h-3.5 text-indigo-400 ${aiLoading ? 'animate-spin' : ''}`} />
              <span>{aiLoading ? 'Analyzing Text...' : 'AI Auto-Predict'}</span>
            </button>
          </div>
          <input
            type="text"
            required
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            onBlur={handleCheckDuplicates}
            placeholder="e.g. Unable to connect to office Wi-Fi in Block B"
            className="w-full bg-slate-900/90 border border-slate-700/80 rounded-xl px-4 py-3 text-sm text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500 transition"
          />
        </div>

        {/* AI Insight Highlight Card */}
        {(aiCategoryResult || aiPriorityResult) && (
          <div className="p-4 rounded-xl bg-gradient-to-r from-indigo-950/80 to-purple-950/80 border border-indigo-500/30 space-y-2">
            <div className="flex items-center space-x-2 text-xs font-bold text-indigo-300 uppercase tracking-wider">
              <Sparkles className="w-4 h-4 text-indigo-400" />
              <span>AI Classifier Telemetry</span>
            </div>
            <div className="grid grid-cols-2 gap-4 text-xs">
              <div>
                <span className="text-slate-400">Predicted Category: </span>
                <span className="font-bold text-emerald-400">{aiCategoryResult?.prediction}</span>
                <span className="text-slate-400 ml-1">({Math.round((aiCategoryResult?.confidence || 0.85) * 100)}%)</span>
              </div>
              <div>
                <span className="text-slate-400">Predicted Priority: </span>
                <span className="font-bold text-amber-400">{aiPriorityResult?.prediction}</span>
                <span className="text-slate-400 ml-1">({Math.round((aiPriorityResult?.confidence || 0.80) * 100)}%)</span>
              </div>
            </div>
            {aiCategoryResult?.topKeywords && aiCategoryResult.topKeywords.length > 0 && (
              <div className="text-[11px] text-indigo-200/80">
                Key Trigger Terms: {aiCategoryResult.topKeywords.map(k => `#${k}`).join(' ')}
              </div>
            )}
          </div>
        )}

        {/* Description */}
        <div>
          <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">Detailed Description *</label>
          <textarea
            required
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            onBlur={handleCheckDuplicates}
            placeholder="Describe the problem, error messages, and step to reproduce..."
            className="w-full bg-slate-900/90 border border-slate-700/80 rounded-xl px-4 py-3 text-sm text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500 transition"
          />
        </div>

        {/* Category & Priority Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">Category</label>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full bg-slate-900/90 border border-slate-700/80 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:border-indigo-500 transition"
            >
              {CATEGORIES.map((cat) => (
                <option key={cat.value} value={cat.value}>{cat.label}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">Priority</label>
            <select
              value={priority}
              onChange={(e) => setPriority(e.target.value)}
              className="w-full bg-slate-900/90 border border-slate-700/80 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:border-indigo-500 transition"
            >
              {PRIORITIES.map((pri) => (
                <option key={pri.value} value={pri.value}>{pri.label}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Department & Location */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">Department</label>
            <input
              type="text"
              required
              value={department}
              onChange={(e) => setDepartment(e.target.value)}
              className="w-full bg-slate-900/90 border border-slate-700/80 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:border-indigo-500 transition"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">Location / Desk ID</label>
            <input
              type="text"
              required
              value={location}
              onChange={(e) => setLocation(e.target.value)}
              placeholder="e.g. Block B, Desk 402"
              className="w-full bg-slate-900/90 border border-slate-700/80 rounded-xl px-4 py-3 text-sm text-white focus:outline-none focus:border-indigo-500 transition"
            />
          </div>
        </div>

        {/* Form Submit Actions */}
        <div className="flex items-center justify-end space-x-4 pt-4 border-t border-slate-800">
          <button
            type="button"
            onClick={() => navigate('/my-tickets')}
            className="px-5 py-2.5 rounded-xl border border-slate-700 text-slate-300 hover:bg-slate-800 text-sm font-semibold transition"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={submitting}
            className="px-6 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-sm shadow-lg shadow-indigo-600/30 transition disabled:opacity-50 flex items-center space-x-2"
          >
            <span>{submitting ? 'Submitting Ticket...' : 'Submit Incident Ticket'}</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateIncidentPage;
