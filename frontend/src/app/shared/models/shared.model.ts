export interface GlobalResponse {
  timestamps?: Date;
  status?: number;
  message: string;
  data?: any;
}

export interface PageResponse<T> {
  content?: Array<T>;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
  first?: boolean;
  last?: boolean;
}

//Validation field form
export type FieldError = {
  value: string;
  message: string;
};

export interface LoadingState<T> {
  loading: boolean;
  error: string;
  data: T;
}
