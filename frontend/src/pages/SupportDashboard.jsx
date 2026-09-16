import React, { useState, useEffect } from 'react';
import { getDashboardStatsApi } from '../services/api';
import StatCard from '../components/StatCard';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  PointElement,
  LineElement
} from 'chart.js';
import { Bar, Doughnut, Pie } from 'react-chartjs-2';
import { Headphones, Ticket, AlertTriangle, Clock, CheckCircle2, ShieldAlert, Users, Layers } from 'lucide-react';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  PointElement,
  LineElement
);

const SupportDashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const res = await getDashboardStatsApi();
      setStats(res.data);
    } catch (err) {
      console.error('Error fetching dashboard stats:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading || !stats) {
    return <div className="text-center py-20 text-slate-400">Loading Support Desk Analytics...</div>;
  }

  // Chart 1: Category Distribution
  const categoryLabels = stats.categoryDistribution?.map(c => c.label) || [];
  const categoryData = stats.categoryDistribution?.map(c => c.count) || [];
  const categoryChartData = {
    labels: categoryLabels,
    datasets: [
      {
        label: 'Incidents by Category',
        data: categoryData,
        backgroundColor: 'rgba(99, 102, 241, 0.65)',
        borderColor: 'rgba(99, 102, 241, 1)',
        borderWidth: 1.5,
        borderRadius: 8,
      }
    ]
  };

  // Chart 2: Priority Distribution
  const priorityLabels = stats.priorityDistribution?.map(p => p.label) || [];
  const priorityData = stats.priorityDistribution?.map(p => p.count) || [];
  const priorityChartData = {
    labels: priorityLabels,
    datasets: [
      {
        label: 'Priority Breakdown',
        data: priorityData,
        backgroundColor: [
          'rgba(148, 163, 184, 0.7)',
          'rgba(56, 189, 248, 0.7)',
          'rgba(251, 146, 60, 0.7)',
          'rgba(244, 63, 94, 0.85)'
        ],
        borderWidth: 0,
      }
    ]
  };

  // Chart 3: Department Distribution
  const deptLabels = stats.departmentDistribution?.map(d => d.label) || [];
  const deptData = stats.departmentDistribution?.map(d => d.count) || [];
  const deptChartData = {
    labels: deptLabels,
    datasets: [
      {
        label: 'Tickets by Department',
        data: deptData,
        backgroundColor: 'rgba(168, 85, 247, 0.65)',
        borderColor: 'rgba(168, 85, 247, 1)',
        borderWidth: 1.5,
        borderRadius: 8,
      }
    ]
  };

  // Chart 4: Agent Workload
  const agentLabels = stats.agentWorkload?.map(a => a.label) || [];
  const agentData = stats.agentWorkload?.map(a => a.count) || [];
  const agentChartData = {
    labels: agentLabels,
    datasets: [
      {
        label: 'Assigned Workload',
        data: agentData,
        backgroundColor: [
          'rgba(129, 140, 248, 0.7)',
          'rgba(52, 211, 153, 0.7)',
          'rgba(251, 113, 133, 0.7)'
        ],
        borderWidth: 0,
      }
    ]
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        labels: { color: '#94a3b8', font: { family: 'Inter', size: 11 } }
      }
    },
    scales: {
      x: { ticks: { color: '#94a3b8' }, grid: { color: 'rgba(255, 255, 255, 0.05)' } },
      y: { ticks: { color: '#94a3b8' }, grid: { color: 'rgba(255, 255, 255, 0.05)' } }
    }
  };

  return (
    <div className="space-y-6">
      <div className="p-6 rounded-3xl bg-slate-900 border border-slate-800 glass-panel flex flex-col sm:flex-row items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">IT Support Desk & Service Analytics</h1>
          <p className="text-sm text-slate-400 mt-1">Real-time telemetry, category distributions, priority breakdowns & agent workloads.</p>
        </div>
        <div className="flex items-center space-x-2 bg-indigo-500/10 border border-indigo-500/20 px-3.5 py-2 rounded-xl text-xs font-semibold text-indigo-300 shrink-0">
          <Headphones className="w-4 h-4 text-indigo-400" />
          <span>Tier 2 Helpdesk Live</span>
        </div>
      </div>

      {/* Top Metrics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total Enterprise Incidents" value={stats.totalIncidents} icon={Ticket} color="indigo" />
        <StatCard title="New / Unassigned" value={stats.newIncidents} icon={AlertTriangle} color="blue" />
        <StatCard title="In-Progress Active" value={stats.inProgressIncidents} icon={Clock} color="amber" />
        <StatCard title="Resolved Incidents" value={stats.resolvedIncidents} icon={CheckCircle2} color="emerald" />
        <StatCard title="Critical Incidents" value={stats.criticalIncidents} icon={ShieldAlert} color="rose" />
        <StatCard title="Avg Resolution Time" value={`${stats.avgResolutionHours} hrs`} icon={Clock} color="purple" subtitle="From ticket creation to resolution" />
        <StatCard title="Active Agent Workload" value={stats.assignedIncidents} icon={Users} color="indigo" />
        <StatCard title="Total Categories" value={stats.categoryDistribution?.length || 9} icon={Layers} color="blue" />
      </div>

      {/* Analytics Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Chart 1: Category Distribution */}
        <div className="glass-panel p-6 rounded-2xl border border-slate-800 space-y-4">
          <h3 className="text-base font-bold text-white tracking-tight">Category Distribution</h3>
          <div className="h-64 relative">
            <Bar data={categoryChartData} options={chartOptions} />
          </div>
        </div>

        {/* Chart 2: Priority Breakdown */}
        <div className="glass-panel p-6 rounded-2xl border border-slate-800 space-y-4">
          <h3 className="text-base font-bold text-white tracking-tight">Priority Breakdown</h3>
          <div className="h-64 relative flex items-center justify-center">
            <Doughnut data={priorityChartData} options={{ ...chartOptions, scales: {} }} />
          </div>
        </div>

        {/* Chart 3: Department Breakdown */}
        <div className="glass-panel p-6 rounded-2xl border border-slate-800 space-y-4">
          <h3 className="text-base font-bold text-white tracking-tight">Tickets by Department</h3>
          <div className="h-64 relative">
            <Bar data={deptChartData} options={chartOptions} />
          </div>
        </div>

        {/* Chart 4: Agent Workload */}
        <div className="glass-panel p-6 rounded-2xl border border-slate-800 space-y-4">
          <h3 className="text-base font-bold text-white tracking-tight">Support Agent Workload Distribution</h3>
          <div className="h-64 relative flex items-center justify-center">
            <Pie data={agentChartData} options={{ ...chartOptions, scales: {} }} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default SupportDashboard;
