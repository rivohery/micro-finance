import { CommonModule } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TransactionType } from '../../../../core/models/account.model';
import { TransactionFormComponent } from './transaction-form/transaction-form.component';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';

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
  currentType: TransactionType = 'DEPOSIT'; // Type par défaut
  errorMsg = signal<string>('');

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
