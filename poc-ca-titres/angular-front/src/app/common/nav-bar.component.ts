import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'cat-nav-bar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav>
      <a routerLink="/ordres" routerLinkActive="actif">Ordres de bourse</a>
      <a routerLink="/avoirs" routerLinkActive="actif">Avoirs salariaux</a>
      <a routerLink="/dats" routerLinkActive="actif">DAT</a>
      <a routerLink="/mobilite" routerLinkActive="actif">Mobilité bancaire</a>
      <span class="spacer"></span>
      @if (auth.estConnecte()) {
        <button type="button" (click)="seDeconnecter()">Déconnexion</button>
      }
    </nav>
  `,
  styles: [`
    nav {
      display: flex;
      gap: 12px;
      align-items: center;
      padding: 10px 14px;
      background: #006a4e;
      border-radius: 6px;
    }
    nav a {
      color: white;
      text-decoration: none;
      padding: 6px 12px;
      border-radius: 4px;
    }
    nav a:hover, nav a.actif {
      background: rgba(255, 255, 255, 0.18);
    }
    .spacer { flex: 1; }
    button {
      padding: 6px 12px;
      background: white;
      color: #006a4e;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 600;
    }
  `]
})
export class NavBarComponent {

  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  seDeconnecter(): void {
    this.auth.deconnecter();
    this.router.navigate(['/login']);
  }
}
