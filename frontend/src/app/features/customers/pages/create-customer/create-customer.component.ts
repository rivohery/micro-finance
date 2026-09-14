import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormClientComponent } from '../../ui/form-client/form-client.component';

@Component({
  selector: 'app-create-customer',
  imports: [CommonModule, FormClientComponent],
  templateUrl: './create-customer.component.html',
  styleUrl: './create-customer.component.css',
})
export class CreateCustomerComponent {
  mode: string = 'create';
}
