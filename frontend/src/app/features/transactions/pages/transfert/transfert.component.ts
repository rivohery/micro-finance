import { NgIf } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { InputComponent } from '../../../../shared/components/form/input/input.component';
import { NumberComponent } from '../../../../shared/components/form/number/number.component';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { AccountTransactionService } from '../../data-access/account-transaction.service';
import { ToastrService } from '../../../../core/services/toastr/toastr.service';
import { Router } from '@angular/router';
import { TransfertRequest } from '../../model/transactions.model';

@Component({
  selector: 'app-transfert',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatDividerModule,
    MatButtonModule,
    MatIconModule,
    NgIf,
    InputComponent,
    NumberComponent,
    MessageBoxComponent,
  ],
  templateUrl: './transfert.component.html',
  styleUrl: './transfert.component.css',
})
export class TransfertComponent {
  transactionService = inject(AccountTransactionService);
  fb = inject(FormBuilder);
  toastr = inject(ToastrService);
  router = inject(Router);

  sending = signal<boolean>(false);
  errorMsg = signal<string>('');

  sourceAccountNumber: FormControl = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  targetAccountNumber: FormControl = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  description: FormControl = new FormControl<string>('', {
    nonNullable: true,
    validators: [
      Validators.required,
      Validators.minLength(5),
      Validators.maxLength(100),
    ],
  });
  transfertAmount: FormControl = new FormControl<number>(0.0, {
    nonNullable: true,
    validators: [Validators.required, Validators.min(10)],
  });

  transferForm: FormGroup = this.fb.nonNullable.group({
    sourceAccountNumber: this.sourceAccountNumber,
    targetAccountNumber: this.targetAccountNumber,
    description: this.description,
    transfertAmount: this.transfertAmount,
  });

  constructor() {}

  get isSameAccounts(): boolean {
    const source = this.transferForm.get('sourceAccountNumber')?.value;
    const target = this.transferForm.get('targetAccountNumber')?.value;
    return source === target;
  }

  onExecuteTransfer(formDirective: FormGroupDirective): void {
    if (this.transferForm.valid && !this.isSameAccounts) {
      this.sending.set(true);
      const request: TransfertRequest = { ...this.transferForm.getRawValue() };
      console.log(request);
      this.transactionService.transfert(request).subscribe({
        next: (resp) => {
          this.sending.set(false);
          formDirective.resetForm();
          this.transferForm.reset();
          this.toastr.show(resp.message || '', 'SUCCESS');
        },
        error: (err) => {
          this.sending.set(false);
          this.errorMsg.set(err.message);
        },
      });
    }
  }

  closeErrorMsg(res: any): void {
    this.errorMsg.set('');
  }

  onReset(): void {
    this.router.navigateByUrl('/my-app/clients/my-accounts');
  }
}
