import { Component, OnInit, effect, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { injectAuthsStore } from './core/stores/auths.facade';
import { MatIconModule } from '@angular/material/icon';
import { NgClass } from '@angular/common';
import { ToastrService } from './shared/service/toastr/toastr.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatIconModule, NgClass],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  title = 'micro-finance';
  authStore = injectAuthsStore();
  toastrService = inject(ToastrService);

  constructor() {
    if (this.authStore.userInfos() === undefined) {
      this.authStore.verifyUserInfoInLS();
    }
  }
}
