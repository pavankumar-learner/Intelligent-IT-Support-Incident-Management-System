import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { getIncidentsApi } from '../services/api';
import { Link } from 'react-router-dom';
import StatCard from '../components/StatCard';
import StatusBadge from '../components/StatusBadge';
import PriorityBadge from '../components/PriorityBadge';
import { Ticket, PlusCircle, Clock, CheckCircle2, AlertTriangle, ArrowRight, ShieldAlert } from 'lucide-react';

const EmployeeDashboard = () => {
  const { user } = useAuth();
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchMyIncidents();
  }, []);

  const fetchMyIncidents = async () => {
    try {
      setLoading(true);
      const res = await getIncidentsApi();
      setIncidents(res.data);
    } catch (err) {
      console.error('Error fetching incidents:', err);
    } finally {
      setLoading(false);
    }
  };

  const total = incidents.length;
  const open = incidents.filter(i => i.status === 'NEW' || i.status === 'ASSIGNED').length;
  const inProgress = incidents.filter(i => i.status === 'IN_PROGRESS' || i.status === 'REOPENED').length;
  const resolved = incidents.filter(i => i.status === 'RESOLVED' || i.status === 'CLOSED').length;
  const criticalHigh = incidents.filter(i => i.priority === 'CRITICAL' || i.priority === 'HIGH').length;

  return (
    <div className="space-y-6">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-6 rounded-3xl bg-gradient-to-r from-indigo-900/60 via-slate-900 to-slate-900 border border-indigo-500/20 glass-panel">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Welcome back, {user?.name}!</h1>
          <p className="text-sm text-slate-400 mt-1">Track your active IT support tickets or submit a new incident request.</p>
        </div>
        <Link
          to="/create-incident"
          className="inline-flex items-center space-x-2 px-5 py-3 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-sm shadow-lg shadow-indigo-600/30 transition shrink-0"
        >
          <PlusCircle className="w-5 h-5 text-emerald-400" />
          <span>Report New Incident</span>
        </Link>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
        <StatCard title="Total Tickets" value={total} icon={Ticket} color="indigo" />
        <StatCard title="Open" value={open} icon={AlertTriangle} color="blue" />
        <StatCard title="In Progress" value={inProgress} icon={Clock} color="amber" />
        <StatCard title="Resolved" value={resolved} icon={CheckCircle2} color="emerald" />
        <StatCard title="High / Critical" value={criticalHigh} icon={ShieldAlert} color="rose" />
      </div>

      {/* Recent Tickets Section */}
      <div className="glass-panel p-6 rounded-2xl border border-slate-800 space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-bold text-white tracking-tight">My Recent Incidents</h2>
          <Link to="/my-tickets" className="text-xs font-semibold text-indigo-400 hover:text-indigo-300 flex items-center space-x-1">
            <span>View All</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        {loading ? (
          <div className="text-center py-8 text-slate-400 text-sm">Loading your tickets...</div>
        ) : incidents.length === 0 ? (
          <div className="text-center py-12 text-slate-400 space-y-2">
            <Ticket className="w-10 h-10 mx-auto text-slate-600" />
            <p>You haven't reported any IT incidents yet.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-slate-300">
              <thead className="text-xs uppercase bg-slate-800/60 text-slate-400 border-b border-slate-700/60">
                <tr>
                  <th className="py-3 px-4">Ticket ID</th>
                  <th className="py-3 px-4">Title</th>
                  <th className="py-3 px-4">Category</th>
                  <th className="py-3 px-4">Priority</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4">Assigned Agent</th>
                  <th className="py-3 px-4">Date</th>
                  <th className="py-3 px-4 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {incidents.slice(0, 6).map((ticket) => (
                  <tr key={ticket.id} className="hover:bg-slate-800/40 transition">
                    <td className="py-3.5 px-4 font-mono font-semibold text-indigo-400">{ticket.ticketNumber}</td>
                    <td className="py-3.5 px-4 font-medium text-white max-w-xs truncate">{ticket.title}</td>
                    <td className="py-3.5 px-4 text-slate-300">{ticket.categoryDisplay || ticket.category}</td>
                    <td className="py-3.5 px-4"><PriorityBadge priority={ticket.priority} /></td>
                    <td className="py-3.5 px-4"><StatusBadge status={ticket.status} /></td>
                    <td className="py-3.5 px-4 text-slate-400">{ticket.assignedTo ? ticket.assignedTo.name : 'Unassigned'}</td>
                    <td className="py-3.5 px-4 text-slate-400 text-xs">{new Date(ticket.createdAt).toLocaleDateString()}</td>
                    <td className="py-3.5 px-4 text-right">
                      <Link
                        to={`/my-tickets?id=${ticket.id}`}
                        className="text-xs font-semibold px-3 py-1.5 rounded-lg bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 hover:bg-indigo-500/20 transition"
                      >
                        Details
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default EmployeeDashboard;
