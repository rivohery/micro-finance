import { inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { AuthsService } from '../services/auths.service';
import { AuthsActions } from './auths.actions';
import { catchError, exhaustMap, map, of } from 'rxjs';
import { UsersService } from '../services/users-service';
import { GlobalResponse } from '../../shared/models/shared.model';
import { UserResponse } from '../../features/auths/auths.model';
import { LocalStorageService } from '../services/localStorage.service';

export const login$ = createEffect(
  () => {
    const authsService = inject(AuthsService);
    const actions = inject(Actions);

    return actions.pipe(
      ofType(AuthsActions.login),
      exhaustMap((action) => {
        return authsService.login(action.loginRequest).pipe(
          exhaustMap((resp: GlobalResponse) =>
            of(AuthsActions.getUserAuthenticated({ userId: resp.data.userId }))
          ),
          catchError((err) =>
            of(AuthsActions.loginFails({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const getUserAuthenticated$ = createEffect(
  () => {
    const usersService = inject(UsersService);
    const localStorageService = inject(LocalStorageService);
    const actions = inject(Actions);

    return actions.pipe(
      ofType(AuthsActions.getUserAuthenticated),
      exhaustMap((action) => {
        return usersService.getUserAuthenticated(action.userId).pipe(
          map((resp: UserResponse) => {
            localStorageService.saveUserInfosInLS(resp);
            return AuthsActions.getUserAuthenticatedSuccess({
              userInfos: resp,
            });
          }),
          catchError((err) =>
            of(AuthsActions.getUserAuthenticatedFails({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const logout$ = createEffect(
  () => {
    const authsService = inject(AuthsService);
    const actions = inject(Actions);
    const localStorageService = inject(LocalStorageService);

    return actions.pipe(
      ofType(AuthsActions.logout),
      exhaustMap((action) => {
        return authsService.logout().pipe(
          exhaustMap((resp: GlobalResponse) => {
            localStorageService.clearLocalStorage();
            return of(AuthsActions.logoutSuccess({ success: resp.message }));
          }),
          catchError((err) =>
            of(AuthsActions.loginFails({ error: err.message }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const verifyUserInfoInLS$ = createEffect(
  () => {
    const localStorageService = inject(LocalStorageService);
    const actions = inject(Actions);

    return actions.pipe(
      ofType(AuthsActions.verifyUserInfoInLS),
      exhaustMap((action) => {
        const userInfos = localStorageService.getUserInfosInLs();
        if (userInfos) {
          return of(AuthsActions.refreshUserInfos({ userInfos }));
        } else {
          return of(AuthsActions.emptyAction());
        }
      })
    );
  },
  { functional: true }
);
