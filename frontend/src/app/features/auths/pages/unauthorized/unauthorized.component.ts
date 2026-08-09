import { Location } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  imports: [MatIconModule, MatButtonModule, RouterLink],
  templateUrl: './unauthorized.component.html',
  styles: [
    `
      :host {
        display: block;
        height: 100%;
      }
    `,
  ],
})
export class UnauthorizedComponent {
  router = inject(Router);
  location = inject(Location);

  goBack() {
    this.location.back();
  }
}
