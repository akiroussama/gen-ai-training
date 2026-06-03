import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { OrdreBourseDto } from './ordre-bourse.model';

@Injectable({ providedIn: 'root' })
export class OrdreBourseService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/ordres-bourse';

  lister(codeClient?: string): Observable<OrdreBourseDto[]> {
    let params = new HttpParams();
    if (codeClient && codeClient.length > 0) {
      params = params.set('codeClient', codeClient);
    }
    return this.http.get<OrdreBourseDto[]>(this.apiUrl, { params });
  }

  detail(id: number): Observable<OrdreBourseDto> {
    return this.http.get<OrdreBourseDto>(`${this.apiUrl}/${id}`);
  }

  executer(id: number): Observable<OrdreBourseDto> {
    return this.http.post<OrdreBourseDto>(`${this.apiUrl}/${id}/executer`, {});
  }
}
