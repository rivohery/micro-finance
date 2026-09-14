import { DatePipe, NgClass } from '@angular/common';
import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AccountService } from '../../data-access/account.service';
import { AccountResponse, AccountStatus } from '../../model/account.model';
import { SearchBarComponent } from '../../../../shared/components/search-bar/search-bar.component';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-account-list',
  imports: [
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    SearchBarComponent,
    MessageBoxComponent,
    LoaderComponent,
    PaginationComponent,
    NgClass,
    DatePipe,
  ],
  templateUrl: './account-list.component.html',
  styleUrl: './account-list.component.css',
})
export class AccountListComponent implements OnInit {
  accountService = inject(AccountService);
  router = inject(Router);

  errorMsg = signal<string>('');
  loading = signal<boolean>(false);
  currentSearch = signal<string>('');
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  totalElements = signal<number>(0);
  accounts = signal<AccountResponse[]>([]);

  constructor() {
    effect(() => {
      const findAllAccountState = this.accountService.findAllAccountState$();
      if (findAllAccountState.status === 'OK') {
        this.loading.set(false);
        this.accounts.set(findAllAccountState.value?.content || []);
        this.totalPages.set(findAllAccountState.value?.totalPages || 0);
        this.totalElements.set(findAllAccountState.value?.totalElements || 0);
        this.currentPage.set(findAllAccountState.value?.number || 0);
      }
      if (findAllAccountState.status === 'ERROR') {
        this.loading.set(false);
        this.errorMsg.set(findAllAccountState.error || '');
      }
    });
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.loadAllAccounts();
  }

  private loadAllAccounts(): void {
    this.accountService.findAllAccountBySearch(
      this.currentSearch(),
      this.currentPage()
    );
  }

  doSearch(search: string): void {
    this.currentSearch.set(search);
    this.currentPage.set(0);
    this.loadAllAccounts();
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.loadAllAccounts();
  }

  // Styles de badges basés sur l'état du cycle de vie du compte
  getStatusBadgeClass(status: AccountStatus): string {
    switch (status) {
      case 'ACTIVE':
        return 'bg-emerald-100 text-emerald-700 border-emerald-200';
      case 'PENDING':
        return 'bg-amber-100 text-amber-700 border-amber-200';
      case 'SUSPENDED':
        return 'bg-rose-100 text-rose-700 border-rose-200';
      case 'CLOSED':
        return 'bg-slate-200 text-slate-700 border-slate-300';
    }
  }

  closeErrorMsg(value: any): void {
    this.errorMsg.set('');
  }

  viewAccountDetails(account: AccountResponse) {
    this.router.navigateByUrl(
      `/my-app/managment/account-details/${account.accountNumber}`
    );
  }
}
