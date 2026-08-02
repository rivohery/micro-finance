import { createActionGroup, emptyProps, props } from '@ngrx/store';
import { LoginRequest, UserResponse } from '../../features/auths/auths.model';
export const AuthsActions = createActionGroup({
  source: 'Auths',
  events: {
    login: props<{ loginRequest: LoginRequest }>(),
    loginFails: props<{ error: string }>(),
    getUserAuthenticated: props<{ userId: string }>(),
    getUserAuthenticatedSuccess: props<{ userInfos: UserResponse }>(),
    getUserAuthenticatedFails: props<{ error: string }>(),
    logout: emptyProps,
    logoutSuccess: props<{ success: string }>(),
    logoutFails: props<{ error: string }>(),
    verifyUserInfoInLS: emptyProps,
    refreshUserInfos: props<{ userInfos: UserResponse }>(),
    emptyAction: emptyProps,
  },
});
