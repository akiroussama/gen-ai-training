import { Routes } from '@angular/router';

import { ListeOrdresComponent } from './valeurs-mobilieres/liste-ordres.component';
import { ListeAvoirsComponent } from './epargne-salariale/liste-avoirs.component';
import { PlaceholderComponent } from './common/placeholder.component';
import { LoginComponent } from './auth/login.component';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/ordres', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  {
    path: 'ordres',
    component: ListeOrdresComponent,
    canActivate: [authGuard],
    title: 'Ordres de bourse'
  },
  {
    path: 'avoirs',
    component: ListeAvoirsComponent,
    canActivate: [authGuard],
    title: 'Avoirs salariaux'
  },
  {
    path: 'dats',
    component: PlaceholderComponent,
    canActivate: [authGuard],
    data: { titre: 'Dépôts à terme', message: 'Liste DAT à venir session 5' },
    title: 'Dépôts à terme'
  },
  {
    path: 'mobilite',
    component: PlaceholderComponent,
    canActivate: [authGuard],
    data: { titre: 'Mobilité bancaire', message: 'Workflow Facilit à venir session 5' },
    title: 'Mobilité bancaire'
  },
  { path: '**', redirectTo: '/ordres' }
];
