import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { AvoirSalarialService } from './avoir-salarial.service';

describe('AvoirSalarialService', () => {
  let service: AvoirSalarialService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AvoirSalarialService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('lister() sans filtre charge tous les avoirs', () => {
    service.lister().subscribe((avoirs) => expect(avoirs.length).toBe(0));

    const req = httpMock.expectOne('/api/v1/avoirs-salariaux');
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('lister() avec filtre passe codeBeneficiaire en param', () => {
    service.lister('BEN-0001').subscribe();

    const req = httpMock.expectOne(
      (r) => r.url === '/api/v1/avoirs-salariaux' &&
             r.params.get('codeBeneficiaire') === 'BEN-0001'
    );
    req.flush([]);
  });

  it('arbitrer() POST avec fondsCible et montant en query params', () => {
    service.arbitrer(1, 'FONDS-ACTIONS', 500).subscribe();

    const req = httpMock.expectOne(
      (r) => r.url === '/api/v1/avoirs-salariaux/1/arbitrer' &&
             r.params.get('fondsCible') === 'FONDS-ACTIONS' &&
             r.params.get('montant') === '500'
    );
    expect(req.request.method).toBe('POST');
    req.flush({});
  });
});
