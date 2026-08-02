import { Component, effect, inject, signal } from '@angular/core';
import { InputComponent } from '../../../../../../shared/components/form/input/input.component';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogRef } from '@angular/material/dialog';
import { UserRequest } from '../../../../../auths/auths.model';
import { UsersService } from '../../../../../../core/services/users-service';
import { ToastrService } from '../../../../../../shared/service/toastr/toastr.service';
import { SubmitButtonComponent } from '../../../../../../shared/components/submit-button/submit-button.component';

@Component({
  selector: 'app-create-employe',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    InputComponent,
    SubmitButtonComponent,
  ],
  templateUrl: './create-employe.component.html',
  styleUrl: './create-employe.component.css',
})
export class CreateEmployeComponent {
  fb = inject(FormBuilder);
  dialogRef = inject(MatDialogRef<CreateEmployeComponent>);
  usersService = inject(UsersService);
  toastrService = inject(ToastrService);

  creating = signal<boolean>(false);

  username = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(4)],
  });

  email = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });

  createForm: FormGroup = this.fb.nonNullable.group({
    username: this.username,
    email: this.email,
  });

  constructor() {
    effect(() => {
      const createUserState = this.usersService.createUsersState$();
      if (createUserState.status === 'OK') {
        this.creating.set(false);
        this.dialogRef.close('ajouté');
      }
      if (createUserState.status === 'ERROR') {
        this.creating.set(false);
        this.toastrService.show(createUserState.error || '', 'ERROR');
      }
    });
  }

  onSubmit(): void {
    if (this.createForm.valid) {
      const request: UserRequest = {
        email: this.createForm.value.email,
        username: this.createForm.value.username,
      };
      this.creating.set(true);
      this.usersService.createUser(request);
    }
  }

  onCancel(): void {
    this.dialogRef.close('annulé');
  }
}
