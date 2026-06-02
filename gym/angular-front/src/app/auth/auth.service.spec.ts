import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';

describe('AuthService', () => {

  let service: AuthService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthService);
  });

  it('est déconnecté par défaut', () => {
    expect(service.estConnecte()).toBeFalse();
  });

  it('connecter() accepte login/password non vides et met estConnecte à true', () => {
    const ok = service.connecter('user', 'pwd');
    expect(ok).toBeTrue();
    expect(service.estConnecte()).toBeTrue();
  });

  it('connecter() refuse login vide', () => {
    const ok = service.connecter('   ', 'pwd');
    expect(ok).toBeFalse();
    expect(service.estConnecte()).toBeFalse();
  });

  it('deconnecter() vide localStorage et reset signal', () => {
    service.connecter('user', 'pwd');
    service.deconnecter();
    expect(service.estConnecte()).toBeFalse();
    expect(localStorage.getItem('cat.poc.auth')).toBeNull();
  });
});
