import { Component, input } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';

export type Option = {
  label: string;
  value: string;
};

@Component({
  selector: 'app-select',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
  ],
  templateUrl: './select.component.html',
  styleUrl: './select.component.css',
})
export class SelectComponent {
  label = input.required<string>();
  matlabel = input.required<string>();
  control = input.required<FormControl>();
  icon = input<string>('');
  hint = input<string>('');
  options = input.required<Option[]>();
}
