import { CanActivateFn, Router } from '@angular/router';
import { injectAuthsStore } from '../stores/auths.facade';
import { inject } from '@angular/core';

export const employeGuard: CanActivateFn = (route, state) => {
  const authStore = injectAuthsStore();
  const router = inject(Router);

  if (authStore.userInfos() && authStore.userInfos()?.role === 'CLIENT') {
    router.navigateByUrl('/my-app/unauthorized');
    return false;
  }
  return true;
};
