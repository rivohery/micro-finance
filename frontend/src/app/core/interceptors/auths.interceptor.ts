import { HttpInterceptorFn } from '@angular/common/http';

export const authsInterceptor: HttpInterceptorFn = (req, next) => {
  // l'URL de l' API
  const apiUrl = 'http://localhost:8088';

  if (req.url.startsWith(apiUrl)) {
    const authReq = req.clone({
      withCredentials: true,
    });
    return next(authReq);
  }
  // Pour les autres domaines, on laisse passer tel quel
  return next(req);
};
