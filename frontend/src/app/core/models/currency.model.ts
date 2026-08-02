export interface CurrencyResponse {
  id: string;
  name: string;
  code: string;
  enable: boolean;
}

export interface CreateCurrencyRequest {
  name: string;
  code: string;
}

export interface UpdateCurrencyRequest {
  id: string;
  name: string;
  code: string;
  enable: boolean;
}
