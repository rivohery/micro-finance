import { NgClass, NgFor, NgIf } from '@angular/common';
import { Component, OnInit, inject, output, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
  CurrencyResponse,
  UpdateCurrencyRequest,
} from '../../../../../core/models/currency.model';
import { MatButtonModule } from '@angular/material/button';
import { CurrencyService } from '../../../../../core/services/currency.service';
import { ToastrService } from '../../../../../shared/service/toastr/toastr.service';

@Component({
  selector: 'app-currency-list',
  imports: [MatIconModule, MatTooltipModule, MatButtonModule, NgClass],
  templateUrl: './currency-list.component.html',
  styleUrl: './currency-list.component.css',
})
export class CurrencyListComponent implements OnInit {
  currencyService = inject(CurrencyService);
  toastr = inject(ToastrService);
  loading = signal<boolean>(false);
  errorEvent = output<string>();
  updating = signal<boolean>(false);
  deleting = signal<boolean>(false);

  ngOnInit(): void {
    this.loading.set(true);
    this.currencyService.findAll().subscribe({
      next: (resp) => {
        console.log(resp);
        this.loading.set(false);
      },
      error: (err) => this.errorEvent.emit(err.message),
    });
  }

  toggleStatus(currency: CurrencyResponse) {
    const request = {
      id: currency.id,
      code: currency.code,
      name: currency.name,
      enable: !currency.enable,
    } as UpdateCurrencyRequest;
    this.updating.set(true);
    this.currencyService.update(request).subscribe({
      next: (resp) => {
        this.updating.set(false);
        this.toastr.show('Modification du status reussie', 'SUCCESS');
      },
      error: (err) => {
        this.updating.set(false);
        this.errorEvent.emit(err.message);
      },
    });
  }

  deleteCurrency(currency: CurrencyResponse) {
    this.deleting.set(true);
    this.currencyService.delete(currency.id).subscribe({
      next: (resp) => {
        this.deleting.set(false);
        this.toastr.show(resp.message, 'SUCCESS');
      },
      error: (err) => {
        this.deleting.set(false);
        this.errorEvent.emit(err.message);
      },
    });
  }
}
