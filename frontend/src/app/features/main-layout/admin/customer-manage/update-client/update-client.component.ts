import { Component, effect, inject, signal } from '@angular/core';
import { CustomerResponse } from '../../../../../core/models/clients.model';
import { ClientsService } from '../../../../../core/services/clients.service';
import { FormClientComponent } from '../common/form-client/form-client.component';
import { LoaderComponent } from '../../../../../shared/components/loader/loader.component';

@Component({
  selector: 'app-update-client',
  imports: [FormClientComponent, LoaderComponent],
  templateUrl: './update-client.component.html',
  styleUrl: './update-client.component.css',
})
export class UpdateClientComponent {
  mode: string = 'update';
  editedCustomer!: CustomerResponse;
  clientService = inject(ClientsService);
  loading = signal<boolean>(true);

  constructor() {
    effect(() => {
      const findCustomerDetailsByIdState =
        this.clientService.findCustomerDetailsByIdState$();
      if (findCustomerDetailsByIdState.value) {
        this.editedCustomer = findCustomerDetailsByIdState.value;
        this.loading.set(false);
      }
    });
  }
}
