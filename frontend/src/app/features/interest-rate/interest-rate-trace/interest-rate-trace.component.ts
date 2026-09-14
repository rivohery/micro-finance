import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { InterestRateTraceService } from '../interest-rate-trace.service';
import { PageResponse } from '../../../shared/models/shared.model';
import { InterestRateTraceResponse } from '../interest-rate-trace.model';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../shared/components/message-box/message-box.component';

export type MonthFilter = {
  value: string;
  label: string;
};

@Component({
  selector: 'app-interest-rate-trace',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    PaginationComponent,
    LoaderComponent,
    MessageBoxComponent,
  ],
  templateUrl: './interest-rate-trace.component.html',
  styleUrl: './interest-rate-trace.component.css',
})
export class InterestRateTraceComponent implements OnInit {
  interestRateTraceService = inject(InterestRateTraceService);
  availableMonths: MonthFilter[] = [
    { value: 'JANUARY', label: 'January' },
    { value: 'FEBRUARY', label: 'February' },
    { value: 'MARCH', label: 'March' },
    { value: 'APRIL', label: 'April' },
    { value: 'MAY', label: 'May' },
    { value: 'JUNE', label: 'June' },
    { value: 'JULY', label: 'July' },
    { value: 'AUGUST', label: 'August' },
    { value: 'SEPTEMBER', label: 'September' },
    { value: 'OCTOBER', label: 'October' },
    { value: 'NOVEMBER', label: 'November' },
    { value: 'DECEMBER', label: 'December' },
  ];
  loading = signal<boolean>(false);
  errorMsg = signal<string>('');

  selectedMonth = signal<string>('');

  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  interestRateTraceList = signal<InterestRateTraceResponse[]>([]);
  totalElements = signal<number>(0);

  totalInterestRateMonthly = signal<number>(0.0);

  constructor() {
    const actualMonth = this.availableMonths[new Date().getMonth()].value;
    this.selectedMonth.set(actualMonth);
    effect(() => this.handleGetInterestRateSummaryState());
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.interestRateTraceService.initGetInterestRateSummaryState();
    this.loadInterestRateTrace();
  }

  private handleGetInterestRateSummaryState(): void {
    const getInterestRateSummaryRespState =
      this.interestRateTraceService.getInterestRateSummaryRespState$();
    if (
      getInterestRateSummaryRespState.status === 'OK' &&
      getInterestRateSummaryRespState.value
    ) {
      this.totalPages.set(
        getInterestRateSummaryRespState.value?.interestRateTraces.totalPages ||
          0
      );
      this.currentPage.set(
        getInterestRateSummaryRespState.value?.interestRateTraces.number || 0
      );
      this.interestRateTraceList.set(
        getInterestRateSummaryRespState.value?.interestRateTraces.content || []
      );
      this.totalElements.set(
        getInterestRateSummaryRespState.value?.interestRateTraces
          .totalElements || 0
      );
      this.totalInterestRateMonthly.set(
        getInterestRateSummaryRespState.value?.totalInterestRateMonthly || 0.0
      );
      this.loading.set(false);
    }
    if (getInterestRateSummaryRespState.status === 'ERROR') {
      this.loading.set(false);
      this.errorMsg.set(getInterestRateSummaryRespState.error || '');
    }
  }

  private loadInterestRateTrace(): void {
    this.interestRateTraceService.getInterestRateSummaryResp(
      this.selectedMonth(),
      this.currentPage()
    );
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.loadInterestRateTrace();
  }

  onChange(month: string): void {
    console.log(month);
    this.selectedMonth.set(month);
    this.loadInterestRateTrace();
  }

  closeErrorMsg(resp: boolean): void {
    this.errorMsg.set('');
  }
}
