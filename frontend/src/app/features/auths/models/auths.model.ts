export interface LoginRequest {
  username: string;
  password: string;
}

export interface ChangePasswordRequest {
  username?: string;
  oldPasswordPlain?: string;
  newPasswordPlain?: string;
}
