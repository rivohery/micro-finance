import { Component, effect, inject, signal } from '@angular/core';
import { CustomerResponse } from '../../models/customer.model';
import { CustomerService } from '../../data-access/customer.service';
import { FormClientComponent } from '../../ui/form-client/form-client.component';
import { LoaderComponent } from '../../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-update-customer',
  imports: [FormClientComponent, LoaderComponent],
  templateUrl: './update-customer.component.html',
  styleUrl: './update-customer.component.css',
})
export class UpdateCustomerComponent {
  mode: string = 'update';
  editedCustomer!: CustomerResponse;
  customerService = inject(CustomerService);
  loading = signal<boolean>(true);

  constructor() {
    effect(() => {
      const findCustomerDetailsByIdState =
        this.customerService.findCustomerDetailsByIdState$();
      if (findCustomerDetailsByIdState.value) {
        this.editedCustomer = findCustomerDetailsByIdState.value;
        this.loading.set(false);
      }
    });
  }
}
