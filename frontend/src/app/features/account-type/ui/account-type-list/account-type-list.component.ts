import { Component, input, output } from '@angular/core';
import { AccountTypeResponse } from '../../model/account-type.model';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-account-type-list',
  imports: [MatButtonModule, MatIconModule, MatTooltipModule, CommonModule],
  templateUrl: './account-type-list.component.html',
  styleUrl: './account-type-list.component.css',
})
export class AccountTypeListComponent {
  accountTypeList = input<AccountTypeResponse[]>([]);
  deleting = input<boolean>(false);
  onDeleteEvent = output<string>();
  onEditEvent = output<AccountTypeResponse>();

  deleteType(accountType: AccountTypeResponse) {
    this.onDeleteEvent.emit(accountType.id);
  }

  editType(accountType: AccountTypeResponse) {
    this.onEditEvent.emit(accountType);
  }
}
