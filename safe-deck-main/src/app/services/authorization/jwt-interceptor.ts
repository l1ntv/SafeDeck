import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { jwtDecode } from 'jwt-decode';
import { Router } from "@angular/router";
import { AuthorizationService } from "./authorization.service";
import { Role } from "../../shared/model/role.enum";

// Перехватывает http-запросы и добавляет к ним токен авторизации
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
   private readonly authService: AuthorizationService = inject(AuthorizationService);
   private readonly router: Router = inject(Router);

   intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
      const token = localStorage.getItem('jwt');
      if (token) {
         let decoded = Object(jwtDecode(token));
         let nowSeconds = new Date().getTime() / 1000;
         //console.log(`секунд до сброса jwt: ${decoded.exp - nowSeconds}`);
         if (nowSeconds >= decoded.exp) { // сбрасываем невалидный jwt
            localStorage.removeItem('jwt');
            this.authService.isLoggedIn = false;
            this.authService.role = Role.GUEST;
            this.router.navigateByUrl('/');
            return next.handle(req);
         }
         const authReq = req.clone({
            setHeaders: {
               Authorization: `Bearer ${token}`
            }
         });
         return next.handle(authReq);
      } else {
         return next.handle(req);
      }
   }
}