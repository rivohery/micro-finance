import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormClientComponent } from '../common/form-client/form-client.component';

@Component({
  selector: 'app-create-client',
  imports: [CommonModule, FormClientComponent],
  templateUrl: './create-client.component.html',
  styleUrl: './create-client.component.css',
})
export class CreateClientComponent {
  mode: string = 'create';
}
