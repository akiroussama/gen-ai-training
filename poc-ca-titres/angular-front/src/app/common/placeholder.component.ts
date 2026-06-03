import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'cat-placeholder',
  standalone: true,
  template: `
    <h2>{{ titre }}</h2>
    <p class="msg">{{ message }}</p>
    <p>
      Ce module sera enrichi dans une session future du POC.
      Pour cette session, l'API backend Spring est néanmoins
      disponible et utilisable directement via curl, Postman ou
      depuis votre <code>copilot-instructions.md</code>.
    </p>
  `,
  styles: [`
    .msg {
      padding: 12px;
      background: #fff7e6;
      border-left: 4px solid #f5a623;
      border-radius: 4px;
    }
    code {
      background: #f0f0f0;
      padding: 2px 6px;
      border-radius: 3px;
    }
  `]
})
export class PlaceholderComponent {

  private readonly route = inject(ActivatedRoute);

  protected readonly titre: string = this.route.snapshot.data['titre'] ?? 'Module';
  protected readonly message: string = this.route.snapshot.data['message'] ?? 'À venir';
}
