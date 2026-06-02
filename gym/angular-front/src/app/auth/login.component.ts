import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthService } from './auth.service';

@Component({
  selector: 'cat-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>Connexion (démo POC)</h2>
    <p class="info">
      Authentification fictive : n'importe quel login + mot de passe
      non vide est accepté. <strong>Ne pas reproduire en prod.</strong>
    </p>

    <form (ngSubmit)="seConnecter()">
      <div class="champ">
        <label for="login">Identifiant</label>
        <input id="login" type="text" [(ngModel)]="login" name="login" required />
      </div>
      <div class="champ">
        <label for="pwd">Mot de passe</label>
        <input id="pwd" type="password" [(ngModel)]="password" name="password" required />
      </div>

      @if (erreur(); as e) { <p class="erreur">{{ e }}</p> }

      <button type="submit">Se connecter</button>
    </form>
  `,
  styles: [`
    form {
      max-width: 360px;
      padding: 20px;
      background: white;
      border-radius: 6px;
      border: 1px solid #e0e0e0;
    }
    .champ {
      margin-bottom: 12px;
      display: flex;
      flex-direction: column;
      gap: 4px;
    }
    .champ input {
      padding: 8px 10px;
      border: 1px solid #c0c0c0;
      border-radius: 4px;
    }
    button {
      padding: 8px 18px;
      background: #006a4e;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 600;
    }
    .info {
      padding: 10px;
      background: #fff7e6;
      border-left: 4px solid #f5a623;
      border-radius: 4px;
      font-size: 0.9em;
    }
    .erreur { color: #b00020; font-weight: 600; }
  `]
})
export class LoginComponent {

  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  login = '';
  password = '';
  readonly erreur = signal<string | null>(null);

  seConnecter(): void {
    this.erreur.set(null);
    if (this.auth.connecter(this.login, this.password)) {
      const redirect = this.route.snapshot.queryParamMap.get('redirect') ?? '/ordres';
      this.router.navigateByUrl(redirect);
    } else {
      this.erreur.set('Identifiant et mot de passe requis');
    }
  }
}
