export interface AccountTypeResponse {
  id: string;
  name: string;
  code: string;
  accountFee: number;
  interestRate: number;
  minimumBalance: number;
  createdDate: Date;
  lastModifiedDate: Date;
}
export interface AccountTypeRequest {
  name: string;
  code: string;
  accountFee: number;
  interestRate: number;
  minimumBalance: number;
}
