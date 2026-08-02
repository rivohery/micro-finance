import { inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { LoginRequest } from '../../features/auths/auths.model';
import { AuthsActions } from './auths.actions';
import { authFeature } from './auths.reducer';

export function injectAuthsStore() {
  const store = inject(Store);

  return {
    login: (loginRequest: LoginRequest) =>
      store.dispatch(AuthsActions.login({ loginRequest })),
    logout: () => store.dispatch(AuthsActions.logout()),
    verifyUserInfoInLS: () => store.dispatch(AuthsActions.verifyUserInfoInLS()),
    loading: store.selectSignal(authFeature.selectLoading),
    userInfos: store.selectSignal(authFeature.selectUserInfos),
    errorMsg: store.selectSignal(authFeature.selectErrorMsg),
    successMsg: store.selectSignal(authFeature.selectSuccessMsg),
  };
}
