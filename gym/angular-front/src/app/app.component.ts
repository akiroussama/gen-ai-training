import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { NavBarComponent } from './common/nav-bar.component';

@Component({
  selector: 'cat-root',
  standalone: true,
  imports: [RouterOutlet, NavBarComponent],
  template: `
    <h1>Titres — POC front Angular</h1>
    <cat-nav-bar></cat-nav-bar>
    <main>
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`
    main {
      margin-top: 16px;
    }
  `]
})
export class AppComponent {
}
