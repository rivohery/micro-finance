import {
  Component,
  effect,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { PageResponse } from '../../../../shared/models/shared.model';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { CommonModule, NgClass } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TransactionDisplayModalComponent } from '../transaction-display-modal/transaction-display-modal.component';
import {
  TransactionResponse,
  TransactionType,
} from '../../model/transactions.model';

@Component({
  selector: 'app-history-account-transactions',
  imports: [MatIconModule, CommonModule, PaginationComponent, MatDialogModule],
  templateUrl: './history-account-transactions.component.html',
  styleUrl: './history-account-transactions.component.css',
})
export class HistoryAccountTransactionsComponent {
  transactionPages = input.required<PageResponse<TransactionResponse>>();
  onPageChange = output<number>();

  dialog = inject(MatDialog);

  totalPages = signal<number>(0);
  currentPage = signal<number>(0);
  transactions = signal<TransactionResponse[]>([]);

  constructor() {
    effect(() => {
      if (this.transactionPages()) {
        console.log(this.transactionPages());
        this.transactions.set(this.transactionPages().content || []);
        this.totalPages.set(this.transactionPages().totalPages || 0);
        this.currentPage.set(this.transactionPages().number || 0);
      }
    });
  }

  goToPage(page: number): void {
    this.onPageChange.emit(page);
  }

  getTxBadgeClass(type: TransactionType) {
    switch (type) {
      case 'DEPOSIT':
        return 'bg-emerald-50 text-emerald-700 border-emerald-100';
      case 'WITHDRAWAL':
        return 'bg-rose-50 text-rose-700 border-rose-100';
      case 'TRANSFERT':
        return 'bg-blue-50 text-blue-700 border-blue-100';
      default:
        return 'bg-slate-50 text-slate-700';
    }
  }

  viewTransactionDetails(tx: TransactionResponse): void {
    const dialogRef = this.dialog.open(TransactionDisplayModalComponent, {
      width: '450px',
      maxWidth: '95vw',
      data: {
        id: tx.id,
        accountNumber: tx.accountNumber,
        transactionType: tx.transactionType,
        description: tx.description,
        reference: tx.reference,
        originalAmount: tx.originalAmount,
        finalAmount: tx.finalAmount,
        exchangeRate: tx.exchangeRate,
        operatorName: tx.operatorName,
        transactionCurrency: tx.transactionCurrency,
        targetCurrency: tx.targetCurrency,
        createdDate: tx.createdDate,
      } as TransactionResponse,
    });
  }
}
