import { Component, Inject, effect, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { CommonModule } from '@angular/common';
import {
  ChangeUserStatusRequest,
  UserResponse,
} from '../../../../../auths/auths.model';
import { UsersService } from '../../../../../../core/services/users-service';
import { ToastrService } from '../../../../../../shared/service/toastr/toastr.service';

@Component({
  selector: 'app-change-status',
  imports: [
    MatIconModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDialogModule,
    MatDividerModule,
    FormsModule,
    CommonModule,
  ],
  templateUrl: './change-status.component.html',
  styleUrl: './change-status.component.css',
})
export class ChangeStatusComponent {
  statusValue: boolean = true;
  userService = inject(UsersService);
  toastr = inject(ToastrService);

  updating = signal<boolean>(false);

  constructor(
    private dialogRef: MatDialogRef<ChangeStatusComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UserResponse
  ) {
    this.statusValue = data.enable;
    effect(() => {
      const changeUserStatusState = this.userService.changeUserStatusState$();
      if (changeUserStatusState.status === 'OK') {
        this.updating.set(false);
        this.toastr.show(changeUserStatusState.value?.message || '', 'SUCCESS');
        this.dialogRef.close('modifié');
      }
      if (changeUserStatusState.status === 'ERROR') {
        this.updating.set(false);
        this.toastr.show(changeUserStatusState.error || '', 'ERROR');
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close('annulé');
  }

  onConfirm(): void {
    this.updating.set(true);
    const request: ChangeUserStatusRequest = {
      userId: this.data.id,
      status: this.statusValue,
    };
    this.userService.changeUserStatus(request);
  }
}
