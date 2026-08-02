import { Injectable, WritableSignal, inject, signal } from '@angular/core';
import { HandleErrorService } from '../../shared/service/handle-error.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { GlobalResponse, PageResponse } from '../../shared/models/shared.model';
import { State } from '../../shared/models/state.model';
import {
  CustomerMinResponse,
  CustomerResponse,
  DetailCustomerWithAccount,
  UpdateStatusClientRequest,
} from '../models/clients.model';
import { Observable, catchError, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ClientsService extends HandleErrorService {
  http = inject(HttpClient);
  adminClientUrl: string = '/api/v1/admin/clients';
  employeeClientUrl: string = '/api/v1/employees/clients';

  private createClientSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  readonly createClientState$ = this.createClientSignal.asReadonly();

  private updateClientSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  readonly updateClientState$ = this.updateClientSignal.asReadonly();

  private findAllCustomerSignal: WritableSignal<
    State<PageResponse<CustomerMinResponse>, string>
  > = signal(
    State.builder<PageResponse<CustomerMinResponse>, string>().forInit().build()
  );
  readonly findAllCustomerState$ = this.findAllCustomerSignal.asReadonly();

  private findCustomerDetailsByIdSignal: WritableSignal<
    State<CustomerResponse, string>
  > = signal(State.builder<CustomerResponse, string>().forInit().build());
  readonly findCustomerDetailsByIdState$ =
    this.findCustomerDetailsByIdSignal.asReadonly();

  private updateCustomerStatusSignal: WritableSignal<
    State<GlobalResponse, string>
  > = signal(State.builder<GlobalResponse, string>().forInit().build());
  readonly updateCustomerStatusState$ =
    this.updateCustomerStatusSignal.asReadonly();

  private closeCustomerAccountSignal: WritableSignal<
    State<GlobalResponse, string>
  > = signal(State.builder<GlobalResponse, string>().forInit().build());
  closeCustomerAccountState$ = this.closeCustomerAccountSignal.asReadonly();

  //***********Admin actions**********
  initCreateClientState(): void {
    this.createClientSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }
  initUpdateClientState(): void {
    this.updateClientSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }
  initFindAllCustomerState(): void {
    this.findAllCustomerSignal.set(
      State.builder<PageResponse<CustomerMinResponse>, string>()
        .forInit()
        .build()
    );
  }
  initUpdateCustomerStatusState(): void {
    this.updateCustomerStatusSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }
  initCloseCustomerAccountState(): void {
    this.closeCustomerAccountSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }
  initCustomerDetailsByIdState(): void {
    this.findCustomerDetailsByIdSignal.set(
      State.builder<CustomerResponse, string>().forInit().build()
    );
  }

  createClient(dataRequest: FormData): void {
    this.initCreateClientState();
    this.http
      .post<GlobalResponse>(`${this.adminClientUrl}/create`, dataRequest, {
        responseType: 'json',
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.createClientSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.createClientSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  updateClient(formdata: FormData): void {
    this.initUpdateClientState();
    const customerId = formdata.get('customerId');
    this.http
      .put<GlobalResponse>(
        `${this.adminClientUrl}/update/${customerId}`,
        formdata,
        {
          responseType: 'json',
        }
      )
      .subscribe({
        next: (resp) => {
          this.updateClientSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.updateClientSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  findCustomerDetailsById(dbId: string): void {
    this.initCustomerDetailsByIdState();
    this.http
      .get<CustomerResponse>(`${this.adminClientUrl}/${dbId}`)
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) => {
          this.findCustomerDetailsByIdSignal.set(
            State.builder<CustomerResponse, string>().forSuccess(resp).build()
          );
        },
        error: (err) => {
          this.findCustomerDetailsByIdSignal.set(
            State.builder<CustomerResponse, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  findAllCustomer(
    search: string = '',
    page: number = 0,
    size: number = 6
  ): void {
    this.initFindAllCustomerState();
    this.http
      .get<PageResponse<CustomerMinResponse>>(
        `${this.adminClientUrl}/find-all`,
        {
          params: new HttpParams()
            .append('search', search)
            .append('page', page)
            .append('size', size),
        }
      )
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.findAllCustomerSignal.set(
            State.builder<PageResponse<CustomerMinResponse>, string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.findAllCustomerSignal.set(
            State.builder<PageResponse<CustomerMinResponse>, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  updateCustomerStatus(request: UpdateStatusClientRequest): void {
    this.initUpdateCustomerStatusState();
    this.http
      .patch<GlobalResponse>(`${this.adminClientUrl}/update-status`, request)
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.updateCustomerStatusSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.updateCustomerStatusSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  closeCustomerAccount(customerId: string): void {
    this.http
      .patch<GlobalResponse>(
        `${this.adminClientUrl}/close-account/${customerId}`,
        {}
      )
      .pipe(
        tap((resp) => console.log(resp)),
        catchError((err) => this.handleError(err))
      )
      .subscribe({
        next: (resp) =>
          this.closeCustomerAccountSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.closeCustomerAccountSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  //*********Employee actions*******
  private findAllEnableCustomersSignal: WritableSignal<
    State<PageResponse<CustomerMinResponse>, string>
  > = signal(
    State.builder<PageResponse<CustomerMinResponse>, string>().forInit().build()
  );
  findAllEnableCustomersState$ = this.findAllEnableCustomersSignal.asReadonly();

  initFindAllEnableCustomersState(): void {
    this.findAllEnableCustomersSignal.set(
      State.builder<PageResponse<CustomerMinResponse>, string>()
        .forInit()
        .build()
    );
  }

  findAllEnableCustomers(
    search: string = '',
    page: number = 0,
    size: number = 6
  ): void {
    this.initFindAllEnableCustomersState();
    this.http
      .get<PageResponse<CustomerMinResponse>>(
        `${this.employeeClientUrl}/find-all-enable`,
        {
          params: new HttpParams()
            .append('search', search)
            .append('page', page)
            .append('size', size),
        }
      )
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.findAllEnableCustomersSignal.set(
            State.builder<PageResponse<CustomerMinResponse>, string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.findAllEnableCustomersSignal.set(
            State.builder<PageResponse<CustomerMinResponse>, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  findDetailsClientWithAccount(
    customerId: string
  ): Observable<DetailCustomerWithAccount> {
    return this.http
      .get<DetailCustomerWithAccount>(
        `${this.employeeClientUrl}/find-details-with-accounts/${customerId}`
      )
      .pipe(catchError((err) => this.handleError(err)));
  }
}
