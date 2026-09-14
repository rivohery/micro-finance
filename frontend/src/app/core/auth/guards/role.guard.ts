import { CanActivateFn, Router } from '@angular/router';
import { injectAuthsStore } from '../stores/auths.facade';
import { inject } from '@angular/core';

export function roleGuard(allowedRoles: string | string[]): CanActivateFn {
  return (route, state) => {
    const authStore = injectAuthsStore();
    const router = inject(Router);

    // Conversion en tableau pour gérer un ou plusieurs rôles autorisés
    const rolesArray = Array.isArray(allowedRoles)
      ? allowedRoles
      : [allowedRoles];
    const userRole = authStore.userInfos()?.role;
    if (!userRole || !rolesArray.includes(userRole)) {
      router.navigateByUrl('/my-app/unauthorized');
      return false;
    }
    return true;
  };
}
