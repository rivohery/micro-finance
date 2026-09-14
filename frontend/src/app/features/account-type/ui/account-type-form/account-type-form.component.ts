import {
  Component,
  effect,
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
import { MatIconModule } from '@angular/material/icon';
import { InputComponent } from '../../../../shared/components/form/input/input.component';
import { AccountTypeService } from '../../data-access/account-type.service';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { AccountTypeRequest } from '../../model/account-type.model';
import { ToastrService } from '../../../../core/services/toastr/toastr.service';
import { NumberComponent } from '../../../../shared/components/form/number/number.component';

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
  mode = input<string>('create');
  sending = input<boolean>(false);
  onCreateAccountType = output<any>();
  onUpdateAccountType = output<any>();

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

  constructor() {
    effect(() => {
      if (this.accountTypeService.accountTypeUpdated()) {
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
      this.onCreateAccountType.emit({ request, formDirective });
    }
  }

  updateAccountType(formDirective: FormGroupDirective): void {
    if (this.accountTypeForm.valid) {
      const request: AccountTypeRequest = this.accountTypeForm.value;
      const accountTypeId =
        this.accountTypeService.accountTypeUpdated()?.id || '';
      this.onUpdateAccountType.emit({ request, accountTypeId, formDirective });
    }
  }
}
