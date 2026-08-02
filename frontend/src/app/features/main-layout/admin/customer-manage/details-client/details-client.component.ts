import {
  Component,
  OnInit,
  effect,
  inject,
  input,
  signal,
} from '@angular/core';

import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import {
  CustomerResponse,
  CustomerStatus,
  initCustomerResponse,
} from '../../../../../core/models/clients.model';
import { ClientsService } from '../../../../../core/services/clients.service';
import { LoaderComponent } from '../../../../../shared/components/loader/loader.component';
import { MessageBoxComponent } from '../../../../../shared/components/message-box/message-box.component';
import { Router } from '@angular/router';
import { ToastrService } from '../../../../../shared/service/toastr/toastr.service';

@Component({
  selector: 'app-details-client',
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    MatChipsModule,
    LoaderComponent,
    MessageBoxComponent,
  ],
  templateUrl: './details-client.component.html',
  styleUrl: './details-client.component.css',
})
export class DetailsClientComponent implements OnInit {
  clientId = input<string>();
  clientService = inject(ClientsService);
  router = inject(Router);
  toastr = inject(ToastrService);

  loading = signal<boolean>(false);
  errorMsg = signal<string>('');

  closingAccount = signal<boolean>(false);
  client: CustomerResponse = initCustomerResponse;

  constructor() {
    effect(() => {
      const findCustomerDetailsByIdState =
        this.clientService.findCustomerDetailsByIdState$();
      if (findCustomerDetailsByIdState.status === 'OK') {
        this.loading.set(false);
        this.client =
          findCustomerDetailsByIdState.value || initCustomerResponse;
      }
      if (findCustomerDetailsByIdState.status === 'ERROR') {
        this.loading.set(false);
        this.errorMsg.set(findCustomerDetailsByIdState.error || '');
      }
    });

    effect(() => {
      if (this.clientService.closeCustomerAccountState$().status === 'OK') {
        this.closingAccount.set(false);
        this.toastr.show(
          this.clientService.closeCustomerAccountState$().value?.message || '',
          'SUCCESS'
        );
        this.router.navigateByUrl('/my-app/admin/clients');
      }
      if (this.clientService.closeCustomerAccountState$().status === 'ERROR') {
        this.closingAccount.set(false);
        this.errorMsg.set(
          this.clientService.closeCustomerAccountState$().error || ''
        );
      }
    });
  }

  ngOnInit(): void {
    this.clientService.initCloseCustomerAccountState();
    if (this.clientId()) {
      const clientId = this.clientId() || '';
      this.loading.set(true);
      this.clientService.findCustomerDetailsById(clientId);
    }
  }

  getStatusClass(status: string) {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-700 border-green-200';
      case 'PENDING':
        return 'bg-amber-100 text-amber-700 border-amber-200';
      case 'SUSPENDED':
        return 'bg-red-100 text-red-700 border-red-200';
      default:
        return 'bg-gray-100 text-gray-700 border-gray-200';
    }
  }

  goBackToList(): void {
    this.router.navigateByUrl('/my-app/admin/clients');
  }

  closeErrorMsg(close: boolean): void {
    this.errorMsg.set('');
  }

  editClient(client: CustomerResponse): void {
    this.clientService.findCustomerDetailsById(client.id);
    this.router.navigateByUrl(`/my-app/admin/update-client/${client.id}`);
  }

  closeAccount(client: CustomerResponse): void {
    /*if (client.status === 'ACTIVE') {
      this.toastr.show(
        'Ce client est active,on ne peut pas clotuter son compte',
        'ERROR'
      );
      return;
    }*/
    this.closingAccount.set(true);
    this.clientService.closeCustomerAccount(client.id);
  }
}
