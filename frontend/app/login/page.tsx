"use client";

import { FormEvent, useState } from "react";
import Link from "next/link";
import { useAuth } from "@/lib/auth/AuthContext";
import { getApiErrorMessage } from "@/lib/api/client";

export default function LoginPage() {
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await login(email, password);
    } catch (err) {
      setError(getApiErrorMessage(err, "Não foi possível entrar. Verifique suas credenciais."));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="signup-page">
      <section className="signup-card" aria-labelledby="login-title">
        <div className="brand" aria-label="RPG Space"><span className="brand-mark" aria-hidden="true">✦</span><span>RPG SPACE</span></div>
        <header className="signup-header">
          <p className="eyebrow">BEM-VINDO DE VOLTA</p>
          <h1 id="login-title">Entrar na conta</h1>
          <p>Continue sua jornada e retome suas aventuras.</p>
        </header>
        {error && <div className="error-banner" role="alert">{error}</div>}
        <form className="signup-form" onSubmit={handleSubmit}>
          <label>E-mail<span className="input-wrap"><svg aria-hidden="true" viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="14" rx="2" /><path d="m3 7 9 6 9-6" /></svg><input name="email" type="email" placeholder="voce@email.com" autoComplete="email" value={email} onChange={(event) => setEmail(event.target.value)} required /></span></label>
          <label>Senha<span className="input-wrap"><svg aria-hidden="true" viewBox="0 0 24 24"><rect x="5" y="10" width="14" height="11" rx="2" /><path d="M8 10V7a4 4 0 0 1 8 0v3" /></svg><input name="password" type={showPassword ? "text" : "password"} placeholder="Sua senha" autoComplete="current-password" value={password} onChange={(event) => setPassword(event.target.value)} required /><button className="visibility-button" type="button" onClick={() => setShowPassword(!showPassword)} aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"}>{showPassword ? "Ocultar" : "Mostrar"}</button></span></label>
          <button className="submit-button" type="submit" disabled={submitting}>{submitting ? "Entrando..." : "Entrar"} <span aria-hidden="true">→</span></button>
        </form>
        <p className="login-link">Ainda não tem conta? <Link href="/register">Criar conta</Link></p>
      </section>
      <p className="page-footer">© 2026 RPG Space · Feito para grandes histórias</p>
    </main>
  );
}