import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AvoirSalarialService } from './avoir-salarial.service';
import { AvoirSalarialDto } from './avoir-salarial.model';

@Component({
  selector: 'cat-liste-avoirs',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe, DatePipe],
  template: `
    <h2>Avoirs salariaux</h2>

    <div class="filtre">
      <label for="codeBen">Code bénéficiaire :</label>
      <input id="codeBen" type="text" [(ngModel)]="filtreBeneficiaire" placeholder="ex : BEN-0001" />
      <button type="button" (click)="rafraichir()">Rechercher</button>
    </div>

    @if (chargement()) { <p>Chargement…</p> }
    @if (erreur(); as e) { <p class="erreur">{{ e }}</p> }

    @if (!chargement() && avoirs().length > 0) {
      <table>
        <thead>
          <tr>
            <th>Bénéficiaire</th>
            <th>Entreprise</th>
            <th>Fonds</th>
            <th>Montant investi</th>
            <th>Date valeur</th>
            <th>Statut</th>
          </tr>
        </thead>
        <tbody>
          @for (a of avoirs(); track a.id) {
            <tr>
              <td>{{ a.codeBeneficiaire }}</td>
              <td>{{ a.idEntreprise }}</td>
              <td>{{ a.codeFonds }}</td>
              <td>{{ a.montantInvesti | currency:'EUR' }}</td>
              <td>{{ a.dateValeur | date:'shortDate' }}</td>
              <td>{{ a.statut }}</td>
            </tr>
          }
        </tbody>
      </table>
    }
    @if (!chargement() && avoirs().length === 0 && !erreur()) {
      <p>Aucun avoir trouvé.</p>
    }
  `,
  styles: [`
    .filtre {
      margin: 12px 0 16px;
      display: flex;
      gap: 8px;
      align-items: center;
    }
    .filtre input {
      padding: 6px 10px;
      border: 1px solid #c0c0c0;
      border-radius: 4px;
    }
    .filtre button {
      padding: 6px 12px;
      background: #006a4e;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    .erreur { color: #b00020; font-weight: 600; }
  `]
})
export class ListeAvoirsComponent implements OnInit {

  private readonly service = inject(AvoirSalarialService);

  readonly avoirs = signal<AvoirSalarialDto[]>([]);
  readonly chargement = signal<boolean>(false);
  readonly erreur = signal<string | null>(null);
  filtreBeneficiaire = '';

  ngOnInit(): void {
    this.rafraichir();
  }

  rafraichir(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.service.lister(this.filtreBeneficiaire).subscribe({
      next: (data) => {
        this.avoirs.set(data);
        this.chargement.set(false);
      },
      error: (err) => {
        this.erreur.set('Erreur de chargement : ' + err.message);
        this.chargement.set(false);
      }
    });
  }
}
