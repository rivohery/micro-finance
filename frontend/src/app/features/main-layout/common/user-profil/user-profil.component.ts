import { Component, OnInit, effect, inject, signal } from '@angular/core';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatChipsModule } from '@angular/material/chips';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { injectAuthsStore } from '../../../../core/stores/auths.facade';
import { ChangeProfileRequest, UserResponse } from '../../../auths/auths.model';
import { UsersService } from '../../../../core/services/users-service';
import { ToastrService } from '../../../../shared/service/toastr/toastr.service';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-user-profil',
  imports: [
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatDividerModule,
    MatChipsModule,
    FormsModule,
    ReactiveFormsModule,
    MessageBoxComponent,
    NgIf,
  ],
  templateUrl: './user-profil.component.html',
  styles: [
    `
      :host {
        display: block;
        padding: 20px;
      }
    `,
  ],
})
export class UserProfilComponent implements OnInit {
  authStore = injectAuthsStore();
  fb = inject(FormBuilder);
  userService = inject(UsersService);
  toastr = inject(ToastrService);

  username = new FormControl<string>('', {
    nonNullable: true,
    validators: Validators.required,
  });
  email = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });
  password = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(4)],
  });

  updating = signal<boolean>(false);
  errorMsg = signal<string>('');

  profileForm: FormGroup = this.fb.nonNullable.group({
    username: this.username,
    email: this.email,
    password: this.password,
  });
  userInfos: UserResponse | undefined;

  constructor() {
    if (this.authStore.userInfos()) {
      this.userInfos = this.authStore.userInfos() || undefined;
      this.profileForm.patchValue({
        username: this.userInfos?.username,
        email: this.userInfos?.email,
      });
    }

    effect(() => {
      const changeProfileState = this.userService.changeProfileState$();
      if (changeProfileState.status === 'OK') {
        this.updating.set(false);
        this.toastr.show(changeProfileState.value?.message || '', 'SUCCESS');
      }
      if (changeProfileState.status === 'ERROR') {
        this.updating.set(false);
        this.errorMsg.set(changeProfileState.error || '');
      }
    });
  }

  ngOnInit(): void {
    this.userService.initChangeProfileState();
  }

  get initials(): string {
    return this.authStore.userInfos()?.role === 'CLIENT'
      ? 'CL'
      : this.authStore.userInfos()?.role === 'ADMIN'
      ? 'AD'
      : 'EM';
  }

  closeErrorMsg(resp: any): void {
    this.errorMsg.set('');
  }

  onSave() {
    if (this.profileForm.valid) {
      this.updating.set(true);
      const request: ChangeProfileRequest = {
        id: this.userInfos?.id || '',
        email: this.profileForm.value.email,
        username: this.profileForm.value.username,
        password: this.profileForm.value.password,
      };
      console.log(request);
      this.userService.changeProfile(request);
    }
  }

  onCancel() {
    this.profileForm.reset({
      username: this.userInfos?.username,
      email: this.userInfos?.email,
      password: '',
    });
  }
}
