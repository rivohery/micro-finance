import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { HistoryAccountTransactionsComponent } from '../../../../shared/components/account/history-account-transactions/history-account-transactions.component';
import { TransactionService } from '../../../../core/services/transaction.service';
import { PageResponse } from '../../../../shared/models/shared.model';
import { TransactionResponse } from '../../../../core/models/account.model';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-transactions',
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    HistoryAccountTransactionsComponent,
    LoaderComponent,
    MessageBoxComponent,
  ],
  providers: [DatePipe],
  templateUrl: './transactions.component.html',
  styleUrl: './transactions.component.css',
})
export class TransactionsComponent implements OnInit {
  transactionService = inject(TransactionService);
  datePipe = inject(DatePipe);

  loading = signal<boolean>(false);
  errorMsg = signal<string>('');

  loadingPdfFile = signal<boolean>(false);

  transactionPages!: PageResponse<TransactionResponse>;
  selectedDate: Date = new Date();
  currentPage: number = 0;
  totalElements: number = 0;

  constructor() {
    effect(() => this.handleFindAllByCreatedDateState());
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.loadAllTransaction();
  }

  private handleFindAllByCreatedDateState(): void {
    const findAllByCreatedDateState =
      this.transactionService.findAllByCreatedDateState$();
    if (
      findAllByCreatedDateState.status === 'OK' &&
      findAllByCreatedDateState.value
    ) {
      this.loading.set(false);
      this.transactionPages = findAllByCreatedDateState.value;
      this.totalElements = findAllByCreatedDateState.value.totalElements || 0;
    }
    if (findAllByCreatedDateState.status === 'ERROR') {
      this.loading.set(false);
      this.errorMsg.set(findAllByCreatedDateState.error || '');
    }
  }

  private loadAllTransaction(): void {
    const dateFormated =
      this.datePipe.transform(this.selectedDate, 'yyyy-MM-dd') || ''; //format iso: yyyy-MM-dd
    this.transactionService.findAllByCreatedDate(
      dateFormated,
      this.currentPage
    );
  }

  goToPage(page: number) {
    this.currentPage = page;
    this.loadAllTransaction();
  }

  closeErrorMsg(value: any) {
    this.errorMsg.set('');
  }

  doSearch(): void {
    this.currentPage = 0;
    console.log(this.selectedDate);
    this.loadAllTransaction();
  }

  clearFilter() {
    this.selectedDate = new Date();
    this.currentPage = 0;
    this.loadAllTransaction();
  }

  exportToPDF(): void {
    console.log('Exportation en cours vers PDF...');
    this.loadingPdfFile.set(true);
    const dateFormated =
      this.datePipe.transform(this.selectedDate, 'yyyy-MM-dd') || ''; //format iso: yyyy-MM-dd
    this.transactionService.exportPdf(dateFormated).subscribe({
      next: (blob) => {
        if (typeof window !== 'undefined') {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `TRA-${dateFormated}.pdf`;
          a.click();
          window.URL.revokeObjectURL(url);
        }
      },
      error: (err) => console.log(err),
      complete: () => this.loadingPdfFile.set(false),
    });
  }
}
