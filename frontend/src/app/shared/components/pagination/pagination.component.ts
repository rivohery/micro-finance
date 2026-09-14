import { NgClass } from '@angular/common';
import { Component, computed, input, output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-pagination',
  imports: [MatIconModule, NgClass],
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.css',
})
export class PaginationComponent {
  totalPages = input.required<number>();
  currentPage = input<number>(0);
  onPageChange = output<number>();

  pages = computed(() =>
    new Array(this.totalPages()).fill(0).map((_, index) => index)
  );

  goToPage(page: number) {
    if (page >= 0 && page <= this.totalPages() - 1) {
      this.onPageChange.emit(page);
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.onPageChange.emit(this.currentPage() + 1);
    }
  }

  previousPage(): void {
    if (this.currentPage() >= 1) {
      this.onPageChange.emit(this.currentPage() - 1);
    }
  }
}
