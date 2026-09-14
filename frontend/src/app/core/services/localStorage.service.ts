import { isPlatformBrowser } from '@angular/common';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { UserResponse } from '../../features/users/model/user.model';

@Injectable({
  providedIn: 'root',
})
export class LocalStorageService {
  private isBrowser: boolean;

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    this.isBrowser = isPlatformBrowser(this.platformId);
  }

  saveUserInfosInLS(userInfo: UserResponse): void {
    if (this.isBrowser) {
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
    } else {
      console.warn('localStorage undefined in Server environment.');
    }
  }

  getUserInfosInLs(): UserResponse | null {
    if (this.isBrowser) {
      return (
        (JSON.parse(
          localStorage.getItem('userInfo') as string
        ) as UserResponse) || null
      );
    } else {
      console.warn('localStorage undefined in Server environment.');
      return null;
    }
  }

  clearLocalStorage(): void {
    if (this.isBrowser) {
      localStorage.clear();
    } else {
      console.warn('localStorage undefined in Server environment.');
    }
  }
}
