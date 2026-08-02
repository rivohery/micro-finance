import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, WritableSignal, inject, signal } from '@angular/core';
import { State } from '../../shared/models/state.model';
import { InterestRateSummaryResp } from '../models/interest-rate-trace.model';
import { HandleErrorService } from '../../shared/service/handle-error.service';
import { catchError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class InterestRateTraceService extends HandleErrorService {
  interestRateUrl: string = `/api/v1/interest-rate-trace`;
  http = inject(HttpClient);

  private getInterestRateSummaryRespSignal: WritableSignal<
    State<InterestRateSummaryResp, string>
  > = signal(
    State.builder<InterestRateSummaryResp, string>().forInit().build()
  );
  readonly getInterestRateSummaryRespState$ =
    this.getInterestRateSummaryRespSignal.asReadonly();

  initGetInterestRateSummaryState(): void {
    this.getInterestRateSummaryRespSignal.set(
      State.builder<InterestRateSummaryResp, string>().forInit().build()
    );
  }

  getInterestRateSummaryResp(
    month: string = '',
    page: number = 0,
    size: number = 6
  ): void {
    this.initGetInterestRateSummaryState();
    this.http
      .get<InterestRateSummaryResp>(`${this.interestRateUrl}`, {
        params: new HttpParams()
          .append('month', month)
          .append('page', page)
          .append('size', size),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) => {
          this.getInterestRateSummaryRespSignal.set(
            State.builder<InterestRateSummaryResp, string>()
              .forSuccess(resp)
              .build()
          );
        },
        error: (err) => {
          this.getInterestRateSummaryRespSignal.set(
            State.builder<InterestRateSummaryResp, string>()
              .forError(err.message)
              .build()
          );
        },
      });
  }
}
