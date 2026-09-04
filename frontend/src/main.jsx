import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ReceiptsPage from './pages/ReceiptsPage';
import ReceiptDetailPage from './pages/ReceiptDetailPage';
import './styles.css';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route element={<ProtectedRoute />}>
          <Route element={<Layout />}>
            <Route index element={<Navigate to="/receipts" replace />} />
            <Route path="receipts" element={<ReceiptsPage />} />
            <Route path="receipts/:id" element={<ReceiptDetailPage />} />
            <Route path="trash" element={<ReceiptsPage isDeleted />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to="/receipts" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

createRoot(document.getElementById('root')).render(
  <React.StrictMode><App /></React.StrictMode>,
);
