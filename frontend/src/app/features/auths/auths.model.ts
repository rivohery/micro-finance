export type Role = 'ADMIN' | 'EMPLOYE' | 'CLIENT';

export interface UserResponse {
  id: string;
  username: string;
  email: string;
  enable: boolean;
  role: Role;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface ChangePasswordRequest {
  username?: string;
  oldPasswordPlain?: string;
  newPasswordPlain?: string;
}

export interface UserRequest {
  username: string;
  email: string;
  role?: Role;
}

export interface ChangeUserStatusRequest {
  userId: string;
  status: boolean;
}

export interface ChangeProfileRequest {
  id: string;
  username: string;
  email: string;
  password?: string;
}
