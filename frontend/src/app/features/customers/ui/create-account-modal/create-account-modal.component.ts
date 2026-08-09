import {
  Component,
  Inject,
  OnInit,
  effect,
  inject,
  signal,
} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { CurrencyService } from '../../../currency/data-access/currency.service';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { AccountTypeService } from '../../../account-type/data-access/account-type.service';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ToastrService } from '../../../../core/services/toastr/toastr.service';
import { CreateAccountRequest } from '../../../accounts/model/account.model';
import {
  Option,
  SelectComponent,
} from '../../../../shared/components/form/select/select.component';
import { AccountService } from '../../../accounts/data-access/account.service';

@Component({
  selector: 'app-create-account-modal',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    MatDialogModule,
    MatSelectModule,
    MatFormFieldModule,
    MatButtonModule,
    MatIconModule,
    SelectComponent,
  ],
  templateUrl: './create-account-modal.component.html',
  styleUrl: './create-account-modal.component.css',
})
export class CreateAccountModalComponent implements OnInit {
  currencyService = inject(CurrencyService);
  accountTypeService = inject(AccountTypeService);
  accountService = inject(AccountService);
  toastr = inject(ToastrService);

  fb = inject(FormBuilder);

  loading = signal<boolean>(true);
  errorMsg = signal<string>('');

  creating = signal<boolean>(false);

  currencyOptions: Option[] = [];
  accountTypeOptions: Option[] = [];

  currencies = toSignal(this.currencyService.fetchEnableCurrency(), {
    initialValue: [],
  });

  currenciesLoaded = signal<boolean>(false);
  accountTypesLoaded = signal<boolean>(false);

  accountTypeCode = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  currencyCode = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  customerId = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  createAccountForm: FormGroup = this.fb.nonNullable.group({
    accountTypeCode: this.accountTypeCode,
    currencyCode: this.currencyCode,
    customerId: this.customerId,
  });

  constructor(
    private dialogRef: MatDialogRef<CreateAccountModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    effect(() => {
      const currencies = this.currencies();
      if (currencies) {
        this.currencyOptions = currencies.map((curr) => {
          return { label: curr.name, value: curr.code } as Option;
        });
        this.currenciesLoaded.set(true);
      }
      console.log(this.currenciesLoaded());
    });
    effect(() => {
      const accountTypes = this.accountTypeService.accountTypeList$();
      if (accountTypes?.length) {
        this.accountTypeOptions = accountTypes.map((accountType) => {
          return { label: accountType.name, value: accountType.code } as Option;
        });
        this.accountTypesLoaded.set(true);
      }
      console.log(this.accountTypesLoaded());
    });
    effect(() => {
      if (this.currenciesLoaded() && this.accountTypesLoaded()) {
        const customerId = this.data.customerId;
        this.createAccountForm.patchValue({
          customerId: customerId,
        });
        this.loading.set(false);
      }
    });
  }

  ngOnInit(): void {
    if (!this.accountTypeService.accountTypeList$().length) {
      this.accountTypeService.findAll();
    }
  }

  onCancel(): void {
    this.dialogRef.close({ status: 'cancel', value: '' });
  }

  onSubmit(): void {
    if (this.createAccountForm.invalid) {
      this.toastr.show('Données envoyer invalide', 'ERROR');
      return;
    }
    const request: CreateAccountRequest = {
      customerId: this.createAccountForm.value.customerId,
      accountTypeCode: this.createAccountForm.value.accountTypeCode,
      currencyCode: this.createAccountForm.value.currencyCode,
    };
    console.log(request);
    this.creating.set(true);
    this.accountService.createAccount(request).subscribe({
      next: (resp) => {
        this.creating.set(false);
        this.toastr.show(resp.message || '', 'SUCCESS');
        this.dialogRef.close({
          status: 'success',
          value: this.createAccountForm.value.customerId,
        });
      },
      error: (err) => {
        this.creating.set(false);
        this.dialogRef.close({ status: 'failed', value: err.message });
      },
    });
  }
}
