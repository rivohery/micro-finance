import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, WritableSignal, inject, signal } from '@angular/core';
import {
  AccountTypeRequest,
  AccountTypeResponse,
} from '../model/account-type.model';
import { Observable, catchError, map, of, startWith, tap } from 'rxjs';
import { HandleErrorService } from '../../../core/services/handle-error.service';
import {
  GlobalResponse,
  LoadingState,
} from '../../../shared/models/shared.model';

@Injectable({
  providedIn: 'root',
})
export class AccountTypeService extends HandleErrorService {
  accountTypeUrl: string = '/api/v1/account-type';
  http = inject(HttpClient);

  private accountTypeList: WritableSignal<AccountTypeResponse[]> = signal([]);
  accountTypeList$ = this.accountTypeList.asReadonly();

  accountTypeUpdated = signal<AccountTypeResponse | null>(null);

  findAll(): Observable<LoadingState<AccountTypeResponse[]>> {
    return this.http.get<AccountTypeResponse[]>(this.accountTypeUrl).pipe(
      map((resp: AccountTypeResponse[]) => {
        this.accountTypeList.set(resp);
        return {
          loading: false,
          error: '',
          data: this.accountTypeList$(),
        };
      }),
      catchError((err) => of({ loading: false, error: err.message, data: [] })),
      startWith({ loading: true, error: '', data: [] })
    );
  }

  create(request: AccountTypeRequest): Observable<AccountTypeResponse> {
    return this.http
      .post<AccountTypeResponse>(this.accountTypeUrl, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(
        tap((resp) =>
          this.accountTypeList.update((accountTypeList) => [
            resp,
            ...accountTypeList,
          ])
        ),
        catchError((err) => this.handleError(err))
      );
  }

  update(
    request: AccountTypeRequest,
    accountTypeId: string
  ): Observable<AccountTypeResponse> {
    return this.http
      .put<AccountTypeResponse>(
        `${this.accountTypeUrl}/${accountTypeId}`,
        request,
        {
          headers: new HttpHeaders().set('Content-Type', 'application/json'),
        }
      )
      .pipe(
        tap((resp) =>
          this.accountTypeList.update((accountTypeList) =>
            accountTypeList.map((item) => {
              return item.id === resp.id ? resp : item;
            })
          )
        ),
        catchError((err) => this.handleError(err))
      );
  }

  delete(accountTypeId: string): Observable<GlobalResponse> {
    return this.http
      .delete<GlobalResponse>(`${this.accountTypeUrl}/${accountTypeId}`)
      .pipe(
        tap((resp) =>
          this.accountTypeList.update((accountTypeList) =>
            accountTypeList.filter(
              (item) => item.id !== resp.data.accountTypeId
            )
          )
        ),
        catchError((err) => this.handleError(err))
      );
  }

  editAccountType(accountType: AccountTypeResponse): void {
    this.accountTypeUpdated.set(accountType);
  }

  resetAccountTypeUpdated(): void {
    this.accountTypeUpdated.set(null);
  }
}
