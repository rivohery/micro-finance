import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { TransactionType } from '../../../transactions/model/transactions.model';
import { TransactionFormComponent } from '../../ui/transaction-form/transaction-form.component';
import { CurrencyService } from '../../../currency/data-access/currency.service';
import { AccountTransactionService } from '../../data-access/account-transaction.service';
import { ToastrService } from '../../../../core/services/toastr/toastr.service';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-cash-operation',
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    TransactionFormComponent,
    MessageBoxComponent,
  ],
  templateUrl: './cash-operation.component.html',
  styleUrl: './cash-operation.component.css',
})
export class CashOperationComponent {
  currencyService = inject(CurrencyService);
  accountTransactionService = inject(AccountTransactionService);
  toastr = inject(ToastrService);

  currentType: TransactionType = 'DEPOSIT'; // Type par défaut
  errorMsg = signal<string>('');
  submitting = signal<boolean>(false);

  currencies = toSignal(this.currencyService.fetchEnableCurrency(), {
    initialValue: [],
  });

  makeDeposit(data: any): void {
    this.submitting.set(true);
    this.accountTransactionService.deposit(data.request).subscribe({
      next: (resp) => {
        console.log(resp);
        this.submitting.set(false);
        this.toastr.show('Dépot reussie', 'SUCCESS');
        data.formDirective.resetForm();
      },
      error: (err) => {
        this.submitting.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  makeWithdraw(data: any): void {
    this.submitting.set(true);
    this.accountTransactionService.withdraw(data.request).subscribe({
      next: (resp) => {
        console.log(resp);
        this.submitting.set(false);
        this.toastr.show('Retrait reussie', 'SUCCESS');
        data.formDirective.resetForm();
      },
      error: (err) => {
        this.submitting.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  setTransactionType(type: TransactionType) {
    this.currentType = type;
  }

  displayErrorMsg(value: string): void {
    this.errorMsg.set(value);
  }

  closeErrorMsg(): void {
    this.errorMsg.set('');
  }
}
