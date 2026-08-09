import { Injectable, signal } from '@angular/core';
import { AlertType, ToastrInfo } from './toastr.model';

@Injectable({
  providedIn: 'root',
})
export class ToastrService {
  private toastrsSignal = signal<ToastrInfo[]>([]);

  readonly toastrs = this.toastrsSignal.asReadonly();

  show(body: string, type: AlertType, timeout = 5000) {
    const toastr: ToastrInfo = { body, type };
    this.toastrsSignal.update((current) => [...current, toastr]);

    setTimeout(() => {
      this.remove(toastr);
    }, timeout);
  }

  remove(toastr: ToastrInfo) {
    this.toastrsSignal.update((current) => current.filter((t) => t !== toastr));
  }
}
