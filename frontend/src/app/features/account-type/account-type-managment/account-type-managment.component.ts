import { Component, effect, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { AccountTypeFormComponent } from '../ui/account-type-form/account-type-form.component';
import { AccountTypeListComponent } from '../ui/account-type-list/account-type-list.component';
import { MessageBoxComponent } from '../../../shared/components/message-box/message-box.component';
import { ToastrService } from '../../../core/services/toastr/toastr.service';
import { AccountTypeService } from '../data-access/account-type.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { AccountTypeResponse } from '../model/account-type.model';

@Component({
  selector: 'app-account-type-managment',
  imports: [
    MatIconModule,
    AccountTypeFormComponent,
    AccountTypeListComponent,
    MessageBoxComponent,
    LoaderComponent,
  ],
  templateUrl: './account-type-managment.component.html',
  styleUrl: './account-type-managment.component.css',
})
export class AccountTypeManagmentComponent {
  accountTypeService = inject(AccountTypeService);
  toastr = inject(ToastrService);
  errorMsg = signal<string>('');
  deleting = signal<boolean>(false);
  sending = signal<boolean>(false);
  mode = signal<string>('create');

  accountTypeState = toSignal(this.accountTypeService.findAll(), {
    initialValue: { loading: false, error: '', data: [] },
  });

  constructor() {
    effect(() => {
      if (this.accountTypeState()?.error) {
        this.errorMsg.set(this.accountTypeState()?.error || '');
      }
    });
    effect(() => {
      if (this.accountTypeService.accountTypeUpdated()) {
        this.mode.update((mode) => (mode = 'edit'));
      } else {
        this.mode.update((mode) => (mode = 'create'));
      }
    });
  }

  deleteType(accountTypeId: string): void {
    this.deleting.set(true);
    this.accountTypeService.delete(accountTypeId).subscribe({
      next: (resp) => {
        this.deleting.set(false);
        this.toastr.show(resp.message, 'SUCCESS');
      },
      error: (err) => {
        this.deleting.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  createAccountType(data: any): void {
    this.sending.set(true);
    this.accountTypeService.create(data.request).subscribe({
      next: (resp) => {
        this.sending.set(false);
        data.formDirective.resetForm();
        this.toastr.show('Ajout du nouveau type du compte réussi', 'SUCCESS');
      },
      error: (err) => {
        this.sending.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  updateAccountType(data: any): void {
    this.sending.set(true);
    this.accountTypeService.update(data.request, data.accountTypeId).subscribe({
      next: (resp) => {
        this.sending.set(false);
        data.formDirective.resetForm();
        this.accountTypeService.resetAccountTypeUpdated();
        this.toastr.show('Type du compte modifié', 'SUCCESS');
      },
      error: (err) => {
        this.sending.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  editType(accountType: AccountTypeResponse): void {
    this.accountTypeService.editAccountType(accountType);
  }

  closeMessageBox(value: any) {
    this.errorMsg.set('');
  }
}
