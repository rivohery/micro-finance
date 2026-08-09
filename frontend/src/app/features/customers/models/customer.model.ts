import { AccountResponse } from '../../accounts/model/account.model';

export interface CustomerRequest {
  firstName: string;
  lastName: string;
  username: string;
  userId: string;
  dateOfBirth: Date;
  phoneNumber: string;
  email: string;
  cin: string;
  occupation: string;
  addressValue: string;
  addressCity: string;
  addressZipCode: string;
  addressCountry: string;
}

export type CustomerStatus = 'ACTIVE' | 'SUSPENDED' | 'PENDING' | 'CLOSED';

export interface CustomerMinResponse {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  photo: string;
  dateOfBirth: Date;
  status: CustomerStatus;
  createdDate: Date;
  lastModifiedDate: Date;
}

export const initCustomerValue = {
  id: '',
  firstName: '',
  lastName: '',
  email: '',
  phoneNumber: '',
  photo: '',
  dateOfBirth: new Date(),
  status: 'PENDING',
  createdDate: new Date(),
  lastModifiedDate: new Date(),
} as CustomerMinResponse;

export interface UpdateStatusClientRequest {
  id: string;
  status: CustomerStatus;
}

export interface CustomerResponse {
  id: string;
  firstName: string;
  lastName: string;
  username: string;
  userId: string;
  dateOfBirth: Date;
  phoneNumber: string;
  email: string;
  cin: string;
  status: CustomerStatus;
  addressValue: string;
  addressCity: string;
  addressZipCode: string;
  addressCountry: string;
  occupation: string;
  photo: string;
  createdDate: Date;
  lastModifiedDate: Date;
  createdBy: string;
  lastModifiedBy: string;
}

export const initCustomerResponse = {
  id: '',
  firstName: '',
  lastName: '',
  username: '',
  userId: '',
  dateOfBirth: new Date(),
  phoneNumber: '',
  email: '',
  cin: '',
  status: 'SUSPENDED',
  addressValue: '',
  addressCity: '',
  addressZipCode: '',
  addressCountry: '',
  occupation: '',
  photo: '',
  createdDate: new Date(),
  lastModifiedDate: new Date(),
  createdBy: '',
  lastModifiedBy: '',
} as CustomerResponse;

export interface DetailCustomerWithAccount {
  accounts: AccountResponse[];
  customer: CustomerMinResponse;
}
