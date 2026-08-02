import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { ClientsService } from '../../../../../core/services/clients.service';
import { ToastrService } from '../../../../../shared/service/toastr/toastr.service';
import { CustomerMinResponse } from '../../../../../core/models/clients.model';
import { MessageBoxComponent } from '../../../../../shared/components/message-box/message-box.component';
import { PaginationComponent } from '../../../../../shared/components/pagination/pagination.component';
import { LoaderComponent } from '../../../../../shared/components/loader/loader.component';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import {
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { DatePipe, NgClass } from '@angular/common';
import { UpdateStatusClientComponent } from './modal/update-status-client/update-status-client.component';
import { Router } from '@angular/router';
import { SearchBarComponent } from '../../../../../shared/components/search-bar/search-bar.component';

@Component({
  selector: 'app-list-client',
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
  templateUrl: './list-client.component.html',
  styleUrl: './list-client.component.css',
})
export class ListClientComponent implements OnInit {
  clientsService = inject(ClientsService);
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
      const findAllCustomerState = this.clientsService.findAllCustomerState$();
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
    this.clientsService.findAllCustomer(
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
    this.clientsService.initCreateClientState();
    this.router.navigateByUrl('/my-app/admin/create-client');
  }

  viewDetails(client: CustomerMinResponse): void {
    this.router.navigateByUrl('/my-app/admin/client/' + client.id);
  }

  changeStatus(client: CustomerMinResponse): void {
    this.clientsService.initUpdateCustomerStatusState();
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
