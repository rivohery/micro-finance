import { UserResponse } from '../../features/auths/auths.model';

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
