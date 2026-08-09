import { Injectable, WritableSignal, inject, signal } from '@angular/core';
import { HandleErrorService } from '../../../core/services/handle-error.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { PageResponse } from '../../../shared/models/shared.model';
import { State } from '../../../shared/models/state.model';
import { TransactionResponse } from '../model/transactions.model';

@Injectable({
  providedIn: 'root',
})
export class TransactionService extends HandleErrorService {
  transactionUrl: string = '/api/v1/transactions';
  http = inject(HttpClient);

  private findAllByCreatedDateSignal: WritableSignal<
    State<PageResponse<TransactionResponse>, string>
  > = signal(
    State.builder<PageResponse<TransactionResponse>, string>().forInit().build()
  );
  readonly findAllByCreatedDateState$ =
    this.findAllByCreatedDateSignal.asReadonly();

  initfindAllByCreatedDateState(): void {
    this.findAllByCreatedDateSignal.set(
      State.builder<PageResponse<TransactionResponse>, string>()
        .forInit()
        .build()
    );
  }

  findAllByAccountNumber(
    accountNumber: string,
    page: number = 0,
    size: number = 2
  ): Observable<PageResponse<TransactionResponse>> {
    return this.http
      .get<PageResponse<TransactionResponse>>(
        `${this.transactionUrl}/belong/${accountNumber}`,
        {
          params: new HttpParams().append('page', page).append('size', size),
        }
      )
      .pipe(catchError((err) => this.handleError(err)));
  }

  findAllByCreatedDate(
    createdDate: string,
    page: number = 0,
    size: number = 10
  ): void {
    this.initfindAllByCreatedDateState();
    this.http
      .get<PageResponse<TransactionResponse>>(`${this.transactionUrl}`, {
        params: new HttpParams()
          .append('createdDate', createdDate)
          .append('page', page)
          .append('size', size),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) => {
          this.findAllByCreatedDateSignal.set(
            State.builder<PageResponse<TransactionResponse>, string>()
              .forSuccess(resp)
              .build()
          );
        },
        error: (err) => {
          this.findAllByCreatedDateSignal.set(
            State.builder<PageResponse<TransactionResponse>, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }

  exportPdf(createdDate: string = ''): Observable<Blob> {
    return this.http.get(`${this.transactionUrl}/export/pdf`, {
      params: new HttpParams().append('createdDate', createdDate),
      responseType: 'blob',
    });
  }
}
