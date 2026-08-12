import { createFeature, createReducer, on } from '@ngrx/store';
import { initialAuthsState } from './auths.state';
import { AuthsActions } from './auths.actions';

export const authFeature = createFeature({
  name: 'auths',
  reducer: createReducer(
    initialAuthsState,
    on(AuthsActions.login, (state) => {
      return {
        ...state,
        loading: true,
      };
    }),
    on(AuthsActions.loginFails, (state, action) => {
      return {
        ...state,
        errorMsg: action.error,
        loading: false,
      };
    }),
    on(AuthsActions.getUserAuthenticated, (state, action) => {
      return {
        ...state,
      };
    }),
    on(AuthsActions.getUserAuthenticatedSuccess, (state, action) => {
      return {
        ...state,
        loading: false,
        userInfos: action.userInfos,
        successMsg: `Bienvenue ${action.userInfos.username}`,
      };
    }),
    on(AuthsActions.getUserAuthenticatedFails, (state, action) => {
      return {
        ...state,
        loading: false,
        errorMsg: action.error,
      };
    }),
    on(AuthsActions.logout, (state) => {
      return {
        ...state,
        loading: true,
      };
    }),
    on(AuthsActions.logoutSuccess, (state, action) => {
      return {
        ...state,
        loading: false,
        successMsg: action.success,
        userInfos: undefined,
      };
    }),
    on(AuthsActions.logoutFails, (state, action) => {
      return {
        ...state,
        loading: false,
        errorMsg: action.error,
      };
    }),
    on(AuthsActions.refreshUserInfos, (state, action) => {
      return {
        ...state,
        userInfos: action.userInfos,
      };
    }),
    on(AuthsActions.initAuthState, (state) => {
      return {
        ...state,
        errorMsg: '',
        loading: false,
        successMsg: '',
        userInfos: undefined,
      };
    }),
    on(AuthsActions.emptyAction, (state) => {
      return {
        ...state,
      };
    })
  ),
});
