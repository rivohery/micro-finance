import { Component, effect, inject, signal } from '@angular/core';
import { CurrencyFormComponent } from '../ui/currency-form/currency-form.component';
import { CurrencyListComponent } from '../ui/currency-list/currency-list.component';
import { MessageBoxComponent } from '../../../shared/components/message-box/message-box.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { CurrencyService } from '../data-access/currency.service';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { ToastrService } from '../../../core/services/toastr/toastr.service';
import { UpdateCurrencyRequest } from '../model/currency.model';

@Component({
  selector: 'app-currency-managment',
  imports: [
    CurrencyFormComponent,
    CurrencyListComponent,
    MessageBoxComponent,
    LoaderComponent,
  ],
  templateUrl: './currency-managment.component.html',
  styleUrl: './currency-managment.component.css',
})
export class CurrencyManagmentComponent {
  errorMsg = signal<string>('');
  currencyService = inject(CurrencyService);
  toastr = inject(ToastrService);
  updating = signal<boolean>(false);
  deleting = signal<boolean>(false);
  sending = signal<boolean>(false);
  currencies = toSignal(this.currencyService.findAll(), {
    initialValue: { loading: false, error: '', data: [] },
  });

  constructor() {
    effect(() => {
      if (this.currencies()?.error) {
        this.errorMsg.set(this.currencies()?.error || '');
      }
    });
  }

  toggleStatus(request: UpdateCurrencyRequest): void {
    this.updating.set(true);
    this.currencyService.update(request).subscribe({
      next: (resp) => {
        this.updating.set(false);
        this.toastr.show('Modification du status reussie', 'SUCCESS');
      },
      error: (err) => {
        this.updating.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  deleteCurrency(currencyId: string): void {
    this.deleting.set(true);
    this.currencyService.delete(currencyId).subscribe({
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

  createNewCurrency(data: any): void {
    this.sending.set(true);
    this.currencyService.create(data.request).subscribe({
      next: (resp) => {
        this.toastr.show('Ajout du nouveau monnaie reussie', 'SUCCESS');
        data.formDirective.resetForm();
      },
      error: (err) => this.errorMsg.set(err.message),
      complete: () => this.sending.set(false),
    });
  }

  closeMsgbox(value: boolean) {
    this.errorMsg.set('');
  }
}
