import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { OrdreBourseService } from './ordre-bourse.service';
import { OrdreBourseDto } from './ordre-bourse.model';

describe('OrdreBourseService', () => {
  let service: OrdreBourseService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(OrdreBourseService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('devrait charger la liste des ordres sans filtre', () => {
    const fakeOrdres: OrdreBourseDto[] = [
      {
        id: 1, codeClient: 'CLI-0001', codeIsin: 'FR0000131104',
        sens: 'ACHAT', quantite: 10, coursLimite: 65.50, statut: 'RECU',
        fraisPercus: 0, agiosPercus: 0,
        dateCreation: '2026-05-20T09:15:00', dateExecution: null
      }
    ];

    service.lister().subscribe((ordres) => {
      expect(ordres.length).toBe(1);
      expect(ordres[0].codeClient).toBe('CLI-0001');
    });

    const req = httpMock.expectOne('/api/v1/ordres-bourse');
    expect(req.request.method).toBe('GET');
    req.flush(fakeOrdres);
  });

  it('devrait passer le filtre codeClient en query parameter', () => {
    service.lister('CLI-0002').subscribe();

    const req = httpMock.expectOne(
      (r) => r.url === '/api/v1/ordres-bourse' && r.params.get('codeClient') === 'CLI-0002'
    );
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('devrait POST vers /executer pour exécuter un ordre', () => {
    service.executer(42).subscribe();

    const req = httpMock.expectOne('/api/v1/ordres-bourse/42/executer');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 42, statut: 'EXECUTE' });
  });
});
