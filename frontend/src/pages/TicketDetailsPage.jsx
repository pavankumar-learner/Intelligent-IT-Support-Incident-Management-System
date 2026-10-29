import React, { useState, useEffect } from 'react';
import { getIncidentByIdApi, getCommentsApi, getHistoryApi, addCommentApi } from '../services/api';
import StatusBadge from '../components/StatusBadge';
import PriorityBadge from '../components/PriorityBadge';
import AiBadge from '../components/AiBadge';
import { ArrowLeft, Clock, MessageSquare, Send, Sparkles, User, ShieldCheck, MapPin, Building } from 'lucide-react';

const TicketDetailsPage = ({ incidentId, onBack }) => {
  const [incident, setIncident] = useState(null);
  const [comments, setComments] = useState([]);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [newComment, setNewComment] = useState('');
  const [submittingComment, setSubmittingComment] = useState(false);
  const [commentError, setCommentError] = useState('');

  useEffect(() => {
    if (incidentId) {
      fetchTicketDetails();
    }
  }, [incidentId]);

  const fetchTicketDetails = async () => {
    setLoading(true);
    try {
      const [incRes, comRes, histRes] = await Promise.all([
        getIncidentByIdApi(incidentId),
        getCommentsApi(incidentId),
        getHistoryApi(incidentId)
      ]);
      setIncident(incRes.data);
      setComments(comRes.data);
      setHistory(histRes.data);
    } catch (err) {
      console.error('Error loading ticket details:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    setSubmittingComment(true);
    setCommentError('');
    try {
      await addCommentApi(incidentId, newComment);
      setNewComment('');
      const comRes = await getCommentsApi(incidentId);
      setComments(comRes.data);
    } catch (err) {
      setCommentError(err.response?.data?.message || 'Failed to post comment. Please try again.');
    } finally {
      setSubmittingComment(false);
    }
  };

  if (loading || !incident) {
    return <div className="text-center py-20 text-slate-400">Loading ticket timeline details...</div>;
  }

  return (
    <div className="max-w-5xl mx-auto space-y-6">
      {/* Back Button Header */}
      <div className="flex items-center justify-between">
        <button
          onClick={onBack}
          className="inline-flex items-center space-x-2 text-xs font-semibold text-slate-400 hover:text-white bg-slate-800 hover:bg-slate-700 px-3.5 py-2 rounded-xl transition border border-slate-700"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Back to Ticket List</span>
        </button>
        <div className="flex items-center space-x-2">
          <StatusBadge status={incident.status} />
          <PriorityBadge priority={incident.priority} />
        </div>
      </div>

      {/* Main Ticket Info Glass Box */}
      <div className="glass-panel p-8 rounded-3xl border border-slate-800 space-y-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-slate-800 pb-6">
          <div>
            <div className="flex items-center space-x-3 mb-2">
              <span className="font-mono text-lg font-bold text-indigo-400 px-3 py-1 bg-indigo-500/10 border border-indigo-500/20 rounded-lg">
                {incident.ticketNumber}
              </span>
              <span className="text-xs text-slate-400">{incident.categoryDisplay || incident.category}</span>
            </div>
            <h1 className="text-2xl font-bold text-white tracking-tight">{incident.title}</h1>
          </div>

          <div className="text-right text-xs text-slate-400 space-y-1">
            <div>Created: <span className="text-slate-200 font-medium">{new Date(incident.createdAt).toLocaleString()}</span></div>
            {incident.resolvedAt && (
              <div>Resolved: <span className="text-emerald-400 font-medium">{new Date(incident.resolvedAt).toLocaleString()}</span></div>
            )}
          </div>
        </div>

        {/* AI Audit Telecom Telemetry Box */}
        <div className="p-5 rounded-2xl bg-gradient-to-r from-indigo-950/70 to-purple-950/70 border border-indigo-500/30 grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
          <div className="space-y-1">
            <div className="flex items-center space-x-2 font-bold text-indigo-300 uppercase tracking-wider">
              <Sparkles className="w-4 h-4 text-indigo-400" />
              <span>AI Category Telemetry</span>
            </div>
            <p className="text-slate-300">
              Predicted: <span className="font-semibold text-emerald-400">{incident.predictedCategory || incident.category}</span>
              <span className="ml-2"><AiBadge confidence={incident.categoryConfidence || 0.88} /></span>
            </p>
          </div>

          <div className="space-y-1">
            <div className="flex items-center space-x-2 font-bold text-indigo-300 uppercase tracking-wider">
              <Sparkles className="w-4 h-4 text-indigo-400" />
              <span>AI Priority Telemetry</span>
            </div>
            <p className="text-slate-300">
              Predicted: <span className="font-semibold text-amber-400">{incident.predictedPriority || incident.priority}</span>
              <span className="ml-2"><AiBadge confidence={incident.priorityConfidence || 0.82} /></span>
            </p>
          </div>
        </div>

        {/* Ticket Description */}
        <div>
          <h3 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">Description</h3>
          <p className="text-sm text-slate-200 bg-slate-900/60 p-4 rounded-xl border border-slate-800 leading-relaxed whitespace-pre-line">
            {incident.description}
          </p>
        </div>

        {/* Metadata Details Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 p-4 rounded-xl bg-slate-900/40 border border-slate-800/80 text-xs">
          <div>
            <span className="text-slate-500 block uppercase font-medium mb-1">Reporter</span>
            <span className="text-white font-semibold flex items-center space-x-1">
              <User className="w-3.5 h-3.5 text-indigo-400" />
              <span>{incident.createdBy?.name}</span>
            </span>
          </div>

          <div>
            <span className="text-slate-500 block uppercase font-medium mb-1">Assigned Agent</span>
            <span className="text-white font-semibold flex items-center space-x-1">
              <ShieldCheck className="w-3.5 h-3.5 text-emerald-400" />
              <span>{incident.assignedTo ? incident.assignedTo.name : 'Unassigned'}</span>
            </span>
          </div>

          <div>
            <span className="text-slate-500 block uppercase font-medium mb-1">Department</span>
            <span className="text-white font-semibold flex items-center space-x-1">
              <Building className="w-3.5 h-3.5 text-purple-400" />
              <span>{incident.department}</span>
            </span>
          </div>

          <div>
            <span className="text-slate-500 block uppercase font-medium mb-1">Location</span>
            <span className="text-white font-semibold flex items-center space-x-1">
              <MapPin className="w-3.5 h-3.5 text-amber-400" />
              <span>{incident.location}</span>
            </span>
          </div>
        </div>
      </div>

      {/* Grid: Audit Timeline & Comments */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Incident Audit History Timeline */}
        <div className="glass-panel p-6 rounded-3xl border border-slate-800 space-y-4">
          <div className="flex items-center space-x-2 font-bold text-white text-base">
            <Clock className="w-5 h-5 text-indigo-400" />
            <span>Incident Audit Lifecycle Timeline</span>
          </div>

          <div className="space-y-4 relative before:absolute before:inset-0 before:left-3.5 before:w-0.5 before:bg-slate-800 pt-2">
            {history.map((item, idx) => (
              <div key={item.id || idx} className="relative pl-8 space-y-1">
                <div className="absolute left-1.5 top-1 w-4 h-4 rounded-full bg-indigo-600 border-2 border-slate-900"></div>
                <div className="text-xs font-semibold text-white flex items-center justify-between">
                  <span>{item.actionNote || 'Status Action'}</span>
                  <span className="text-slate-500 font-normal">{new Date(item.changedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                </div>
                <div className="text-[11px] text-slate-400">
                  By <span className="text-slate-300 font-medium">{item.changedBy?.name}</span>
                  {item.newStatus && <span className="ml-2 text-indigo-300">[{item.newStatus}]</span>}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Interactive Comments Section */}
        <div className="glass-panel p-6 rounded-3xl border border-slate-800 space-y-4 flex flex-col justify-between">
          <div className="space-y-4">
            <div className="flex items-center space-x-2 font-bold text-white text-base">
              <MessageSquare className="w-5 h-5 text-purple-400" />
              <span>Support Discussion ({comments.length})</span>
            </div>

            <div className="space-y-3 max-h-80 overflow-y-auto pr-2">
              {comments.length === 0 ? (
                <p className="text-xs text-slate-500 italic py-4">No comments added yet.</p>
              ) : (
                comments.map((c) => (
                  <div key={c.id} className="p-3.5 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-1 text-xs">
                    <div className="flex items-center justify-between font-semibold text-slate-300">
                      <span className="text-indigo-400">{c.user?.name} ({c.user?.role?.replace('ROLE_', '')})</span>
                      <span className="text-[10px] text-slate-500">{new Date(c.createdAt).toLocaleString()}</span>
                    </div>
                    <p className="text-slate-200 leading-relaxed">{c.comment}</p>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Add Comment Form */}
          <form onSubmit={handleAddComment} className="pt-4 border-t border-slate-800 space-y-2">
            {commentError && (
              <p className="text-xs text-rose-400 font-medium">{commentError}</p>
            )}
            <div className="relative">
              <input
                type="text"
                required
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="Type a comment or status update note..."
                className="w-full bg-slate-900 border border-slate-700 rounded-xl pl-4 pr-12 py-3 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500"
              />
              <button
                type="submit"
                disabled={submittingComment}
                className="absolute right-2 top-2 p-1.5 rounded-lg bg-indigo-600 hover:bg-indigo-500 text-white transition disabled:opacity-50"
              >
                <Send className="w-4 h-4" />
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default TicketDetailsPage;
