"use client";

import { FormEvent, useState } from "react";

export default function Home() {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const passwordsMatch = !confirmPassword || password === confirmPassword;

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (password !== confirmPassword) return;
    setSubmitted(true);
  }

  return (
    <main className="signup-page">
      <section className="signup-card" aria-labelledby="signup-title">
        <div className="brand" aria-label="RPG Space"><span className="brand-mark" aria-hidden="true">✦</span><span>RPG SPACE</span></div>
        <header className="signup-header">
          <p className="eyebrow">COMECE SUA JORNADA</p>
          <h1 id="signup-title">Crie sua conta</h1>
          <p>Entre para a comunidade e prepare-se para a próxima aventura.</p>
        </header>
        {submitted ? (
          <div className="success-message" role="status"><span aria-hidden="true">✓</span><div><strong>Cadastro realizado!</strong><p>Sua conta foi criada com sucesso. Boas aventuras!</p></div></div>
        ) : (
          <form className="signup-form" onSubmit={handleSubmit}>
            <label>Nome de exibição<span className="input-wrap"><svg aria-hidden="true" viewBox="0 0 24 24"><path d="M20 21a8 8 0 0 0-16 0M12 13a5 5 0 1 0 0-10 5 5 0 0 0 0 10Z" /></svg><input name="name" type="text" placeholder="Como quer ser chamado?" autoComplete="name" required /></span></label>
            <label>E-mail<span className="input-wrap"><svg aria-hidden="true" viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="14" rx="2" /><path d="m3 7 9 6 9-6" /></svg><input name="email" type="email" placeholder="voce@email.com" autoComplete="email" required /></span></label>
            <label>Senha<span className="input-wrap"><svg aria-hidden="true" viewBox="0 0 24 24"><rect x="5" y="10" width="14" height="11" rx="2" /><path d="M8 10V7a4 4 0 0 1 8 0v3" /></svg><input name="password" type={showPassword ? "text" : "password"} placeholder="No mínimo 8 caracteres" minLength={8} autoComplete="new-password" value={password} onChange={(event) => setPassword(event.target.value)} required /><button className="visibility-button" type="button" onClick={() => setShowPassword(!showPassword)} aria-label={showPassword ? "Ocultar senha" : "Mostrar senha"}>{showPassword ? "Ocultar" : "Mostrar"}</button></span></label>
            <label>Confirmar senha<span className={`input-wrap ${!passwordsMatch ? "input-error" : ""}`}><svg aria-hidden="true" viewBox="0 0 24 24"><rect x="5" y="10" width="14" height="11" rx="2" /><path d="M8 10V7a4 4 0 0 1 8 0v3" /></svg><input name="confirmPassword" type={showPassword ? "text" : "password"} placeholder="Repita sua senha" minLength={8} autoComplete="new-password" value={confirmPassword} onChange={(event) => setConfirmPassword(event.target.value)} required /></span>{!passwordsMatch && <small className="error-text">As senhas não coincidem.</small>}</label>
            <label className="terms"><input type="checkbox" required /><span>Concordo com os <a href="#termos">Termos de Uso</a> e a <a href="#privacidade">Política de Privacidade</a>.</span></label>
            <button className="submit-button" type="submit">Criar minha conta <span aria-hidden="true">→</span></button>
          </form>
        )}
        <p className="login-link">Já faz parte da comunidade? <a href="#entrar">Entrar na conta</a></p>
      </section>
      <p className="page-footer">© 2026 RPG Space · Feito para grandes histórias</p>
    </main>
  );
}
