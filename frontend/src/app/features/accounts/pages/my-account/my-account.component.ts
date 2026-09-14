import { Component, effect, inject, signal } from '@angular/core';
import { AccountService } from '../../data-access/account.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { AccountCardListComponent } from '../../ui/account-card-list/account-card-list.component';

@Component({
  selector: 'app-my-account',
  imports: [LoaderComponent, MessageBoxComponent, AccountCardListComponent],
  templateUrl: './my-account.component.html',
  styleUrl: './my-account.component.css',
})
export class MyAccountComponent {
  accountService = inject(AccountService);

  accountState = toSignal(this.accountService.findMyAccounts(), {
    initialValue: { loading: true, error: '', data: [] },
  });
  errorMsg = signal<string>('');

  constructor() {
    effect(() => {
      if (this.accountState()?.error) {
        this.errorMsg.set(this.accountState()?.error || '');
      }
    });
  }

  closeErrorMsg(value: any) {
    this.errorMsg.set('');
  }
}
