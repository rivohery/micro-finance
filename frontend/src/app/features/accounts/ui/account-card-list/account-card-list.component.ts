import { CommonModule } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { AccountResponse } from '../../model/account.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-account-card-list',
  imports: [CommonModule, MatIconModule, MatButtonModule, MatCardModule],
  templateUrl: './account-card-list.component.html',
  styleUrl: './account-card-list.component.css',
})
export class AccountCardListComponent {
  accounts = input<AccountResponse[]>([]);
  router = inject(Router);

  onViewDetails(account: AccountResponse): void {
    this.router.navigateByUrl(
      `/my-app/managment/account-details/${account.accountNumber}`
    );
  }
}
