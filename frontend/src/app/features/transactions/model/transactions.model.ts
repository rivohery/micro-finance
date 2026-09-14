export type TransactionType = 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFERT';

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
