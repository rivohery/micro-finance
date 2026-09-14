import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { routes } from './app.routes';
import {
  provideClientHydration,
  withEventReplay,
} from '@angular/platform-browser';
import {
  provideHttpClient,
  withInterceptors,
  withXsrfConfiguration,
} from '@angular/common/http';
import { authsInterceptor } from './core/http/auths.interceptor';
import { provideState, provideStore } from '@ngrx/store';
import { authFeature } from './core/auth/stores/auths.reducer';
import { provideEffects } from '@ngrx/effects';
import {
  login$,
  logout$,
  getUserAuthenticated$,
  verifyUserInfoInLS$,
} from './core/auth/stores/auths.effects';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    provideClientHydration(withEventReplay()),
    provideHttpClient(
      withInterceptors([authsInterceptor]),
      // Configuration par défaut, mais on peut la préciser :
      withXsrfConfiguration({
        cookieName: 'XSRF-TOKEN',
        headerName: 'X-XSRF-TOKEN',
      })
    ),
    provideStore(),
    provideState(authFeature),
    provideEffects({
      login$,
      logout$,
      getUserAuthenticated$,
      verifyUserInfoInLS$,
    }),
    provideCharts(withDefaultRegisterables()),
  ],
};
