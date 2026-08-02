import { Component, OnDestroy, input, output } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Subscription, debounceTime } from 'rxjs';

@Component({
  selector: 'app-search-bar',
  imports: [MatIconModule, MatButtonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './search-bar.component.html',
  styleUrl: './search-bar.component.css',
})
export class SearchBarComponent implements OnDestroy {
  placeholder = input<string>('Rechercher...');
  searchEvent = output<string>();
  modeSearch = input<string>('keyup');

  searchControl: FormControl<string> = new FormControl('', {
    nonNullable: true,
  });
  searchSubscription!: Subscription;

  onSearch() {
    if (this.modeSearch() === 'keyup') {
      this.searchSubscription = this.searchControl.valueChanges
        .pipe(debounceTime(3000))
        .subscribe({
          next: (value) => this.searchEvent.emit(value),
        });
    } else {
      this.searchEvent.emit(this.searchControl.value);
    }
  }

  clearSearch() {
    this.searchControl.setValue('');
    this.searchEvent.emit('');
  }

  ngOnDestroy(): void {
    this.searchSubscription.unsubscribe();
  }
}
