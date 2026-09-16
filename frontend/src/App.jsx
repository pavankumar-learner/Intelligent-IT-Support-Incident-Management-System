import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';

import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';

import LoginPage from './pages/LoginPage';
import EmployeeDashboard from './pages/EmployeeDashboard';
import CreateIncidentPage from './pages/CreateIncidentPage';
import MyTicketsPage from './pages/MyTicketsPage';
import SupportDashboard from './pages/SupportDashboard';
import IncidentManagementPage from './pages/IncidentManagementPage';
import AdminDashboard from './pages/AdminDashboard';

const ProtectedLayout = ({ children, allowedRoles }) => {
  const { user, token, loading } = useAuth();

  if (loading) {
    return <div className="min-h-screen bg-slate-950 flex items-center justify-center text-slate-400">Loading Session...</div>;
  }

  if (!token || !user) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="min-h-screen flex flex-col bg-slate-950">
      <Navbar />
      <div className="flex flex-1">
        <Sidebar />
        <main className="flex-1 p-6 lg:p-8 overflow-y-auto max-w-7xl mx-auto w-full">
          {children}
        </main>
      </div>
    </div>
  );
};

const App = () => {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route
            path="/dashboard"
            element={
              <ProtectedLayout>
                <EmployeeDashboard />
              </ProtectedLayout>
            }
          />

          <Route
            path="/create-incident"
            element={
              <ProtectedLayout>
                <CreateIncidentPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/my-tickets"
            element={
              <ProtectedLayout>
                <MyTicketsPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/support-dashboard"
            element={
              <ProtectedLayout allowedRoles={['ROLE_SUPPORT_AGENT', 'ROLE_ADMIN']}>
                <SupportDashboard />
              </ProtectedLayout>
            }
          />

          <Route
            path="/incidents-management"
            element={
              <ProtectedLayout allowedRoles={['ROLE_SUPPORT_AGENT', 'ROLE_ADMIN']}>
                <IncidentManagementPage />
              </ProtectedLayout>
            }
          />

          <Route
            path="/admin-dashboard"
            element={
              <ProtectedLayout allowedRoles={['ROLE_ADMIN']}>
                <AdminDashboard />
              </ProtectedLayout>
            }
          />

          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;
