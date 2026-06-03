import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ListeOrdresComponent } from './liste-ordres.component';
import { OrdreBourseDto } from './ordre-bourse.model';

describe('ListeOrdresComponent', () => {
  let fixture: ComponentFixture<ListeOrdresComponent>;
  let component: ListeOrdresComponent;
  let httpMock: HttpTestingController;

  const fakeOrdres: OrdreBourseDto[] = [
    {
      id: 1, codeClient: 'CLI-0001', codeIsin: 'FR0000131104',
      sens: 'ACHAT', quantite: 10, coursLimite: 65.50, statut: 'RECU',
      fraisPercus: 0, agiosPercus: 0,
      dateCreation: '2026-05-20T09:15:00', dateExecution: null
    },
    {
      id: 2, codeClient: 'CLI-0001', codeIsin: 'FR0000120271',
      sens: 'VENTE', quantite: 5, coursLimite: 175.20, statut: 'EXECUTE',
      fraisPercus: 12.50, agiosPercus: 0,
      dateCreation: '2026-05-21T10:30:00', dateExecution: '2026-05-21T10:35:00'
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListeOrdresComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListeOrdresComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('devrait se créer et charger les ordres au ngOnInit', () => {
    fixture.detectChanges();

    const req = httpMock.expectOne('/api/v1/ordres-bourse');
    req.flush(fakeOrdres);

    expect(component).toBeTruthy();
    expect(component.ordres().length).toBe(2);
  });

  it('rafraichir() doit appliquer le filtre client', () => {
    fixture.detectChanges();
    httpMock.expectOne('/api/v1/ordres-bourse').flush([]);

    component.filtreClient = 'CLI-0001';
    component.rafraichir();

    const req = httpMock.expectOne(
      (r) => r.url === '/api/v1/ordres-bourse' && r.params.get('codeClient') === 'CLI-0001'
    );
    req.flush(fakeOrdres);

    expect(component.ordres().length).toBe(2);
    expect(component.erreur()).toBeNull();
  });

  it('executer() doit POST sur /executer puis rafraîchir', () => {
    fixture.detectChanges();
    httpMock.expectOne('/api/v1/ordres-bourse').flush(fakeOrdres);

    component.executer(fakeOrdres[0]);

    const reqExec = httpMock.expectOne('/api/v1/ordres-bourse/1/executer');
    expect(reqExec.request.method).toBe('POST');
    reqExec.flush({ ...fakeOrdres[0], statut: 'EXECUTE' });

    const reqRefresh = httpMock.expectOne('/api/v1/ordres-bourse');
    reqRefresh.flush(fakeOrdres);
  });
});
