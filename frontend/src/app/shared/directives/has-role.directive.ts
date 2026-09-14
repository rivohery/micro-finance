import {
  Directive,
  TemplateRef,
  ViewContainerRef,
  effect,
  inject,
  input,
} from '@angular/core';
import { injectAuthsStore } from '../../core/auth/stores/auths.facade';

@Directive({
  selector: '[hasRole]',
  standalone: true,
})
export class HasRoleDirective {
  private authStore = injectAuthsStore();
  private templateRef = inject(TemplateRef<unknown>);
  private viewContainer = inject(ViewContainerRef);

  // Supporte un rôle unique [hasRole]="'ADMIN'" ou une liste [hasRole]="['ADMIN', 'EMPLOYE']"
  hasRole = input.required<string | string[]>();

  private isCreated = false;

  constructor() {
    // L'effect observe automatiquement le Signal du rôle utilisateur et l'input hasRole
    effect(() => {
      const allowedRoles = this.hasRole();
      const currentRole = this.authStore.userInfos()?.role;

      const rolesArray = Array.isArray(allowedRoles)
        ? allowedRoles
        : [allowedRoles];
      const hasPermission = currentRole
        ? rolesArray.includes(currentRole)
        : false;

      if (hasPermission && !this.isCreated) {
        // Injection du composant/élément dans le DOM uniquement s'il est autorisé
        this.viewContainer.createEmbeddedView(this.templateRef);
        this.isCreated = true;
      } else if (!hasPermission && this.isCreated) {
        // Suppression du DOM si les droits ont changé
        this.viewContainer.clear();
        this.isCreated = false;
      }
    });
  }
}
