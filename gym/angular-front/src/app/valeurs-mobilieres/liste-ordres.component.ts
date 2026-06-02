import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { OrdreBourseService } from './ordre-bourse.service';
import { OrdreBourseDto } from './ordre-bourse.model';

@Component({
  selector: 'cat-liste-ordres',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe, DatePipe],
  templateUrl: './liste-ordres.component.html',
  styleUrls: ['./liste-ordres.component.css']
})
export class ListeOrdresComponent implements OnInit {

  private readonly service = inject(OrdreBourseService);

  readonly ordres = signal<OrdreBourseDto[]>([]);
  readonly chargement = signal<boolean>(false);
  readonly erreur = signal<string | null>(null);
  filtreClient = '';

  ngOnInit(): void {
    this.rafraichir();
  }

  rafraichir(): void {
    this.chargement.set(true);
    this.erreur.set(null);
    this.service.lister(this.filtreClient).subscribe({
      next: (data) => {
        this.ordres.set(data);
        this.chargement.set(false);
      },
      error: (err) => {
        this.erreur.set('Erreur de chargement : ' + err.message);
        this.chargement.set(false);
      }
    });
  }

  executer(ordre: OrdreBourseDto): void {
    this.service.executer(ordre.id).subscribe({
      next: () => this.rafraichir(),
      error: (err) => this.erreur.set('Erreur exécution : ' + err.message)
    });
  }
}
