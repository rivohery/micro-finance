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
        successMsg: '',
        errorMsg: '',
        userInfos: undefined,
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
        successMsg: `Bienvenue ${action.userInfos.username} sur notre application`,
        errorMsg: '',
      };
    }),
    on(AuthsActions.getUserAuthenticatedFails, (state, action) => {
      return {
        ...state,
        loading: false,
        errorMsg: action.error,
        userInfos: undefined,
        successMsg: '',
      };
    }),
    on(AuthsActions.logout, (state) => {
      return {
        ...state,
        loading: true,
        successMsg: '',
        errorMsg: '',
      };
    }),
    on(AuthsActions.logoutSuccess, (state, action) => {
      return {
        ...state,
        loading: false,
        successMsg: action.success,
        userInfos: undefined,
        errorMsg: '',
      };
    }),
    on(AuthsActions.logoutFails, (state, action) => {
      return {
        ...state,
        loading: false,
        errorMsg: action.error,
        successMsg: '',
      };
    }),
    on(AuthsActions.refreshUserInfos, (state, action) => {
      return {
        ...state,
        userInfos: action.userInfos,
        loading: false,
        successMsg: '',
        errorMsg: '',
      };
    }),
    on(AuthsActions.emptyAction, (state) => {
      return {
        ...state,
      };
    })
  ),
});
