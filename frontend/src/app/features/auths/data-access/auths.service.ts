import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { LoginRequest } from '../models/auths.model';
import { Observable, catchError, tap } from 'rxjs';
import { GlobalResponse } from '../../../shared/models/shared.model';
import { HandleErrorService } from '../../../core/services/handle-error.service';
//import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthsService extends HandleErrorService {
  //private authsUrl: string = `${environment.API_URL}/auths`;
  private authsUrl: string = `/api/v1/auths`;

  http = inject(HttpClient);

  login(request: LoginRequest): Observable<GlobalResponse> {
    return this.http
      .post<GlobalResponse>(`${this.authsUrl}/login`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        tap((resp) => console.log(resp)),
        catchError((err) => this.handleError(err))
      );
  }

  logout(): Observable<GlobalResponse> {
    return this.http
      .post<GlobalResponse>(
        `${this.authsUrl}/logout`,
        {},
        {
          headers: new HttpHeaders(),
        }
      )
      .pipe(catchError((err) => this.handleError(err)));
  }
}
