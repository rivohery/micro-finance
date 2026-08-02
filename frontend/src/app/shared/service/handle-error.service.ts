import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class HandleErrorService {
  protected handleError(err: HttpErrorResponse): Observable<never> {
    console.error(err);
    const errorMsg = err.error.message
      ? err.error.message
      : err.message
      ? err.message
      : 'Server work wrong,Please try later';
    return throwError(
      () => new Error(`Error ${err.status ? err.status : 500}: ${errorMsg}`)
    );
  }
}
