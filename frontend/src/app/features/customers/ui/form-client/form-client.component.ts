import {
  Component,
  OnInit,
  effect,
  inject,
  input,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  CustomerRequest,
  CustomerResponse,
  initCustomerResponse,
} from '../../models/customer.model';
import { CommonModule, DatePipe } from '@angular/common';
import { CustomerService } from '../../data-access/customer.service';
import { UploadImageComponent } from '../../../../shared/components/upload-image/upload-image.component';
import { InputComponent } from '../../../../shared/components/form/input/input.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MessageBoxComponent } from '../../../../shared/components/message-box/message-box.component';
import { Router } from '@angular/router';
import { ToastrService } from '../../../../core/services/toastr/toastr.service';

@Component({
  selector: 'app-form-client',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatButtonModule,
    MatDividerModule,
    InputComponent,
    MessageBoxComponent,
    UploadImageComponent,
  ],
  providers: [DatePipe],
  templateUrl: './form-client.component.html',
  styleUrl: './form-client.component.css',
})
export class FormClientComponent implements OnInit {
  mode = input<string>('create');
  editedCustomer = input<CustomerResponse>(initCustomerResponse);

  fb = inject(FormBuilder);
  datePipe = inject(DatePipe);
  clientService = inject(CustomerService);
  router = inject(Router);
  toastr = inject(ToastrService);

  sending = signal<boolean>(false);
  errorMsg = signal<string>('');

  selectedImage!: File;

  lastName: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  firstName: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  username: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  dateOfBirth: FormControl<Date> = new FormControl(new Date(), {
    nonNullable: true,
    validators: [Validators.required],
  });

  cin: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.pattern('^[0-9]{12}$')],
  });

  phoneNumber: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  email: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.email],
  });

  occupation: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  addressValue: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  addressZipCode: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  addressCity: FormControl<string> = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  addressCountry: FormControl<string> = new FormControl('Madagascar', {
    nonNullable: true,
    validators: [Validators.required],
  });

  clientForm: FormGroup = this.fb.nonNullable.group({
    lastName: this.lastName,
    firstName: this.firstName,
    username: this.username,
    dateOfBirth: this.dateOfBirth,
    cin: this.cin,
    // Contact & Pro
    phoneNumber: this.phoneNumber,
    email: this.email,
    occupation: this.occupation,
    // Adresse
    addressValue: this.addressValue,
    addressZipCode: this.addressZipCode,
    addressCity: this.addressCity,
    addressCountry: this.addressCountry,
  });

  constructor() {
    effect(() => {
      if (this.mode() === 'update' && this.editedCustomer().id) {
        this.clientForm.patchValue({
          lastName: this.editedCustomer().lastName,
          firstName: this.editedCustomer().firstName,
          dateOfBirth: this.editedCustomer().dateOfBirth,
          username: this.editedCustomer().username,
          cin: this.editedCustomer().cin,
          // Contact & Pro
          phoneNumber: this.editedCustomer().phoneNumber,
          email: this.editedCustomer().email,
          occupation: this.editedCustomer().occupation,
          // Adresse
          addressValue: this.editedCustomer().addressValue,
          addressZipCode: this.editedCustomer().addressZipCode,
          addressCity: this.editedCustomer().addressCity,
          addressCountry: this.editedCustomer().addressCountry,
        });
      }
    });

    effect(() => {
      const createClientState = this.clientService.createClientState$();
      if (createClientState.status === 'OK') {
        this.sending.set(false);
        this.toastr.show(createClientState.value?.message || '', 'SUCCESS');
        this.router.navigateByUrl(
          `/my-app/admin/client/${createClientState.value?.data.clientId}`
        );
      }
      if (createClientState.status === 'ERROR') {
        this.sending.set(false);
        this.errorMsg.set(createClientState.error || '');
      }
    });

    effect(() => {
      const updateClientState = this.clientService.updateClientState$();
      if (updateClientState.status === 'OK') {
        this.sending.set(false);
        this.toastr.show(updateClientState.value?.message || '', 'SUCCESS');
        this.router.navigateByUrl(
          `/my-app/admin/client/${updateClientState.value?.data.clientId}`
        );
      }
      if (updateClientState.status === 'ERROR') {
        this.sending.set(false);
        this.errorMsg.set(updateClientState.error || '');
      }
    });
  }

  ngOnInit(): void {
    this.clientService.initCreateClientState();
    this.clientService.initUpdateClientState();
  }

  closeErrorMsg(close: boolean): void {
    this.errorMsg.set('');
  }

  checkImage(image: File): void {
    this.selectedImage = image;
  }

  onCreate() {
    if (this.clientForm.invalid) {
      this.errorMsg.set('Certaines champs sont invalides ou incomplète');
      return;
    }
    const dateBrute: Date = this.clientForm.value.dateOfBirth;

    const dateFormatee = this.datePipe.transform(dateBrute, 'yyyy-MM-dd'); //format iso: yyyy-MM-dd

    const customerInfo: CustomerRequest = {
      ...this.clientForm.getRawValue(),
      dateOfBirth: dateFormatee,
    };

    console.log(customerInfo);
    const formData = new FormData();
    formData.append('customerInfo', JSON.stringify(customerInfo));
    formData.append('file', this.selectedImage);
    this.sending.set(true);
    this.clientService.createClient(formData);
  }

  onUpdate(): void {
    if (this.clientForm.invalid) {
      this.errorMsg.set('Certaines champs sont invalides ou incomplète');
      return;
    }
    const dateBrute: Date = this.clientForm.value.dateOfBirth;
    const dateFormatee = this.datePipe.transform(dateBrute, 'yyyy-MM-dd'); //format iso: yyyy-MM-dd
    const customerInfo: CustomerRequest = {
      ...this.clientForm.getRawValue(),
      dateOfBirth: dateFormatee,
      userId: this.editedCustomer().userId,
    };
    console.log(customerInfo);
    const formdata = new FormData();
    formdata.append('customerId', this.editedCustomer().id);
    formdata.append('customerInfo', JSON.stringify(customerInfo));
    formdata.append('file', this.selectedImage);
    this.sending.set(true);
    this.clientService.updateClient(formdata);
  }

  onCancel(): void {
    this.router.navigateByUrl('/my-app/admin/clients');
  }
}
