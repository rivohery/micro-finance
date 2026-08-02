export type AccountStatus = 'PENDING' | 'ACTIVE' | 'SUSPENDED' | 'CLOSED';

export interface AccountResponse {
  id: string;
  balance: number;
  accountType: string;
  createdDate: Date;
  lastModifiedDate: Date;
  accountNumber: string;
  status: AccountStatus;
  customerId: string;
  currencyCode: string;
}

export const initAccountResponse = {
  id: '',
  balance: 0,
  accountType: '',
  createdDate: new Date(),
  lastModifiedDate: new Date(),
  accountNumber: '',
  status: 'PENDING',
  customerId: '',
  currencyCode: '',
} as AccountResponse;

export interface CreateAccountRequest {
  accountTypeCode: string;
  currencyCode: string;
  customerId: string;
}

export type TransactionType = 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFERT';

export interface TransactionRequest {
  accountNumber: string;
  amount: number;
  currencyCode: string;
  description: string;
}

export interface TransfertRequest {
  sourceAccountNumber: string;
  targetAccountNumber: string;
  description: string;
  transfertAmount: string;
}

export interface TransactionResponse {
  id: string;
  accountNumber: string;
  transactionType: TransactionType;
  description: string;
  reference: string;
  originalAmount: number;
  finalAmount: number;
  exchangeRate: number;
  operatorName: string;
  transactionCurrency: string;
  targetCurrency: string;
  createdDate: Date;
}

export interface AccountStatusHistoryResponse {
  id: string;
  accountId: string;
  oldStatus: AccountStatus;
  newStatus: AccountStatus;
  doingBy: string;
  doingAt: Date;
  reason: string;
}

export interface AccountLifeCycleRequest {
  accountId: string;
  reason: string;
}
