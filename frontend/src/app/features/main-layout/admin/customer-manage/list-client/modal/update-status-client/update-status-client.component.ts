import { Component, Inject, effect, inject, signal } from '@angular/core';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatRadioModule } from '@angular/material/radio';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import {
  CustomerMinResponse,
  CustomerStatus,
  UpdateStatusClientRequest,
} from '../../../../../../../core/models/clients.model';
import { FormsModule } from '@angular/forms';
import { NgClass } from '@angular/common';
import { ClientsService } from '../../../../../../../core/services/clients.service';
import { ToastrService } from '../../../../../../../shared/service/toastr/toastr.service';

@Component({
  selector: 'app-update-status-client',
  imports: [
    MatButtonModule,
    MatRadioModule,
    MatDialogModule,
    MatIconModule,
    FormsModule,
    NgClass,
  ],
  templateUrl: './update-status-client.component.html',
  styleUrl: './update-status-client.component.css',
})
export class UpdateStatusClientComponent {
  selectedStatus!: CustomerStatus;
  clientService = inject(ClientsService);
  toastr = inject(ToastrService);

  loading = signal<boolean>(false);

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: CustomerMinResponse,
    private dialogRef: MatDialogRef<UpdateStatusClientComponent>
  ) {
    this.selectedStatus = data.status;

    effect(() => {
      const updateCustomerStatusState =
        this.clientService.updateCustomerStatusState$();
      if (updateCustomerStatusState.status === 'OK') {
        this.loading.set(false);
        this.toastr.show(
          updateCustomerStatusState.value?.message || '',
          'SUCCESS'
        );
        this.dialogRef.close('modifié');
      }
      if (updateCustomerStatusState.status === 'ERROR') {
        this.loading.set(false);
        this.toastr.show(updateCustomerStatusState.error || '', 'ERROR');
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close('annulé');
  }

  onConfirm(): void {
    if (this.selectedStatus === 'PENDING') {
      this.toastr.show('On ne peut pas modifier le status en PENDING', 'ERROR');
      return;
    }
    this.loading.set(true);
    const request: UpdateStatusClientRequest = {
      id: this.data.id,
      status: this.selectedStatus,
    };
    this.clientService.updateCustomerStatus(request);
  }
}
