import { Component, input } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NgxControlError } from 'ngxtension/control-error';
import { FieldError } from '../../../models/shared.model';

@Component({
  selector: 'app-input',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    FormsModule,
    ReactiveFormsModule,
    NgxControlError,
  ],
  templateUrl: './input.component.html',
  styleUrl: './input.component.css',
})
export class InputComponent {
  label = input.required<string>();
  control = input.required<FormControl>();
  icon = input<string>('');
  hint = input<string>('');
  placeholder = input<string>('');
  errors = input<FieldError[]>([]);
  type = input<string>('text');
  iconPosition = input<string>('prefix');

  get invalidControl(): boolean {
    return (
      this.control() &&
      this.control()?.invalid &&
      (this.control()?.touched || this.control()?.dirty)
    );
  }
}
