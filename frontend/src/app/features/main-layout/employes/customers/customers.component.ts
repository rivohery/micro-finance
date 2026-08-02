import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { ClientsService } from '../../../../core/services/clients.service';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';
import { SearchBarComponent } from '../../../../shared/components/search-bar/search-bar.component';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { DatePipe, NgClass } from '@angular/common';
import { CustomerMinResponse } from '../../../../core/models/clients.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-customers',
  imports: [
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    LoaderComponent,
    SearchBarComponent,
    MessageBoxComponent,
    PaginationComponent,
    NgClass,
    DatePipe,
  ],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css',
})
export class CustomersComponent implements OnInit {
  clientsService = inject(ClientsService);
  router = inject(Router);
  loading = signal<boolean>(false);
  errorMsg = signal<string>('');

  size: number = 6;
  currentSearch = signal<string>('');
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  clients = signal<CustomerMinResponse[]>([]);
  totalElements = signal<number>(0);

  constructor() {
    effect(() => this.handleFindAllEnableCustomersState());
  }

  ngOnInit(): void {
    this.loading.set(true);
    this.clientsService.initFindAllEnableCustomersState();
    this.clientsService.findAllEnableCustomers(
      this.currentSearch(),
      this.currentPage(),
      this.size
    );
  }

  private handleFindAllEnableCustomersState(): void {
    const findAllEnableCustomersState =
      this.clientsService.findAllEnableCustomersState$();
    if (
      findAllEnableCustomersState.status === 'OK' &&
      findAllEnableCustomersState.value
    ) {
      this.loading.set(false);
      this.clients.set(findAllEnableCustomersState.value.content || []);
      this.totalElements.set(
        findAllEnableCustomersState.value.totalElements || 0
      );
      this.totalPages.set(findAllEnableCustomersState.value.totalPages || 0);
    }
    if (findAllEnableCustomersState.status === 'ERROR') {
      this.loading.set(false);
      this.errorMsg.set(findAllEnableCustomersState.error || '');
    }
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.clientsService.findAllEnableCustomers(
      this.currentSearch(),
      this.currentPage(),
      this.size
    );
  }

  doSearch(value: string): void {
    console.log(value);
    this.currentSearch.set(value);
    this.currentPage.set(0);
    this.clientsService.findAllEnableCustomers(
      this.currentSearch(),
      this.currentPage(),
      this.size
    );
  }

  closeErrorMsg(value: boolean): void {
    this.errorMsg.set('');
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

  displayDetails(customer: CustomerMinResponse): void {
    this.router.navigateByUrl(
      `/my-app/managment/customer-details/${customer.id}`
    );
  }
}
