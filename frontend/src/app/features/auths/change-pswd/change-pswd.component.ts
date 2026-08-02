import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import {
  FormBuilder,
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { InputComponent } from '../../../shared/components/form/input/input.component';
import { PasswordComponent } from '../../../shared/components/form/password/password.component';
import { MessageBoxComponent } from '../../../shared/components/message-box/message-box.component';
import { SubmitButtonComponent } from '../../../shared/components/submit-button/submit-button.component';
import { UsersService } from '../../../core/services/users-service';
import { ToastrService } from '../../../shared/service/toastr/toastr.service';
import { ChangePasswordRequest } from '../auths.model';

@Component({
  selector: 'app-change-pswd',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    RouterLink,
    InputComponent,
    PasswordComponent,
    MessageBoxComponent,
    SubmitButtonComponent,
  ],
  templateUrl: './change-pswd.component.html',
  styleUrl: './change-pswd.component.css',
})
export class ChangePswdComponent implements OnInit {
  fb = inject(FormBuilder);
  usersService = inject(UsersService);
  toastr = inject(ToastrService);
  router = inject(Router);

  successMsg = signal<string>('');
  errorMsg = signal<string>('');
  loading = signal<boolean>(false);

  username = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  oldPassword = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  newPassword = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(4)],
  });

  changePswdForm = this.fb.nonNullable.group({
    username: this.username,
    oldPassword: this.oldPassword,
    newPassword: this.newPassword,
  });

  constructor() {
    effect(() => {
      const changePasswordState = this.usersService.changePasswordState$();
      if (changePasswordState.status === 'OK') {
        this.loading.set(false);
        this.toastr.show(changePasswordState.value?.message || '', 'SUCCESS');
        this.router.navigateByUrl('/login');
      }
      if (changePasswordState.status === 'OK') {
        this.loading.set(false);
        this.errorMsg.set(changePasswordState.error || '');
      }
    });
  }

  ngOnInit(): void {
    this.usersService.initChangePasswordState();
  }

  closeMsgBox(close: boolean): void {
    this.errorMsg.update((value) => '');
  }

  changePassword(): void {
    this.loading.set(true);
    const request: ChangePasswordRequest = {
      username: this.changePswdForm.value.username,
      oldPasswordPlain: this.changePswdForm.value.oldPassword,
      newPasswordPlain: this.changePswdForm.value.newPassword,
    };
    this.usersService.changePassword(request);
  }
}
