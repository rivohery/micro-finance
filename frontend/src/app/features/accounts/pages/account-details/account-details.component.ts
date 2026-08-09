import { Component, effect, inject, input, signal } from '@angular/core';
import {
  AccountResponse,
  AccountStatus,
  AccountStatusHistoryResponse,
  initAccountResponse,
} from '../../model/account.model';

import { CommonModule } from '@angular/common';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { HistoryAccountTransactionsComponent } from '../../../transactions/ui/history-account-transactions/history-account-transactions.component';
import { PageResponse } from '../../../../shared/models/shared.model';
import { HistoryAccountLifeCycleComponent } from '../../ui/history-account-life-cycle/history-account-life-cycle.component';
import { AccountService } from '../../data-access/account.service';
import { TransactionService } from '../../../transactions/data-access/transaction.service';
import { AccountLifeCycleService } from '../../data-access/account-life-cycle.service';
import {
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { ChangeStatusAccountModalComponent } from '../../ui/change-status-account-modal/change-status-account-modal.component';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { injectAuthsStore } from '../../../../core/auth/stores/auths.facade';
import { TransactionResponse } from '../../../transactions/model/transactions.model';
import { HasRoleDirective } from '../../../../shared/directives/has-role.directive';

type TabType = 'TRANSACTIONS' | 'LIFECYCLE';

@Component({
  selector: 'app-account-details',
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatDialogModule,
    LoaderComponent,
    MessageBoxComponent,
    HistoryAccountTransactionsComponent,
    HistoryAccountLifeCycleComponent,
    HasRoleDirective,
  ],
  templateUrl: './account-details.component.html',
  styleUrl: './account-details.component.css',
})
export class AccountDetailsComponent {
  authStore = injectAuthsStore();
  accountNumber = input<string>();
  accountService = inject(AccountService);
  transactionService = inject(TransactionService);
  accountLifeCycleService = inject(AccountLifeCycleService);
  dialog = inject(MatDialog);

  loading = signal<boolean>(true);
  errorMsg = signal<string>('');

  account = signal<AccountResponse>(initAccountResponse);
  transactionPages!: PageResponse<TransactionResponse>;
  currentTransactionPage = signal<number>(0);
  accountLyfeCyclePages!: PageResponse<AccountStatusHistoryResponse>;
  currentLifeCyclePage = signal<number>(0);

  constructor() {
    effect(() => this.initStateOfComponent());
    effect(() => this.handleFindByAccountNumberState());
  }

  private initStateOfComponent(): void {
    const accountNumber = this.accountNumber();
    if (accountNumber) {
      this.accountService.initFindByAccountNumberState();
      this.accountService.findByAccountNumber(accountNumber);
    }
  }

  private handleFindByAccountNumberState(): void {
    const findByAccountNumberState =
      this.accountService.findByAccountNumberState$();
    if (
      findByAccountNumberState.status === 'OK' &&
      findByAccountNumberState.value
    ) {
      this.loading.set(false);
      this.account.set(findByAccountNumberState.value);
      this.getTransactionPage(this.account().accountNumber);
    }
    if (findByAccountNumberState.status === 'ERROR') {
      this.loading.set(false);
      this.errorMsg.set(findByAccountNumberState.error || '');
    }
  }

  private getTransactionPage(accountNumber: string): void {
    this.transactionService
      .findAllByAccountNumber(accountNumber, this.currentTransactionPage())
      .subscribe({
        next: (resp) => (this.transactionPages = resp),
        error: (err) => this.errorMsg.set(err.message),
      });
  }

  private getAccountLifeCyclePage(accountid: string): void {
    this.accountLifeCycleService
      .findAllByAccountId(accountid, this.currentLifeCyclePage())
      .subscribe({
        next: (resp) => (this.accountLyfeCyclePages = resp),
        error: (err) => this.errorMsg.set(err.message),
      });
  }

  /*  get employeAccess(): boolean {
    return (
      this.authStore.userInfos()?.role === 'ADMIN' ||
      this.authStore.userInfos()?.role === 'EMPLOYE'
    );
  } */

  goToTransactionPage(page: number): void {
    this.currentTransactionPage.set(page);
    this.getTransactionPage(this.account().accountNumber);
  }

  goToAccountLifeCyclePage(page: number): void {
    this.currentLifeCyclePage.set(page);
    this.getAccountLifeCyclePage(this.account().id);
  }

  // Onglet actif par défaut
  activeTab: TabType = 'TRANSACTIONS';

  switchTab(tab: TabType) {
    this.activeTab = tab;
    if (tab === 'TRANSACTIONS') {
      this.currentTransactionPage.set(0);
      this.getTransactionPage(this.account().accountNumber);
    } else {
      this.currentLifeCyclePage.set(0);
      this.getAccountLifeCyclePage(this.account().id);
    }
  }

  // Helpers de badges pour le style Tailwind
  getStatusClass(status: AccountStatus): string {
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

  openStatusModal() {
    const dialogRef = this.dialog.open(ChangeStatusAccountModalComponent, {
      width: '450px',
      maxWidth: '95vw',
      data: {
        accountId: this.account().id,
        status: this.account().status,
        accountNumber: this.account().accountNumber,
      },
    });
    /* dialogRef.afterClosed().subscribe((resp) => {
      if (resp !== 'annulé') {
        console.log(resp);
      }
    }); */
  }

  refresh(): void {
    this.accountService.findByAccountNumber(this.account().accountNumber);
  }

  closeErrorMsg(error: any) {
    this.errorMsg.set('');
  }
}
