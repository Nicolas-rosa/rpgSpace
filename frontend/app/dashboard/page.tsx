"use client";

import ProtectedRoute from "@/components/ProtectedRoute";
import { useAuth } from "@/lib/auth/AuthContext";

export default function DashboardPage() {
  const { user, logout } = useAuth();

  return (
    <ProtectedRoute>
      <main className="dashboard-page">
        <section className="dashboard-card" aria-labelledby="dashboard-title">
          <div className="brand" aria-label="RPG Space"><span className="brand-mark" aria-hidden="true">✦</span><span>RPG SPACE</span></div>
          <header className="signup-header">
            <p className="eyebrow">PAINEL DO JOGADOR</p>
            <h1 id="dashboard-title">Olá, {user?.username}!</h1>
            <p>Bem-vindo de volta à sua jornada.</p>
          </header>
          <dl className="dashboard-info">
            <div><dt>Nome de exibição</dt><dd>{user?.username}</dd></div>
            <div><dt>E-mail</dt><dd>{user?.email}</dd></div>
            <div><dt>Perfil</dt><dd>{user?.role}</dd></div>
          </dl>
          <button className="logout-button" type="button" onClick={() => logout()}>Sair da conta</button>
        </section>
        <p className="page-footer">© 2026 RPG Space · Feito para grandes histórias</p>
      </main>
    </ProtectedRoute>
  );
}