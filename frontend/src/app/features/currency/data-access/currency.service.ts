import { Injectable, WritableSignal, inject, signal } from '@angular/core';
import { HandleErrorService } from '../../../core/services/handle-error.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {
  CreateCurrencyRequest,
  CurrencyResponse,
  UpdateCurrencyRequest,
} from '../model/currency.model';
import { Observable, catchError, map, of, startWith, tap } from 'rxjs';
import {
  GlobalResponse,
  LoadingState,
} from '../../../shared/models/shared.model';
import { error } from 'node:console';

@Injectable({
  providedIn: 'root',
})
export class CurrencyService extends HandleErrorService {
  adminCurrencyUrl: string = `/api/v1/admin/currencies`;
  employeCurrencyUrl: string = `/api/v1/employee/currencies`;
  http = inject(HttpClient);

  private currencies: WritableSignal<CurrencyResponse[]> = signal([]);
  currencies$ = this.currencies.asReadonly();

  findAll(): Observable<LoadingState<CurrencyResponse[]>> {
    if (this.currencies$().length === 0) {
      return this.http.get<CurrencyResponse[]>(this.adminCurrencyUrl).pipe(
        map((res) => {
          this.currencies.set(res);
          return { loading: false, error: '', data: this.currencies$() };
        }),
        catchError((err) =>
          of({ loading: false, error: err.message, data: [] })
        ),
        startWith({ loading: true, error: '', data: [] })
      );
    }
    return of({ loading: false, error: '', data: this.currencies$() });
  }

  create(request: CreateCurrencyRequest): Observable<CurrencyResponse> {
    return this.http
      .post<CurrencyResponse>(this.adminCurrencyUrl, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        tap((res) =>
          this.currencies.update((currencies) => [res, ...currencies])
        ),
        catchError((err) => this.handleError(err))
      );
  }

  delete(currrencyId: string): Observable<GlobalResponse> {
    return this.http
      .delete<GlobalResponse>(`${this.adminCurrencyUrl}/${currrencyId}`)
      .pipe(
        tap((resp: GlobalResponse) => {
          this.currencies.update((current) =>
            current.filter((curr) => curr.id !== resp.data.currencyId)
          );
        }),
        catchError((err) => this.handleError(err))
      );
  }

  update(request: UpdateCurrencyRequest): Observable<CurrencyResponse> {
    return this.http
      .put<CurrencyResponse>(this.adminCurrencyUrl, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        tap((res: CurrencyResponse) =>
          this.currencies.update((current) =>
            current.map((currency) => {
              return currency.id === res.id ? res : currency;
            })
          )
        ),
        catchError((err) => this.handleError(err))
      );
  }

  fetchEnableCurrency(): Observable<CurrencyResponse[]> {
    return this.http
      .get<CurrencyResponse[]>(
        `${this.employeCurrencyUrl}/fetch-enable-currency`
      )
      .pipe(catchError((err) => this.handleError(err)));
  }
}
