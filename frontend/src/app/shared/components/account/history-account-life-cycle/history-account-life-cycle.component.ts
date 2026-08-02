import { Component, effect, input, output, signal } from '@angular/core';
import { PageResponse } from '../../../models/shared.model';
import {
  AccountStatus,
  AccountStatusHistoryResponse,
} from '../../../../core/models/account.model';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { DatePipe, NgClass } from '@angular/common';
import { PaginationComponent } from '../../pagination/pagination.component';

@Component({
  selector: 'app-history-account-life-cycle',
  imports: [
    MatTooltipModule,
    MatIconModule,
    NgClass,
    DatePipe,
    PaginationComponent,
  ],
  templateUrl: './history-account-life-cycle.component.html',
  styleUrl: './history-account-life-cycle.component.css',
})
export class HistoryAccountLifeCycleComponent {
  accountLyfeCyclePages =
    input.required<PageResponse<AccountStatusHistoryResponse>>();
  onPageChange = output<number>();

  totalPages = signal<number>(0);
  currentPage = signal<number>(0);
  lifecycleLogs = signal<AccountStatusHistoryResponse[]>([]);

  constructor() {
    effect(() => {
      const accountLyfeCyclePages = this.accountLyfeCyclePages();
      if (accountLyfeCyclePages) {
        this.totalPages.set(accountLyfeCyclePages.totalPages || 0);
        this.currentPage.set(accountLyfeCyclePages.number || 0);
        this.lifecycleLogs.set(accountLyfeCyclePages.content || []);
      }
    });
  }

  getStatusClass(status: AccountStatus): string {
    switch (status) {
      case 'ACTIVE':
        return 'bg-emerald-100 text-emerald-700 border-emerald-200';
      case 'PENDING':
        return 'bg-amber-100 text-amber-700 border-amber-200';
      case 'SUSPENDED':
        return 'bg-rose-100 text-rose-700 border-rose-200';
      case 'CLOSED':
        return 'bg-slate-200 text-slate-700 border-slate-300';
    }
  }

  goToPage(page: number) {
    this.onPageChange.emit(page);
  }
}
