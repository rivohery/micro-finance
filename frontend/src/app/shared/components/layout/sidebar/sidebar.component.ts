import { Component, effect, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { injectAuthsStore } from '../../../../core/stores/auths.facade';
import { ToastrService } from '../../../service/toastr/toastr.service';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, MatIconModule, MatButtonModule, NgIf],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
})
export class SidebarComponent {
  authStore = injectAuthsStore();
  router = inject(Router);
  toastr = inject(ToastrService);

  constructor() {
    effect(() => {
      if (this.authStore.successMsg() && !this.authStore.userInfos()) {
        this.toastr.show(this.authStore.successMsg(), 'SUCCESS');
        this.router.navigate(['/login']);
      }
    });
  }

  logout(): void {
    this.authStore.logout();
  }

  get adminAccess(): boolean {
    return this.authStore.userInfos()?.role === 'ADMIN';
  }

  get employeAccess(): boolean {
    return (
      this.authStore.userInfos()?.role === 'ADMIN' ||
      this.authStore.userInfos()?.role === 'EMPLOYE'
    );
  }

  get customerAccess(): boolean {
    return this.authStore.userInfos()?.role === 'CLIENT';
  }
}
