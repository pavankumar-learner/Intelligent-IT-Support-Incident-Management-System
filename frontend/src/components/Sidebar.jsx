import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  PlusCircle,
  Ticket,
  Headphones,
  BarChart3,
  Users,
  ShieldAlert,
  Sparkles
} from 'lucide-react';

const Sidebar = () => {
  const { user } = useAuth();
  const role = user?.role;

  const isEmployee = role === 'ROLE_EMPLOYEE';
  const isAgent = role === 'ROLE_SUPPORT_AGENT';
  const isAdmin = role === 'ROLE_ADMIN';

  return (
    <aside className="w-64 bg-slate-900/60 border-r border-slate-800 shrink-0 min-h-[calc(100vh-4rem)] p-4 flex flex-col justify-between">
      <div className="space-y-6">
        {/* Navigation Group */}
        <div>
          <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider px-3 mb-2">Main Menu</p>
          <nav className="space-y-1">
            {/* Employee Links */}
            {(isEmployee || isAgent || isAdmin) && (
              <NavLink
                to="/dashboard"
                className={({ isActive }) =>
                  `flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
                    isActive
                      ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                  }`
                }
              >
                <LayoutDashboard className="w-4 h-4" />
                <span>My Overview</span>
              </NavLink>
            )}

            <NavLink
              to="/create-incident"
              className={({ isActive }) =>
                `flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
                  isActive
                    ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`
              }
            >
              <PlusCircle className="w-4 h-4 text-emerald-400" />
              <span>Report Incident</span>
            </NavLink>

            <NavLink
              to="/my-tickets"
              className={({ isActive }) =>
                `flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
                  isActive
                    ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`
              }
            >
              <Ticket className="w-4 h-4" />
              <span>My Tickets</span>
            </NavLink>
          </nav>
        </div>

        {/* Support Agent Section */}
        {(isAgent || isAdmin) && (
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider px-3 mb-2">Support Desk</p>
            <nav className="space-y-1">
              <NavLink
                to="/support-dashboard"
                className={({ isActive }) =>
                  `flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
                    isActive
                      ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                  }`
                }
              >
                <BarChart3 className="w-4 h-4 text-amber-400" />
                <span>Analytics & Desk</span>
              </NavLink>

              <NavLink
                to="/incidents-management"
                className={({ isActive }) =>
                  `flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
                    isActive
                      ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                  }`
                }
              >
                <Headphones className="w-4 h-4 text-indigo-400" />
                <span>All Incident Queue</span>
              </NavLink>
            </nav>
          </div>
        )}

        {/* Admin Section */}
        {isAdmin && (
          <div>
            <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider px-3 mb-2">Administration</p>
            <nav className="space-y-1">
              <NavLink
                to="/admin-dashboard"
                className={({ isActive }) =>
                  `flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition ${
                    isActive
                      ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                      : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                  }`
                }
              >
                <Users className="w-4 h-4 text-purple-400" />
                <span>User Management</span>
              </NavLink>
            </nav>
          </div>
        )}
      </div>

      {/* AI Intelligence Footer Box */}
      <div className="bg-gradient-to-br from-indigo-950/80 to-purple-950/80 border border-indigo-500/20 rounded-xl p-3.5 text-xs text-indigo-200 flex flex-col space-y-2">
        <div className="flex items-center space-x-2 font-semibold text-indigo-300">
          <Sparkles className="w-4 h-4 text-indigo-400 animate-pulse" />
          <span>AI Assistant Active</span>
        </div>
        <p className="text-slate-400 leading-tight">NLP ticket classification & cosine duplicate detection engine online.</p>
      </div>
    </aside>
  );
};

export default Sidebar;
