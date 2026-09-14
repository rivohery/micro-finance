import { Injectable, inject } from '@angular/core';
import { HandleErrorService } from '../../../core/services/handle-error.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import { PageResponse } from '../../../shared/models/shared.model';
import { AccountStatusHistoryResponse } from '../model/account.model';

@Injectable({
  providedIn: 'root',
})
export class AccountLifeCycleService extends HandleErrorService {
  accountLifeCycleUrl: string = '/api/v1/account-history-status';
  http = inject(HttpClient);

  findAllByAccountId(
    accountId: string,
    page: number = 0,
    size: number = 2
  ): Observable<PageResponse<AccountStatusHistoryResponse>> {
    return this.http
      .get<PageResponse<AccountStatusHistoryResponse>>(
        `${this.accountLifeCycleUrl}/${accountId}`,
        {
          params: new HttpParams().append('page', page).append('size', size),
        }
      )
      .pipe(catchError((err) => this.handleError(err)));
  }
}
