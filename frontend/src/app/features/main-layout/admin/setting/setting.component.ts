import { Component, signal } from '@angular/core';
import { AccountTypeFormComponent } from './account-type-form/account-type-form.component';
import { AccountTypeListComponent } from './account-type-list/account-type-list.component';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-setting',
  imports: [
    MatIconModule,
    AccountTypeFormComponent,
    AccountTypeListComponent,
    MessageBoxComponent,
  ],
  templateUrl: './setting.component.html',
  styleUrl: './setting.component.css',
})
export class SettingComponent {
  errorMsg = signal<string>('');

  displayError(errorMsg: string): void {
    this.errorMsg.set(errorMsg);
  }

  closeMessageBox(value: any) {
    this.errorMsg.set('');
  }
}
