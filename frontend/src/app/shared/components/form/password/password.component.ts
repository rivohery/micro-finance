import { Component, input } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NgxControlError } from 'ngxtension/control-error';
import { FieldError } from '../../../models/shared.model';

@Component({
  selector: 'app-password',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    NgxControlError,
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './password.component.html',
  styleUrl: './password.component.css',
})
export class PasswordComponent {
  label = input.required<string>();
  control = input.required<FormControl>();
  errors = input<FieldError[]>([]);

  hidePassword: boolean = true;

  get invalidControl(): boolean {
    return (
      this.control() &&
      this.control()?.invalid &&
      (this.control()?.touched || this.control()?.dirty)
    );
  }
}
