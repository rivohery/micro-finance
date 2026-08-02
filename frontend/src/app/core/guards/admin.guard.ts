import { CanActivateFn, Router } from '@angular/router';
import { injectAuthsStore } from '../stores/auths.facade';
import { inject } from '@angular/core';

export const adminGuard: CanActivateFn = (route, state) => {
  const authStore = injectAuthsStore();
  const router = inject(Router);

  if (authStore.userInfos()?.role !== 'ADMIN') {
    router.navigateByUrl('/my-app/unauthorized');
    return false;
  }
  return true;
};
