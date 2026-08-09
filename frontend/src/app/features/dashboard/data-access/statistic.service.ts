import { Injectable, WritableSignal, inject, signal } from '@angular/core';
import { HandleErrorService } from '../../../core/services/handle-error.service';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';
import {
  NumberAccountStatistic,
  RegistrationStatistic,
  SoldeAccountStatistic,
} from '../model/statistic.model';
import { State } from '../../../shared/models/state.model';

@Injectable({
  providedIn: 'root',
})
export class StatisticService extends HandleErrorService {
  statisticUrl: string = '/api/v1/statistic';
  http = inject(HttpClient);

  private accountStatisticNumberSignal: WritableSignal<
    State<NumberAccountStatistic[], string>
  > = signal(
    State.builder<NumberAccountStatistic[], string>().forInit().build()
  );
  readonly accountStatisticNumberState$ =
    this.accountStatisticNumberSignal.asReadonly();

  private accountStatisticSoldSignal: WritableSignal<
    State<SoldeAccountStatistic[], string>
  > = signal(
    State.builder<SoldeAccountStatistic[], string>().forInit().build()
  );
  readonly accountStatisticSoldState$ =
    this.accountStatisticSoldSignal.asReadonly();

  private registrationStatisticOfWeekSignal: WritableSignal<
    State<RegistrationStatistic[], string>
  > = signal(
    State.builder<RegistrationStatistic[], string>().forInit().build()
  );
  readonly registrationStatisticState$ =
    this.registrationStatisticOfWeekSignal.asReadonly();

  getNbrTotalOfCustomer(): Observable<number> {
    return this.http
      .get<number>(`${this.statisticUrl}/nbr-total-of-customer`)
      .pipe(catchError((err) => this.handleError(err)));
  }

  getNbrTotalOfAccount(): Observable<number> {
    return this.http
      .get<number>(`${this.statisticUrl}/nbr-total-of-account`)
      .pipe(catchError((err) => this.handleError(err)));
  }

  getSoldeTotalOfAccountInMga(): Observable<number> {
    return this.http
      .get<number>(`${this.statisticUrl}/sold-total-of-account`)
      .pipe(catchError((err) => this.handleError(err)));
  }

  initAccountStatisticNumberState(): void {
    this.accountStatisticNumberSignal.set(
      State.builder<NumberAccountStatistic[], string>().forInit().build()
    );
  }
  initAccountStatisticSoldState(): void {
    this.accountStatisticSoldSignal.set(
      State.builder<SoldeAccountStatistic[], string>().forInit().build()
    );
  }
  initRegistrationStatisticState(): void {
    this.registrationStatisticOfWeekSignal.set(
      State.builder<RegistrationStatistic[], string>().forInit().build()
    );
  }

  getAccountStatisticNumber(): void {
    this.initAccountStatisticNumberState();
    this.http
      .get<NumberAccountStatistic[]>(
        `${this.statisticUrl}/account-number-by-type`
      )
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.accountStatisticNumberSignal.set(
            State.builder<NumberAccountStatistic[], string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.accountStatisticNumberSignal.set(
            State.builder<NumberAccountStatistic[], string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  getAccountStatisticSold(): void {
    this.initAccountStatisticSoldState();
    this.http
      .get<SoldeAccountStatistic[]>(`${this.statisticUrl}/account-sold-by-type`)
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.accountStatisticSoldSignal.set(
            State.builder<SoldeAccountStatistic[], string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.accountStatisticSoldSignal.set(
            State.builder<SoldeAccountStatistic[], string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  getRegistrationStatisticOfWeek(): void {
    this.initRegistrationStatisticState();
    this.http
      .get<RegistrationStatistic[]>(
        `${this.statisticUrl}/registration-customer-statistic`
      )
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.registrationStatisticOfWeekSignal.set(
            State.builder<RegistrationStatistic[], string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.registrationStatisticOfWeekSignal.set(
            State.builder<RegistrationStatistic[], string>()
              .forError(err.message)
              .build()
          ),
      });
  }
}
