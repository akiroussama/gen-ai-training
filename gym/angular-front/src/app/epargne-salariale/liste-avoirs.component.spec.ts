import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ListeAvoirsComponent } from './liste-avoirs.component';
import { AvoirSalarialDto } from './avoir-salarial.model';

describe('ListeAvoirsComponent', () => {
  let fixture: ComponentFixture<ListeAvoirsComponent>;
  let component: ListeAvoirsComponent;
  let httpMock: HttpTestingController;

  const fakeAvoirs: AvoirSalarialDto[] = [
    {
      id: 1, codeBeneficiaire: 'BEN-0001', idEntreprise: 'ENT-FICTIVE-A',
      codeFonds: 'FONDS-MONETAIRE', montantInvesti: 5200.00,
      dateValeur: '2024-09-15', statut: 'DISPONIBLE'
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListeAvoirsComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListeAvoirsComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('se crée et charge les avoirs au ngOnInit', () => {
    fixture.detectChanges();

    const req = httpMock.expectOne('/api/v1/avoirs-salariaux');
    req.flush(fakeAvoirs);

    expect(component).toBeTruthy();
    expect(component.avoirs().length).toBe(1);
    expect(component.avoirs()[0].codeBeneficiaire).toBe('BEN-0001');
  });

  it('rafraichir() avec filtre bénéficiaire passe le param', () => {
    fixture.detectChanges();
    httpMock.expectOne('/api/v1/avoirs-salariaux').flush([]);

    component.filtreBeneficiaire = 'BEN-0001';
    component.rafraichir();

    const req = httpMock.expectOne(
      (r) => r.url === '/api/v1/avoirs-salariaux' &&
             r.params.get('codeBeneficiaire') === 'BEN-0001'
    );
    req.flush(fakeAvoirs);
    expect(component.avoirs().length).toBe(1);
  });
});
