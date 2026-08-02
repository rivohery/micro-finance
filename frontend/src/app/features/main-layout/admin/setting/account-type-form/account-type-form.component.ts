import { Component, effect, inject, output, signal } from '@angular/core';
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
import { InputComponent } from '../../../../../shared/components/form/input/input.component';
import { AccountTypeService } from '../../../../../core/services/account-type.service';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { AccountTypeRequest } from '../../../../../core/models/account-type.model';
import { ToastrService } from '../../../../../shared/service/toastr/toastr.service';
import { NumberComponent } from '../../../../../shared/components/form/number/number.component';

@Component({
  selector: 'app-account-type-form',
  imports: [
    MatIconModule,
    MatButtonModule,
    FormsModule,
    ReactiveFormsModule,
    InputComponent,
    NumberComponent,
  ],
  templateUrl: './account-type-form.component.html',
  styleUrl: './account-type-form.component.css',
})
export class AccountTypeFormComponent {
  fb = inject(FormBuilder);
  accountTypeService = inject(AccountTypeService);
  toastr = inject(ToastrService);
  errorEvent = output<string>();

  mode = signal<string>('create');

  name = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  code = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.pattern('^[0-9]{2}$')],
  });
  accountFee = new FormControl<number>(0.0, {
    nonNullable: true,
  });
  interestRate = new FormControl<number>(0.0, {
    nonNullable: true,
  });
  minimumBalance = new FormControl<number>(0.0, {
    nonNullable: true,
  });

  accountTypeForm: FormGroup = this.fb.nonNullable.group({
    name: this.name,
    code: this.code,
    accountFee: this.accountFee,
    interestRate: this.interestRate,
    minimumBalance: this.minimumBalance,
  });

  sending = signal<boolean>(false);

  constructor() {
    effect(() => {
      if (this.accountTypeService.accountTypeUpdated()) {
        this.mode.update((mode) => (mode = 'edit'));
        this.accountTypeForm.patchValue({
          name: this.accountTypeService.accountTypeUpdated()?.name,
          code: this.accountTypeService.accountTypeUpdated()?.code,
          accountFee: this.accountTypeService.accountTypeUpdated()?.accountFee,
          interestRate:
            this.accountTypeService.accountTypeUpdated()?.interestRate,
          minimumBalance:
            this.accountTypeService.accountTypeUpdated()?.minimumBalance,
        });
      }
    });
  }

  addAccountType(formDirective: FormGroupDirective): void {
    if (this.accountTypeForm.valid) {
      const request: AccountTypeRequest = this.accountTypeForm.value;
      this.sending.set(true);
      this.accountTypeService.create(request).subscribe({
        next: (resp) => {
          this.sending.set(false);
          formDirective.resetForm();
          this.toastr.show('Ajout du nouveau type du compte réussi', 'SUCCESS');
        },
        error: (err) => {
          this.sending.set(false);
          this.errorEvent.emit(err.message);
        },
      });
    }
  }

  updateAccountType(formDirective: FormGroupDirective): void {
    if (this.accountTypeForm.valid) {
      const request: AccountTypeRequest = this.accountTypeForm.value;
      this.sending.set(true);
      const accountTypeId =
        this.accountTypeService.accountTypeUpdated()?.id || '';
      this.accountTypeService.update(request, accountTypeId).subscribe({
        next: (resp) => {
          this.mode.update((mode) => (mode = 'create'));
          this.sending.set(false);
          formDirective.resetForm();
          this.accountTypeService.resetAccountTypeUpdated();
          this.toastr.show('Type du compte modifié', 'SUCCESS');
        },
        error: (err) => {
          this.sending.set(false);
          this.errorEvent.emit(err.message);
        },
      });
    }
  }
}
