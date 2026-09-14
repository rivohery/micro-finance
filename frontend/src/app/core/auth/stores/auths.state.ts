import { UserResponse } from '../../../features/users/model/user.model';

export interface AuthsState {
  userInfos: UserResponse | undefined;
  successMsg: string;
  errorMsg: string;
  loading: boolean;
}

export const initialAuthsState = {
  userInfos: undefined,
  successMsg: '',
  errorMsg: '',
  loading: false,
} as AuthsState;
