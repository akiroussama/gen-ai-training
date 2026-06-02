import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AvoirSalarialDto } from './avoir-salarial.model';

@Injectable({ providedIn: 'root' })
export class AvoirSalarialService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/avoirs-salariaux';

  lister(codeBeneficiaire?: string): Observable<AvoirSalarialDto[]> {
    let params = new HttpParams();
    if (codeBeneficiaire && codeBeneficiaire.length > 0) {
      params = params.set('codeBeneficiaire', codeBeneficiaire);
    }
    return this.http.get<AvoirSalarialDto[]>(this.apiUrl, { params });
  }

  arbitrer(id: number, fondsCible: string, montant: number): Observable<AvoirSalarialDto> {
    const params = new HttpParams()
      .set('fondsCible', fondsCible)
      .set('montant', montant.toString());
    return this.http.post<AvoirSalarialDto>(`${this.apiUrl}/${id}/arbitrer`, null, { params });
  }
}
