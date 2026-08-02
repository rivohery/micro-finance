import { Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-submit-button',
  imports: [MatIconModule],
  templateUrl: './submit-button.component.html',
  styleUrl: './submit-button.component.css',
})
export class SubmitButtonComponent {
  isLoading = input<boolean>(false);
  label = input<string>('Envoyer');
  loadingLabel = input<string>('Chargement...');
  icon = input<string>('');
}
