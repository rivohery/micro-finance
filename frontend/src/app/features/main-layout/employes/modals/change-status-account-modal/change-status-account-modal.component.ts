import { CommonModule } from '@angular/common';
import {
  Component,
  Inject,
  OnInit,
  effect,
  inject,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { AccountService } from '../../../../../core/services/account.service';
import { AccountLifeCycleRequest } from '../../../../../core/models/account.model';
import { ToastrService } from '../../../../../shared/service/toastr/toastr.service';
import { MessageBoxComponent } from '../../../../../shared/components/message-box/message-box.component';

@Component({
  selector: 'app-change-status-account-modal',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatRadioModule,
    MatInputModule,
    MatFormFieldModule,
    MatButtonModule,
    MatIconModule,
    MessageBoxComponent,
  ],
  templateUrl: './change-status-account-modal.component.html',
  styleUrl: './change-status-account-modal.component.css',
})
export class ChangeStatusAccountModalComponent implements OnInit {
  fb = inject(FormBuilder);
  accountService = inject(AccountService);
  toastr = inject(ToastrService);

  statusForm!: FormGroup;
  submiting = signal<boolean>(false);
  errorMsg = signal<string>('');

  // Liste des statuts avec labels et descriptions pour guider l'employé
  statusOptions = [
    {
      value: 'PENDING',
      label: 'En attente (Pending)',
      desc: 'Le dossier attend les pièces justificatives.',
    },
    {
      value: 'ACTIVE',
      label: 'Actif (Active)',
      desc: 'Le compte peut réaliser toutes les opérations.',
    },
    {
      value: 'SUSPENDED',
      label: 'Suspendu (Suspended)',
      desc: 'Bloqué temporairement (ex: anomalie ou gel).',
    },
    {
      value: 'CLOSED',
      label: 'Clôturé (Closed)',
      desc: 'Fermeture définitive du compte financier.',
    },
  ];

  constructor(
    private dialogRef: MatDialogRef<ChangeStatusAccountModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.statusForm = this.fb.group({
      newStatus: [data?.status || 'PENDING', Validators.required],
      reason: ['', [Validators.required, Validators.minLength(10)]],
    });

    effect(() => {
      const activateAccountState = this.accountService.activateAccountState$();
      if (activateAccountState.status === 'OK') {
        this.submiting.set(false);
        this.toastr.show(activateAccountState.value?.message || '', 'SUCCESS');
        this.dialogRef.close('ACTIVE');
      }
      if (activateAccountState.status === 'ERROR') {
        this.errorMsg.set(activateAccountState.error || '');
        this.submiting.set(false);
      }
    });

    effect(() => {
      const suspendAccountState = this.accountService.suspendAccountState$();
      if (suspendAccountState.status === 'OK') {
        this.submiting.set(false);
        this.toastr.show(suspendAccountState.value?.message || '', 'SUCCESS');
        this.dialogRef.close('SUSPENDED');
      }
      if (suspendAccountState.status === 'ERROR') {
        this.errorMsg.set(suspendAccountState.error || '');
        this.submiting.set(false);
      }
    });

    effect(() => {
      const closeAccountState = this.accountService.closeAccountState$();
      if (closeAccountState.status === 'OK') {
        this.submiting.set(false);
        this.toastr.show(closeAccountState.value?.message || '', 'SUCCESS');
        this.dialogRef.close('CLOSED');
      }
      if (closeAccountState.status === 'ERROR') {
        this.submiting.set(false);
        this.errorMsg.set(closeAccountState.error || '');
      }
    });
  }

  ngOnInit(): void {
    this.accountService.initActivateAccountState();
    this.accountService.initCloseAccountState();
    this.accountService.initSuspendAccountState();
  }

  onSubmit() {
    if (this.statusForm.valid) {
      const request: AccountLifeCycleRequest = {
        accountId: this.data?.accountId,
        reason: this.statusForm.value.reason,
      };
      this.submiting.set(true);

      if (this.statusForm.value.newStatus === 'ACTIVE') {
        this.accountService.activateAccount(request);
      }
      if (this.statusForm.value.newStatus === 'SUSPENDED') {
        this.accountService.suspendAccount(request);
      }
      if (this.statusForm.value.newStatus === 'CLOSED') {
        this.accountService.closeAccount(request);
      }
    }
  }

  closeErrorMsg(res: any) {
    this.errorMsg.set('');
  }

  onCancel() {
    this.dialogRef.close('annulé');
  }
}
