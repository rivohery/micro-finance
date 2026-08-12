import {
  Injectable,
  WritableSignal,
  computed,
  inject,
  signal,
} from '@angular/core';
import { HandleErrorService } from '../../../core/services/handle-error.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, catchError } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { State } from '../../../shared/models/state.model';
import {
  GlobalResponse,
  PageResponse,
} from '../../../shared/models/shared.model';
import {
  ChangeProfileRequest,
  ChangeUserStatusRequest,
  UserRequest,
  UserResponse,
} from '../model/user.model';
import { ChangePasswordRequest } from '../../auths/models/auths.model';

@Injectable({
  providedIn: 'root',
})
export class UsersService extends HandleErrorService {
  // userUrl: string = `${environment.API_URL}/users`;
  userUrl: string = `/api/v1/users`;

  http = inject(HttpClient);

  private getAllEmployesSignal: WritableSignal<
    State<PageResponse<UserResponse>, string>
  > = signal(
    State.builder<PageResponse<UserResponse>, string>().forInit().build()
  );
  getAllEmployesState$ = computed(() => this.getAllEmployesSignal());

  private createUserSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  createUsersState$ = computed(() => this.createUserSignal());

  private deleteUserSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  deleteUsersState$ = computed(() => this.deleteUserSignal());

  private changeUserStatusSignal: WritableSignal<
    State<GlobalResponse, string>
  > = signal(State.builder<GlobalResponse, string>().forInit().build());
  changeUserStatusState$ = computed(() => this.changeUserStatusSignal());

  private changePasswordSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  changePasswordState$ = computed(() => this.changePasswordSignal());

  private changeProfileSignal: WritableSignal<State<GlobalResponse, string>> =
    signal(State.builder<GlobalResponse, string>().forInit().build());
  readonly changeProfileState$ = this.changeProfileSignal.asReadonly();

  initChangeProfileState(): void {
    this.changeProfileSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  getUserAuthenticated(userId: string): Observable<UserResponse> {
    return this.http
      .get<UserResponse>(`${this.userUrl}/${userId}`)
      .pipe(catchError((err) => this.handleError(err)));
  }

  initUsersState(): void {
    this.initGetAllEmployesState();
    this.initDeleteUserState();
    this.initChangeUserStatusState();
  }

  public initDeleteUserState(): void {
    this.deleteUserSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  private initGetAllEmployesState(): void {
    this.getAllEmployesSignal.set(
      State.builder<PageResponse<UserResponse>, string>().forInit().build()
    );
  }

  initCreateUserState(): void {
    this.createUserSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  initChangeUserStatusState(): void {
    this.changeUserStatusSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  initChangePasswordState(): void {
    this.changePasswordSignal.set(
      State.builder<GlobalResponse, string>().forInit().build()
    );
  }

  getAllEmployes(
    search: string = '',
    page: number = 0,
    size: number = 6
  ): void {
    this.initGetAllEmployesState();
    this.http
      .get<PageResponse<UserResponse>>(`${this.userUrl}`, {
        params: new HttpParams()
          .append('search', search)
          .append('page', page)
          .append('size', size),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.getAllEmployesSignal.set(
            State.builder<PageResponse<UserResponse>, string>()
              .forSuccess(resp)
              .build()
          ),
        error: (err) =>
          this.getAllEmployesSignal.set(
            State.builder<PageResponse<UserResponse>, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  createUser(request: UserRequest): void {
    this.initCreateUserState();
    this.http
      .post<GlobalResponse>(`${this.userUrl}`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.createUserSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.createUserSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  deleteUser(userId: string): void {
    this.initDeleteUserState();
    this.http
      .delete<GlobalResponse>(`${this.userUrl}/${userId}`)
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.deleteUserSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.deleteUserSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  changeUserStatus(request: ChangeUserStatusRequest): void {
    this.initChangeUserStatusState();
    this.http
      .patch<GlobalResponse>(`${this.userUrl}/change-status`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.changeUserStatusSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.changeUserStatusSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  changePassword(request: ChangePasswordRequest): void {
    this.initChangePasswordState();
    this.http
      .post<GlobalResponse>(`${this.userUrl}/change-password`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.changePasswordSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.changePasswordSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }

  changeProfile(request: ChangeProfileRequest): void {
    this.initChangeProfileState();
    this.http
      .patch<GlobalResponse>(`${this.userUrl}/change-profile`, request, {
        headers: new HttpHeaders().set('Content-Type', 'application/json'),
      })
      .pipe(catchError((err) => this.handleError(err)))
      .subscribe({
        next: (resp) =>
          this.changeProfileSignal.set(
            State.builder<GlobalResponse, string>().forSuccess(resp).build()
          ),
        error: (err) =>
          this.changeProfileSignal.set(
            State.builder<GlobalResponse, string>()
              .forError(err.message)
              .build()
          ),
      });
  }
}
