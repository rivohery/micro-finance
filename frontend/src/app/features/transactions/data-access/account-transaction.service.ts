import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { HandleErrorService } from '../../../core/services/handle-error.service';
import { GlobalResponse } from '../../../shared/models/shared.model';
import {
  TransactionRequest,
  TransactionResponse,
  TransfertRequest,
} from '../model/transactions.model';

@Injectable({
  providedIn: 'root',
})
export class AccountTransactionService extends HandleErrorService {
  http = inject(HttpClient);
  accountTransactionUrl: string = '/api/v1/operations';

  deposit(request: TransactionRequest): Observable<TransactionResponse> {
    return this.http
      .post<TransactionResponse>(
        `${this.accountTransactionUrl}/deposit`,
        request,
        {
          headers: new HttpHeaders().set('Content-Type', 'application/json'),
        }
      )
      .pipe(catchError((err) => this.handleError(err)));
  }

  withdraw(request: TransactionRequest): Observable<TransactionResponse> {
    return this.http
      .post<TransactionResponse>(
        `${this.accountTransactionUrl}/withdraw`,
        request,
        {
          headers: new HttpHeaders().set('Content-Type', 'application/json'),
        }
      )
      .pipe(catchError((err) => this.handleError(err)));
  }

  transfert(request: TransfertRequest): Observable<GlobalResponse> {
    return this.http
      .post<GlobalResponse>(
        `${this.accountTransactionUrl}/transfert`,
        request,
        {
          headers: new HttpHeaders().set('Content-Type', 'application/json'),
        }
      )
      .pipe(catchError((err) => this.handleError(err)));
  }
}
