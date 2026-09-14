import { NgClass } from '@angular/common';
import { Component, OnInit, inject, input, output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import {
  CurrencyResponse,
  UpdateCurrencyRequest,
} from '../../model/currency.model';
import { MatButtonModule } from '@angular/material/button';
import { ToastrService } from '../../../../core/services/toastr/toastr.service';

@Component({
  selector: 'app-currency-list',
  imports: [MatIconModule, MatTooltipModule, MatButtonModule, NgClass],
  templateUrl: './currency-list.component.html',
  styleUrl: './currency-list.component.css',
})
export class CurrencyListComponent {
  currencies = input<CurrencyResponse[]>([]);
  updating = input<boolean>(false);
  deleting = input<boolean>(false);
  onToggleStatusEvent = output<UpdateCurrencyRequest>();
  onDeleteEvent = output<string>();

  toastr = inject(ToastrService);

  toggleStatus(currency: CurrencyResponse) {
    const request = {
      id: currency.id,
      code: currency.code,
      name: currency.name,
      enable: !currency.enable,
    } as UpdateCurrencyRequest;
    this.onToggleStatusEvent.emit(request);
  }

  deleteCurrency(currency: CurrencyResponse) {
    this.onDeleteEvent.emit(currency.id);
  }
}
