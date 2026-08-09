import { Component, inject, input, output, signal } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormGroupDirective,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { CreateCurrencyRequest } from '../../model/currency.model';
import { InputComponent } from '../../../../shared/components/form/input/input.component';

@Component({
  selector: 'app-currency-form',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatCheckboxModule,
    MatIconModule,
    MatButtonModule,
    InputComponent,
  ],
  templateUrl: './currency-form.component.html',
  styleUrl: './currency-form.component.css',
})
export class CurrencyFormComponent {
  mode = signal<string>('add');
  fb = inject(FormBuilder);
  sending = input<boolean>(false);
  onCreateEvent = output<any>();

  name: FormControl = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required],
  });
  code: FormControl = new FormControl<string>('', {
    nonNullable: true,
    validators: [
      Validators.required,
      Validators.maxLength(3),
      Validators.pattern('^[A-Z]+$'),
    ],
  });

  currencyForm: FormGroup = this.fb.nonNullable.group({
    name: this.name,
    code: this.code,
  });

  get nameInvalid(): boolean {
    return this.name.invalid && (this.name.touched || this.name.dirty);
  }

  get codeInvalid(): boolean {
    return this.code.invalid && (this.code.touched || this.code.dirty);
  }

  addCurrency(formDirective: FormGroupDirective): void {
    if (this.currencyForm.valid) {
      const request = {
        code: this.currencyForm.value.code,
        name: this.currencyForm.value.name,
      } as CreateCurrencyRequest;
      this.onCreateEvent.emit({ request, formDirective });
    }
  }
}
