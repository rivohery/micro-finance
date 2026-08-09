import { PageResponse } from '../../shared/models/shared.model';

export interface InterestRateTraceResponse {
  id: string;
  accountNumber: string;
  interestRate: number;
  currencyCode: string;
  amount: number;
  mgaAmount: number;
  month: string;
  year: string;
}

export interface InterestRateSummaryResp {
  interestRateTraces: PageResponse<InterestRateTraceResponse>;
  totalInterestRateMonthly: number;
}
