import { Component, Inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import {
  TransactionResponse,
  TransactionType,
} from '../../model/transactions.model';

@Component({
  selector: 'app-transaction-display-modal',
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
  ],
  templateUrl: './transaction-display-modal.component.html',
  styleUrl: './transaction-display-modal.component.css',
})
export class TransactionDisplayModalComponent {
  tx!: TransactionResponse;

  constructor(
    private dialogRef: MatDialogRef<TransactionDisplayModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: TransactionResponse
  ) {
    this.tx = { ...data };
  }

  getTxTypeStyles(type: TransactionType) {
    switch (type) {
      case 'DEPOSIT':
        return {
          bg: 'bg-emerald-500/10 text-emerald-400',
          label: 'Dépôt / Crédit',
          icon: 'arrow_downward',
          color: 'text-emerald-600',
        };
      case 'WITHDRAWAL':
        return {
          bg: 'bg-rose-500/10 text-rose-400',
          label: 'Retrait / Débit',
          icon: 'arrow_upward',
          color: 'text-rose-600',
        };
      case 'TRANSFERT':
        return {
          bg: 'bg-blue-500/10 text-blue-400',
          label: 'Transfert Interne',
          icon: 'swap_horiz',
          color: 'text-blue-600',
        };
      default:
        return {
          bg: 'bg-slate-500/10 text-slate-400',
          label: 'Inconnu',
          icon: 'help',
          color: 'text-slate-600',
        };
    }
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
