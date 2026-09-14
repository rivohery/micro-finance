import { Component, input } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NgxControlError } from 'ngxtension/control-error';
import { FieldError } from '../../../models/shared.model';

@Component({
  selector: 'app-number',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    NgxControlError,
  ],
  templateUrl: './number.component.html',
  styleUrl: './number.component.css',
})
export class NumberComponent {
  control = input.required<FormControl>();
  label = input.required<string>();
  icon = input<string>('');
  iconPosition = input<string>('prefix');
  placeholder = input<string>('');
  hint = input<string>('');
  errors = input<FieldError[]>([]);
  min = input<number>(0.0);
  max = input<number>(100000000);
  step = input<number>(1.0);

  get invalidControl(): boolean {
    return (
      this.control() &&
      this.control()?.invalid &&
      (this.control()?.touched || this.control()?.dirty)
    );
  }
}
