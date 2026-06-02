import { Injectable, signal } from '@angular/core';

/**
 * AuthService FICTIF — usage formation uniquement.
 *
 * DETTE TECH INTENTIONNELLE (D54) :
 * - Authentification basée sur un boolean localStorage
 * - Aucune validation serveur (login/password ignorés)
 * - Aucun token JWT, aucune expiration
 * - Vulnérable à XSS (localStorage lisible par JS tiers)
 *
 * À NE PAS REPRODUIRE en production. Sert l'atelier J3 vibe checks
 * et l'atelier O27 détection dette tech.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {

  private static readonly STORAGE_KEY = 'cat.poc.auth';

  private readonly connecte = signal<boolean>(this.lireEtatInitial());

  readonly estConnecte = this.connecte.asReadonly();

  connecter(login: string, password: string): boolean {
    if (login.trim().length === 0 || password.length === 0) {
      return false;
    }
    localStorage.setItem(AuthService.STORAGE_KEY, login);
    this.connecte.set(true);
    return true;
  }

  deconnecter(): void {
    localStorage.removeItem(AuthService.STORAGE_KEY);
    this.connecte.set(false);
  }

  private lireEtatInitial(): boolean {
    if (typeof localStorage === 'undefined') {
      return false;
    }
    return localStorage.getItem(AuthService.STORAGE_KEY) !== null;
  }
}
