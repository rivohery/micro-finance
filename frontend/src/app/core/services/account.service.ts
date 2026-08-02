import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable, WritableSignal, inject, signal } from '@angular/core';
import { State } from '../../shared/models/state.model';
import { GlobalResponse, PageResponse } from '../../shared/models/shared.model';
import {
  AccountLifeCycleRequest,
  AccountResponse,
  CreateAccountRequest,
} from '../models/account.model';
import { Observable, catchError, map, of, startWith } from 'rxjs';
import { HandleErrorService } from '../../shared/service/handle-error.service';

@Injectable({
  providedIn: 'root',
})
export class AccountService extends HandleErrorService {
  accountUrl: string = '/api/v1/accounts';
  http = inject(HttpClient);

  private findByAccountNumberSignal: WritableSignal<
    State<AccountResponse, string>
  > = signal(State.builder<AccountResponse, string>().forInit().build());
  findByAccountNumberState$ = this.findByAccountNumberSignal.asReadonly();

  private activateAccountSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  readonly activateAccountState$ = this.activateAccountSignal.asReadonly();

  private findAllAccountSignal: WritableSignal<
    State<PageResponse<AccountResponse>, string>
  > = signal(
    State.builder<PageResponse<AccountResponse>, string>().forInit().build()
  );
  readonly findAllAccountState$ = this.findAllAccountSignal.asReadonly();

  private suspendAccountSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  readonly suspendAccountState$ = this.suspendAccountSignal.asReadonly();

  private closeAccountSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  readonly closeAccountState$ = this.closeAccountSignal.asReadonly();

  createAccount(request: CreateAccountRequest): Observable<GlobalResponse> {
    return this.http
      .post<GlobalResponse>(`${this.accountUrl}/create`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(catchError((err) => this.handleError(err)));
  }

  initFindByAccountNumberState(): void {
    this.findByAccountNumberSignal.set(
      State.builder<AccountResponse, string>().forInit().build()
    );
  }

  initActivateAccountState(): void {
    this.activateAccountSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }
  initSuspendAccountState(): void {
    this.suspendAccountSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }
  initCloseAccountState(): void {
    this.closeAccountSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  findByAccountNumber(accountNumber: string): void {
    this.http
      .get<AccountResponse>(
        `${this.accountUrl}/account-number/${accountNumber}`
      )
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) => {
          this.findByAccountNumberSignal.set(
            State.builder<AccountResponse, string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.findByAccountNumberSignal.set(
            State.builder<AccountResponse, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  activateAccount(request: AccountLifeCycleRequest): void {
    this.initActivateAccountState();
    this.http
      .patch<GlobalResponse>(`${this.accountUrl}/activate-account`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) => {
          this.activateAccountSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.activateAccountSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  suspendAccount(request: AccountLifeCycleRequest): void {
    this.initSuspendAccountState();
    this.http
      .patch<GlobalResponse>(`${this.accountUrl}/suspend-account`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) => {
          this.suspendAccountSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.suspendAccountSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  closeAccount(request: AccountLifeCycleRequest): void {
    this.initCloseAccountState();
    this.http
      .patch<GlobalResponse>(`${this.accountUrl}/close-account`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) => {
          this.closeAccountSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.closeAccountSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  initFindAllAccountState(): void {
    this.findAllAccountSignal.set(
      State.builder<PageResponse<AccountResponse>, string>().forInit().build()
    );
  }

  findAllAccountBySearch(
    search: string = '',
    page: number = 0,
    size: number = 10
  ): void {
    this.initFindAllAccountState();
    this.http
      .get<PageResponse<AccountResponse>>(`${this.accountUrl}`, {
        params: new HttpParams()
          .append('search', search)
          .append('page', page)
          .append('size', size),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) => {
          this.findAllAccountSignal.set(
            State.builder<PageResponse<AccountResponse>, string>()
              .forSuccess(resp)
              .build()
          );
        },
        error: (err) => {
          this.findAllAccountSignal.set(
            State.builder<PageResponse<AccountResponse>, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  findMyAccounts(): Observable<{
    loading: boolean;
    error: string;
    data: AccountResponse[];
  }> {
    return this.http.get<AccountResponse[]>(`${this.accountUrl}/me`).pipe(
      map((resp) => {
        return { loading: false, error: '', data: resp };
      }),
      catchError((err) => of({ loading: false, error: err.message, data: [] })),
      startWith({ loading: true, error: '', data: [] })
    );
  }
}
