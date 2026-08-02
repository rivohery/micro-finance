import { CanActivateFn, Router } from '@angular/router';
import { injectAuthsStore } from '../stores/auths.facade';
import { inject } from '@angular/core';
import { ToastrService } from '../../shared/service/toastr/toastr.service';

export const authsGuard: CanActivateFn = (route, state) => {
  const authStore = injectAuthsStore();
  const router = inject(Router);
  const toastr = inject(ToastrService);

  if (!authStore.userInfos()) {
    toastr.show(
      "Vous devez s'authentifier pour accéder à cette resource",
      'ERROR'
    );
    router.navigate(['/login']);
    return false;
  }
  return true;
};
