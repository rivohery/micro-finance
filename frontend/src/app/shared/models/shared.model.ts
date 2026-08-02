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

//Field Form
export type FieldError = {
  value: string;
  message: string;
};
