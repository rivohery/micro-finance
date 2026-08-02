import { NgClass } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-message-box',
  imports: [NgClass, MatIconModule],
  templateUrl: './message-box.component.html',
  styleUrl: './message-box.component.css',
})
export class MessageBoxComponent {
  type = input<'success' | 'error'>('success');
  message = input.required<string>();
  title = input<string>('');
  onClose = output<boolean>();

  get icon(): string {
    return this.type() === 'success' ? 'check_circle' : 'error';
  }

  closeMsgBox(): void {
    this.onClose.emit(true);
  }
}
