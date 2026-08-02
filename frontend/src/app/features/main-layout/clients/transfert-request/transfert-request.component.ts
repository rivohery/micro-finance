import { Component, inject, signal } from '@angular/core';
import { AccountTransactionService } from '../../../../core/services/account-transaction.service';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ToastrService } from '../../../../shared/service/toastr/toastr.service';
import { NgIf } from '@angular/common';
import { InputComponent } from '../../../../shared/components/form/input/input.component';
import { MatDividerModule } from '@angular/material/divider';
import { NumberComponent } from '../../../../shared/components/form/number/number.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { TransfertRequest } from '../../../../core/models/account.model';
import { Router } from '@angular/router';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';

@Component({
  selector: 'app-transfert-request',
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
  templateUrl: './transfert-request.component.html',
  styleUrl: './transfert-request.component.css',
})
export class TransfertRequestComponent {
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
    validators: [Validators.required, Validators.min(100)],
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
