import { Component, OnInit, inject, output, signal } from '@angular/core';
import { AccountTypeService } from '../../../../../core/services/account-type.service';
import { ToastrService } from '../../../../../shared/service/toastr/toastr.service';
import { AccountTypeResponse } from '../../../../../core/models/account-type.model';
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
export class AccountTypeListComponent implements OnInit {
  accountTypeService = inject(AccountTypeService);
  toastr = inject(ToastrService);
  errorEvent = output<string>();

  loading = signal<boolean>(false);
  deleting = signal<boolean>(false);

  constructor() {}

  ngOnInit(): void {
    this.loading.set(true);
    this.accountTypeService.findAll().subscribe({
      next: (resp) => {
        this.loading.set(false);
        console.log(resp);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorEvent.emit(err.message);
      },
    });
  }

  deleteType(accountType: AccountTypeResponse) {
    this.deleting.set(true);
    this.accountTypeService.delete(accountType.id).subscribe({
      next: (resp) => {
        this.deleting.set(false);
        this.toastr.show(resp.message, 'SUCCESS');
      },
      error: (err) => {
        this.deleting.set(false);
        this.errorEvent.emit(err.message);
      },
    });
  }

  editType(accountType: AccountTypeResponse) {
    this.accountTypeService.editAccountType(accountType);
  }
}
