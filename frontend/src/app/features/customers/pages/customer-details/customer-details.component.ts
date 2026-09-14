import {
  Component,
  OnInit,
  effect,
  inject,
  input,
  signal,
} from '@angular/core';
import { CustomerService } from '../../data-access/customer.service';
import {
  CustomerMinResponse,
  DetailCustomerWithAccount,
  initCustomerValue,
} from '../../models/customer.model';
import {
  AccountResponse,
  initAccountResponse,
} from '../../../accounts/model/account.model';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { PhoneFormatPipe } from '../../../../shared/pipes/phone-format.pipe';
import { AccountCardListComponent } from '../../../accounts/ui/account-card-list/account-card-list.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { CreateAccountModalComponent } from '../../ui/create-account-modal/create-account-modal.component';
import { AccountService } from '../../../accounts/data-access/account.service';
import { ToastrService } from '../../../../core/services/toastr/toastr.service';

@Component({
  selector: 'app-customer-details',
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatDialogModule,
    LoaderComponent,
    MessageBoxComponent,
    PhoneFormatPipe,
    AccountCardListComponent,
  ],
  templateUrl: './customer-details.component.html',
  styleUrl: './customer-details.component.css',
})
export class CustomerDetailsComponent {
  customerId = input<string>(); // from route

  customerService = inject(CustomerService);
  accountService = inject(AccountService);
  toastr = inject(ToastrService);
  dialog = inject(MatDialog);
  loading = signal<boolean>(false);
  errorMsg = signal<string>('');

  accounts = signal<AccountResponse[]>([]);
  customer = signal<CustomerMinResponse>(initCustomerValue);

  currentCustomerId!: string;

  constructor() {
    effect(() => this.handleInitComponentState());
  }

  private handleInitComponentState(): void {
    this.currentCustomerId = this.customerId() || '';
    if (this.currentCustomerId && this.currentCustomerId.length) {
      this.loading.set(true);
      this.loadCustomerInfosWithAccounts(this.currentCustomerId);
    }
  }

  private loadCustomerInfosWithAccounts(customerId: string): void {
    this.customerService.findDetailsClientWithAccount(customerId).subscribe({
      next: (resp: DetailCustomerWithAccount) => {
        console.log(resp);
        this.accounts.set(resp.accounts);
        this.customer.set(resp.customer);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMsg.set(err.message);
      },
    });
  }

  getStatusClass(status: string) {
    switch (status) {
      case 'ACTIVE':
        return 'bg-emerald-100 text-emerald-700 border-emerald-200';
      case 'PENDING':
        return 'bg-amber-100 text-amber-700 border-amber-200';
      case 'SUSPENDED':
        return 'bg-rose-100 text-rose-700 border-rose-200';
      default:
        return 'bg-gray-100 text-gray-700';
    }
  }

  onCreateNewAccount(customer: CustomerMinResponse) {
    const dialogRef = this.dialog.open(CreateAccountModalComponent, {
      width: '90%',
      maxWidth: '450px',
      disableClose: true,
      data: {
        customerId: customer.id,
      },
    });
    dialogRef.afterClosed().subscribe({
      next: (resp) => {
        if (resp.status === 'success') {
          this.loadCustomerInfosWithAccounts(resp.value);
        }
        if (resp.status === 'failed') {
          this.errorMsg.set(resp.value);
        }
      },
    });
  }

  closeErrorMsg(value: boolean): void {
    this.errorMsg.set('');
  }
}
