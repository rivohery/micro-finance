import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { UsersService } from '../data-access/users-service';
import { MessageBoxComponent } from '../../../shared/components/message-box/message-box.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { SearchBarComponent } from '../../../shared/components/search-bar/search-bar.component';
import { NgClass } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { UserResponse } from '../model/user.model';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogModule,
} from '@angular/material/dialog';
import { DeleteConfirmModalComponent } from '../../../shared/components/delete-confirm-modal/delete-confirm-modal.component';
import { CreateEmployeComponent } from '../ui/create-employe/create-employe.component';
import { ToastrService } from '../../../core/services/toastr/toastr.service';
import { ChangeStatusComponent } from '../ui/change-status/change-status.component';

@Component({
  selector: 'app-user-mangement',
  imports: [
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatMenuModule,
    MatDialogModule,
    MessageBoxComponent,
    PaginationComponent,
    SearchBarComponent,
    LoaderComponent,
    NgClass,
  ],
  templateUrl: './user-mangement.component.html',
  styleUrl: './user-mangement.component.css',
})
export class UserMangementComponent implements OnInit {
  usersService = inject(UsersService);
  dialog = inject(MatDialog);
  loading = signal<boolean>(false);
  errorMsg = signal<string>('');
  toastrService = inject(ToastrService);

  currentPage = signal<number>(0);
  currentsearch = signal<string>('');
  users = signal<UserResponse[]>([]);
  totalElements = signal<number>(0);
  totalPages = signal<number>(0);

  constructor() {
    effect(() => this.handleGetAllEmployesState());
    effect(() => this.handleDeleteUsersState());
  }

  ngOnInit(): void {
    this.usersService.initUsersState();
    this.loading.set(true);
    this.usersService.getAllEmployes(this.currentsearch(), this.currentPage());
  }

  private handleGetAllEmployesState(): void {
    const getAllEmployesState = this.usersService.getAllEmployesState$();
    if (getAllEmployesState.status === 'OK') {
      this.loading.set(false);
      this.currentPage.set(getAllEmployesState.value?.number || 0);
      this.users.set(getAllEmployesState.value?.content || []);
      this.totalElements.set(getAllEmployesState.value?.totalElements || 0);
      this.totalPages.set(getAllEmployesState.value?.totalPages || 0);
    }
    if (getAllEmployesState.status === 'ERROR') {
      this.loading.set(false);
      this.errorMsg.set(getAllEmployesState?.error || '');
    }
  }

  private handleDeleteUsersState(): void {
    const deleteUsersState = this.usersService.deleteUsersState$();
    if (deleteUsersState.status === 'OK') {
      this.currentPage.set(0);
      this.usersService.getAllEmployes(
        this.currentsearch(),
        this.currentPage()
      );
      this.toastrService.show(deleteUsersState.value?.message || '', 'SUCCESS');
    }
    if (deleteUsersState.status === 'ERROR') {
      this.errorMsg.set(deleteUsersState.error || '');
    }
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.usersService.getAllEmployes(this.currentsearch(), this.currentPage());
  }

  doSearch(value: string): void {
    this.currentsearch.set(value);
    this.currentPage.set(0);
    this.usersService.initDeleteUserState();
    this.usersService.getAllEmployes(this.currentsearch(), this.currentPage());
  }

  closeErrorMsg(value: boolean): void {
    this.errorMsg.set('');
  }

  editStatus(user: UserResponse) {
    this.usersService.initChangeUserStatusState();
    const dialogRef = this.dialog.open(ChangeStatusComponent, {
      width: '450px',
      maxWidth: '95vw',
      data: {
        id: user.id,
        username: user.username,
        email: user.email,
        enable: user.enable,
        role: user.role,
      } as UserResponse,
    });

    dialogRef.afterClosed().subscribe((resp) => {
      if (resp === 'modifié') {
        this.usersService.getAllEmployes(
          this.currentsearch(),
          this.currentPage()
        );
        this.usersService.initChangeUserStatusState();
      }
    });
  }

  addUser(): void {
    this.usersService.initCreateUserState();
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true; // Empêche la fermeture en cliquant à l'extérieur
    dialogConfig.width = '500px';
    dialogConfig.maxWidth = '95vw'; // Garantit que c'est responsive sur petit écran

    const dialogRef = this.dialog.open(CreateEmployeComponent, dialogConfig);

    dialogRef.afterClosed().subscribe((resp) => {
      if (resp === 'ajouté') {
        const createUserState = this.usersService.createUsersState$();
        this.toastrService.show(
          createUserState.value?.message || '',
          'SUCCESS'
        );
        this.usersService.getAllEmployes();
      }
    });
  }

  deleteUser(user: UserResponse) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true; // Empêche la fermeture en cliquant à l'extérieur
    dialogConfig.width = '100%';
    dialogConfig.maxWidth = '400px';

    const dialogRef = this.dialog.open(
      DeleteConfirmModalComponent,
      dialogConfig
    );

    dialogRef.afterClosed().subscribe((confirm) => {
      if (confirm) {
        this.usersService.deleteUser(user?.id);
      }
    });
  }
}
