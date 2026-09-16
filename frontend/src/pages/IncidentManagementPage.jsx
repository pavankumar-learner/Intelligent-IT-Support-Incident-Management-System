import React, { useState, useEffect } from 'react';
import { getIncidentsApi, getAgentsApi, assignAgentApi, updateStatusApi, updatePriorityApi } from '../services/api';
import StatusBadge from '../components/StatusBadge';
import PriorityBadge from '../components/PriorityBadge';
import TicketDetailsPage from './TicketDetailsPage';
import { Search, Filter, Headphones, UserCheck, ShieldAlert, CheckCircle2, ChevronLeft, ChevronRight } from 'lucide-react';

const IncidentManagementPage = () => {
  const [incidents, setIncidents] = useState([]);
  const [agents, setAgents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedIncidentId, setSelectedIncidentId] = useState(null);

  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [priorityFilter, setPriorityFilter] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  // Modals / Quick Actions State
  const [assignModalIncident, setAssignModalIncident] = useState(null);
  const [selectedAgentId, setSelectedAgentId] = useState('');

  const [statusModalIncident, setStatusModalIncident] = useState(null);
  const [newStatus, setNewStatus] = useState('IN_PROGRESS');
  const [statusComment, setStatusComment] = useState('');

  const [priorityModalIncident, setPriorityModalIncident] = useState(null);
  const [newPriority, setNewPriority] = useState('HIGH');
  const [priorityComment, setPriorityComment] = useState('');

  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;

  useEffect(() => {
    fetchData();
  }, [search, categoryFilter, priorityFilter, statusFilter]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const params = {};
      if (search) params.search = search;
      if (categoryFilter) params.category = categoryFilter;
      if (priorityFilter) params.priority = priorityFilter;
      if (statusFilter) params.status = statusFilter;

      const [incRes, agentRes] = await Promise.all([
        getIncidentsApi(params),
        getAgentsApi()
      ]);
      setIncidents(incRes.data);
      setAgents(agentRes.data);
      setCurrentPage(1);
    } catch (err) {
      console.error('Error fetching support queue data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAssignSubmit = async (e) => {
    e.preventDefault();
    if (!selectedAgentId || !assignModalIncident) return;
    try {
      await assignAgentApi(assignModalIncident.id, selectedAgentId);
      setAssignModalIncident(null);
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to assign agent');
    }
  };

  const handleStatusSubmit = async (e) => {
    e.preventDefault();
    if (!statusModalIncident) return;
    try {
      await updateStatusApi(statusModalIncident.id, newStatus, statusComment);
      setStatusModalIncident(null);
      setStatusComment('');
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to update status');
    }
  };

  const handlePrioritySubmit = async (e) => {
    e.preventDefault();
    if (!priorityModalIncident) return;
    try {
      await updatePriorityApi(priorityModalIncident.id, newPriority, priorityComment);
      setPriorityModalIncident(null);
      setPriorityComment('');
      fetchData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to override priority');
    }
  };

  const totalPages = Math.ceil(incidents.length / itemsPerPage) || 1;
  const paginated = incidents.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  if (selectedIncidentId) {
    return <TicketDetailsPage incidentId={selectedIncidentId} onBack={() => setSelectedIncidentId(null)} />;
  }

  return (
    <div className="space-y-6">
      <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 glass-panel flex flex-col sm:flex-row items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Enterprise Incident Queue</h1>
          <p className="text-sm text-slate-400 mt-1">Manage ticket assignments, status transitions, priority overrides, and SLA resolutions.</p>
        </div>
        <div className="text-xs text-slate-400 bg-slate-800 px-3.5 py-2 rounded-xl border border-slate-700 font-medium shrink-0">
          Total Queue: <span className="font-bold text-indigo-400">{incidents.length}</span> tickets
        </div>
      </div>

      {/* Search & Filters */}
      <div className="glass-panel p-4 rounded-2xl border border-slate-800 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
        <div className="relative">
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search ticket # or keywords..."
            className="w-full bg-slate-900 border border-slate-700/80 rounded-xl pl-9 pr-4 py-2 text-sm text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500"
          />
          <Search className="w-4 h-4 text-slate-500 absolute left-3 top-3" />
        </div>

        <select
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          className="bg-slate-900 border border-slate-700/80 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
        >
          <option value="">All Categories</option>
          <option value="Hardware">Hardware</option>
          <option value="Software">Software</option>
          <option value="Network">Network</option>
          <option value="Email">Email</option>
          <option value="Access_Login">Access/Login</option>
          <option value="Security">Security</option>
          <option value="Database">Database</option>
          <option value="Printer">Printer</option>
          <option value="Other">Other</option>
        </select>

        <select
          value={priorityFilter}
          onChange={(e) => setPriorityFilter(e.target.value)}
          className="bg-slate-900 border border-slate-700/80 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
        >
          <option value="">All Priorities</option>
          <option value="LOW">Low</option>
          <option value="MEDIUM">Medium</option>
          <option value="HIGH">High</option>
          <option value="CRITICAL">Critical</option>
        </select>

        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="bg-slate-900 border border-slate-700/80 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
        >
          <option value="">All Statuses</option>
          <option value="NEW">New</option>
          <option value="ASSIGNED">Assigned</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="RESOLVED">Resolved</option>
          <option value="CLOSED">Closed</option>
          <option value="REOPENED">Reopened</option>
        </select>
      </div>

      {/* Incidents Queue Table */}
      <div className="glass-panel rounded-2xl border border-slate-800 overflow-hidden">
        {loading ? (
          <div className="text-center py-12 text-slate-400 text-sm">Loading incident queue...</div>
        ) : paginated.length === 0 ? (
          <div className="text-center py-16 text-slate-400">No tickets found.</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-300">
              <thead className="text-xs uppercase bg-slate-800/80 text-slate-400 border-b border-slate-700">
                <tr>
                  <th className="py-3.5 px-4">Ticket</th>
                  <th className="py-3.5 px-4">Title</th>
                  <th className="py-3.5 px-4">Category</th>
                  <th className="py-3.5 px-4">Priority</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4">Reporter</th>
                  <th className="py-3.5 px-4">Assigned Agent</th>
                  <th className="py-3.5 px-4 text-right">Quick Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {paginated.map((t) => (
                  <tr key={t.id} className="hover:bg-slate-800/40 transition">
                    <td className="py-3.5 px-4 font-mono font-semibold text-indigo-400">{t.ticketNumber}</td>
                    <td className="py-3.5 px-4 font-medium text-white max-w-xs truncate">{t.title}</td>
                    <td className="py-3.5 px-4 text-slate-300">{t.categoryDisplay || t.category}</td>
                    <td className="py-3.5 px-4"><PriorityBadge priority={t.priority} /></td>
                    <td className="py-3.5 px-4"><StatusBadge status={t.status} /></td>
                    <td className="py-3.5 px-4 text-xs text-slate-400">{t.createdBy?.name} ({t.department})</td>
                    <td className="py-3.5 px-4 text-xs text-slate-300 font-medium">{t.assignedTo ? t.assignedTo.name : <span className="text-amber-400 italic">Unassigned</span>}</td>
                    <td className="py-3.5 px-4 text-right space-x-1">
                      <button
                        onClick={() => { setAssignModalIncident(t); setSelectedAgentId(t.assignedTo?.id || ''); }}
                        className="px-2.5 py-1 rounded bg-indigo-500/10 hover:bg-indigo-500/20 text-indigo-300 border border-indigo-500/20 text-xs font-semibold transition"
                      >
                        Assign
                      </button>
                      <button
                        onClick={() => { setStatusModalIncident(t); setNewStatus(t.status); }}
                        className="px-2.5 py-1 rounded bg-purple-500/10 hover:bg-purple-500/20 text-purple-300 border border-purple-500/20 text-xs font-semibold transition"
                      >
                        Status
                      </button>
                      <button
                        onClick={() => setSelectedIncidentId(t.id)}
                        className="px-2.5 py-1 rounded bg-slate-700 hover:bg-slate-600 text-white text-xs font-semibold transition"
                      >
                        View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        <div className="p-4 border-t border-slate-800 flex items-center justify-between text-xs text-slate-400">
          <span>Page {currentPage} of {totalPages}</span>
          <div className="flex items-center space-x-2">
            <button
              disabled={currentPage === 1}
              onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
              className="p-1.5 rounded-lg border border-slate-700 disabled:opacity-30 hover:bg-slate-800 transition"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <button
              disabled={currentPage === totalPages}
              onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
              className="p-1.5 rounded-lg border border-slate-700 disabled:opacity-30 hover:bg-slate-800 transition"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* ASSIGN AGENT MODAL */}
      {assignModalIncident && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl max-w-md w-full p-6 space-y-4">
            <h3 className="text-lg font-bold text-white">Assign Ticket: {assignModalIncident.ticketNumber}</h3>
            <form onSubmit={handleAssignSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-2 uppercase">Select Support Agent</label>
                <select
                  required
                  value={selectedAgentId}
                  onChange={(e) => setSelectedAgentId(e.target.value)}
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-2.5 text-sm text-white focus:outline-none"
                >
                  <option value="">-- Choose Agent --</option>
                  {agents.map(a => (
                    <option key={a.id} value={a.id}>{a.name} ({a.department})</option>
                  ))}
                </select>
              </div>
              <div className="flex justify-end space-x-3 pt-4 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setAssignModalIncident(null)}
                  className="px-4 py-2 rounded-xl border border-slate-700 text-slate-300 text-xs font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold"
                >
                  Save Assignment
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* UPDATE STATUS MODAL */}
      {statusModalIncident && (
        <div className="fixed inset-0 bg-slate-950/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl max-w-md w-full p-6 space-y-4">
            <h3 className="text-lg font-bold text-white">Update Status: {statusModalIncident.ticketNumber}</h3>
            <form onSubmit={handleStatusSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-2 uppercase">Target Status</label>
                <select
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value)}
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-2.5 text-sm text-white focus:outline-none"
                >
                  <option value="NEW">NEW</option>
                  <option value="ASSIGNED">ASSIGNED</option>
                  <option value="IN_PROGRESS">IN PROGRESS</option>
                  <option value="RESOLVED">RESOLVED</option>
                  <option value="CLOSED">CLOSED</option>
                  <option value="REOPENED">REOPENED</option>
                </select>
              </div>
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-2 uppercase">Resolution Note / Action Comment</label>
                <textarea
                  rows={3}
                  value={statusComment}
                  onChange={(e) => setStatusComment(e.target.value)}
                  placeholder="Describe resolution or reason for status update..."
                  className="w-full bg-slate-800 border border-slate-700 rounded-xl px-4 py-2.5 text-sm text-white focus:outline-none"
                />
              </div>
              <div className="flex justify-end space-x-3 pt-4 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setStatusModalIncident(null)}
                  className="px-4 py-2 rounded-xl border border-slate-700 text-slate-300 text-xs font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white text-xs font-semibold"
                >
                  Update Status
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default IncidentManagementPage;
