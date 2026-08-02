import { Component, effect, inject, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { injectAuthsStore } from '../../../core/stores/auths.facade';
import { LoginRequest } from '../auths.model';
import { MessageBoxComponent } from '../../../shared/components/message-box/message-box.component';
import { SubmitButtonComponent } from '../../../shared/components/submit-button/submit-button.component';
import { InputComponent } from '../../../shared/components/form/input/input.component';
import { PasswordComponent } from '../../../shared/components/form/password/password.component';
import { ToastrService } from '../../../shared/service/toastr/toastr.service';

@Component({
  selector: 'app-login',
  imports: [
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
    MessageBoxComponent,
    SubmitButtonComponent,
    InputComponent,
    PasswordComponent,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  hidePassword: boolean = true;
  fb = inject(FormBuilder);
  store = injectAuthsStore();
  toastr = inject(ToastrService);
  router = inject(Router);

  errorMsg = signal<string>('');

  username = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(4)],
  });
  password = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  loginForm!: FormGroup;

  constructor() {
    this.initLoginForm();

    effect(() => {
      if (this.store.errorMsg()) {
        this.errorMsg.set(this.store.errorMsg());
      }
      if (this.store.userInfos()) {
        const successMsg: string = this.store.successMsg() || '';
        this.toastr.show(successMsg, 'SUCCESS');
        this.router.navigateByUrl('/my-app/user-profil');
      }
    });
  }

  initLoginForm(): void {
    this.loginForm = this.fb.nonNullable.group({
      username: this.username,
      password: this.password,
    });
  }

  login(): void {
    const loginRequest: LoginRequest = this.loginForm.value;
    this.store.login(loginRequest);
  }

  closeMsgBox(close: boolean) {
    if (close) this.errorMsg.update((value) => '');
  }
}
