import {
  Component,
  OnInit,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import {
  TransactionRequest,
  TransactionType,
} from '../../../../../core/models/account.model';
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
import { CurrencyService } from '../../../../../core/services/currency.service';
import { AccountTransactionService } from '../../../../../core/services/account-transaction.service';
import { ToastrService } from '../../../../../shared/service/toastr/toastr.service';
import { toSignal } from '@angular/core/rxjs-interop';

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
  onError = output<string>();

  fb = inject(FormBuilder);
  currencyService = inject(CurrencyService);
  accountTransactionService = inject(AccountTransactionService);
  toastr = inject(ToastrService);

  submitting = signal<boolean>(false);

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

  currencies = toSignal(this.currencyService.fetchEnableCurrency(), {
    initialValue: [],
  });

  ngOnInit(): void {
    this.transactionForm.reset();
  }

  private deposit(
    request: TransactionRequest,
    formDirective: FormGroupDirective
  ): void {
    this.accountTransactionService.deposit(request).subscribe({
      next: (resp) => {
        console.log(resp);
        this.submitting.set(false);
        this.toastr.show('Dépot reussie', 'SUCCESS');
        formDirective.resetForm();
        this.transactionForm.reset();
      },
      error: (err) => {
        this.submitting.set(false);
        this.onError.emit(err.message);
      },
    });
  }

  private withdraw(
    request: TransactionRequest,
    formDirective: FormGroupDirective
  ): void {
    this.accountTransactionService.withdraw(request).subscribe({
      next: (resp) => {
        console.log(resp);
        this.submitting.set(false);
        this.toastr.show('Retrait reussie', 'SUCCESS');
        formDirective.resetForm();
        this.transactionForm.reset();
      },
      error: (err) => {
        this.submitting.set(false);
        this.onError.emit(err.message);
      },
    });
  }

  executeTransaction(formDirective: FormGroupDirective): void {
    if (this.transactionForm.invalid) {
      this.onError.emit('Certaines données sont invalide');
      return;
    }
    const transactionRequest: TransactionRequest = this.transactionForm.value;
    this.submitting.set(true);
    if (this.currentType() === 'DEPOSIT') {
      this.deposit(transactionRequest, formDirective);
    } else {
      this.withdraw(transactionRequest, formDirective);
    }
  }

  onCancel() {
    this.transactionForm.reset();
  }
}
