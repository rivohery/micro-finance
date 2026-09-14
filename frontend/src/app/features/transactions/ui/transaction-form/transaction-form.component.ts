import {
  Component,
  OnInit,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { CurrencyService } from '../../../currency/data-access/currency.service';
import { AccountTransactionService } from '../../data-access/account-transaction.service';
import { ToastrService } from '../../../../core/services/toastr/toastr.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { TransactionType } from '../../model/transactions.model';
import { TransactionRequest } from '../../model/transactions.model';
import { CurrencyResponse } from '../../../currency/model/currency.model';

@Component({
  selector: 'app-transaction-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatSelectModule,
  ],
  templateUrl: './transaction-form.component.html',
  styleUrl: './transaction-form.component.css',
})
export class TransactionFormComponent implements OnInit {
  currentType = input<TransactionType>('DEPOSIT');
  currencies = input<CurrencyResponse[]>([]);
  submitting = input<boolean>(false);
  onError = output<string>();
  onDepositEvent = output<any>();
  onWithdrawEvent = output<any>();

  fb = inject(FormBuilder);

  accountNumber: FormControl = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  amount: FormControl = new FormControl<number>(0.0, {
    nonNullable: true,
    validators: [Validators.required, Validators.min(100)],
  });
  currencyCode: FormControl = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  description: FormControl = new FormControl<string>('', {
    nonNullable: true,
    validators: [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(255),
    ],
  });

  transactionForm: FormGroup = this.fb.nonNullable.group({
    accountNumber: this.accountNumber,
    amount: this.amount,
    currencyCode: this.currencyCode,
    description: this.description,
  });

  ngOnInit(): void {
    this.transactionForm.reset();
  }

  executeTransaction(formDirective: FormGroupDirective): void {
    if (this.transactionForm.invalid) {
      return;
    }
    const request: TransactionRequest = this.transactionForm.value;
    if (this.currentType() === 'DEPOSIT') {
      this.onDepositEvent.emit({ request, formDirective });
    } else {
      this.onWithdrawEvent.emit({ request, formDirective });
    }
  }

  onCancel() {
    this.transactionForm.reset();
  }
}
