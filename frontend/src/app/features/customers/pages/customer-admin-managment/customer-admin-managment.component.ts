import { Component, effect, inject, signal } from '@angular/core';
import { CustomerMinResponse } from '../../models/customer.model';
import { CustomerService } from '../../data-access/customer.service';
import { ToastrService } from '../../../../core/services/toastr/toastr.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { SearchBarComponent } from '../../../../shared/components/search-bar/search-bar.component';
import { DatePipe, NgClass } from '@angular/common';
import { UpdateStatusClientComponent } from '../../ui/update-status-client/update-status-client.component';

@Component({
  selector: 'app-customer-admin-managment',
  imports: [
    MatIconModule,
    MatButtonModule,
    MatDialogModule,
    MessageBoxComponent,
    PaginationComponent,
    LoaderComponent,
    SearchBarComponent,
    NgClass,
    DatePipe,
  ],
  templateUrl: './customer-admin-managment.component.html',
  styleUrl: './customer-admin-managment.component.css',
})
export class CustomerAdminManagmentComponent {
  customerService = inject(CustomerService);
  toastrService = inject(ToastrService);
  dialog = inject(MatDialog);
  router = inject(Router);

  loading = signal<boolean>(false);
  errorMsg = signal<string>('');

  currentSearch: string = '';
  currentPage: number = 0;
  size: number = 6;
  totalPages: number = 0;
  clients: CustomerMinResponse[] = [];
  totalElements: number = 0;

  constructor() {
    effect(() => {
      const findAllCustomerState = this.customerService.findAllCustomerState$();
      if (findAllCustomerState.status === 'OK') {
        this.loading.set(false);
        this.clients = findAllCustomerState.value?.content || [];
        this.totalElements = findAllCustomerState.value?.totalElements || 0;
        this.totalPages = findAllCustomerState.value?.totalPages || 0;
      }
      if (findAllCustomerState.status === 'ERROR') {
        this.loading.set(false);
        this.errorMsg.set(findAllCustomerState.error || '');
      }
    });
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.loadClientList();
  }

  loadClientList(): void {
    this.customerService.findAllCustomer(
      this.currentSearch,
      this.currentPage,
      this.size
    );
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadClientList();
  }

  doSearch(value: string): void {
    console.log(value);
    this.currentSearch = value;
    this.currentPage = 0;
    this.loadClientList();
  }

  closeErrorMsg(value: boolean): void {
    this.errorMsg.set('');
  }

  addClient(): void {
    this.customerService.initCreateClientState();
    this.router.navigateByUrl('/my-app/admin/create-client');
  }

  viewDetails(client: CustomerMinResponse): void {
    this.router.navigateByUrl('/my-app/admin/client/' + client.id);
  }

  changeStatus(client: CustomerMinResponse): void {
    this.customerService.initUpdateCustomerStatusState();
    const dialogRef = this.dialog.open(UpdateStatusClientComponent, {
      width: '450px',
      maxWidth: '95vw',
      data: {
        id: client.id,
        firstName: client.firstName,
        lastName: client.lastName,
        photo: '',
        dateOfBirth: client.dateOfBirth,
        status: client.status,
        createdDate: client.createdDate,
        lastModifiedDate: client.lastModifiedDate,
      },
    });
    dialogRef.afterClosed().subscribe((resp) => {
      if (resp === 'modifié') {
        this.loadClientList();
      }
    });
  }

  getStatusStyles(status: string) {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-700 border-green-200';
      case 'PENDING':
        return 'bg-amber-100 text-amber-700 border-amber-200';
      case 'SUSPENDED':
        return 'bg-red-100 text-red-700 border-red-200';
      default:
        return 'bg-gray-200 text-gray-800 border-gray-300';
    }
  }
}
