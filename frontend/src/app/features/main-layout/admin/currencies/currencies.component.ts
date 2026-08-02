import { Component, signal } from '@angular/core';
import { CurrencyFormComponent } from './currency-form/currency-form.component';
import { CurrencyListComponent } from './currency-list/currency-list.component';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';

@Component({
  selector: 'app-currencies',
  imports: [CurrencyFormComponent, CurrencyListComponent, MessageBoxComponent],
  templateUrl: './currencies.component.html',
  styleUrl: './currencies.component.css',
})
export class CurrenciesComponent {
  errorMsg = signal<string>('');

  displayErrorMsg(errorMsg: string) {
    this.errorMsg.set(errorMsg);
  }

  closeMsgbox(value: boolean) {
    this.errorMsg.set('');
  }
}
