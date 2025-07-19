import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { catchError, map, Observable, of } from "rxjs";
import { jwtDecode } from 'jwt-decode';
import { Role } from "../../shared/model/role.enum";
import { environment } from "../../../environments/environment";

@Injectable({ providedIn: 'root' })
export class AuthorizationService {
   private readonly http = inject(HttpClient);
   private readonly apiUrl = environment.apiUrl;
   
   isLoggedIn: boolean;
   loginName: string = '';
   role: Role = Role.GUEST;

   constructor() {
      var token = localStorage.getItem('jwt');
      this.isLoggedIn = (token != null)
      if (token != null) {
         let decoded = Object(jwtDecode(token));
         console.log(decoded);
         this.loginName = decoded.sub ? decoded.sub : '';
         this.role = decoded.role ? decoded.role : '';
      }
   }

   isAuthenticated(): boolean {
      return this.isLoggedIn;
   }

   generateRegisterCode(email: string): Observable<number> {
      return this.http.post<any>(`${this.apiUrl}/auth/generate-register-code`, {email})
         .pipe(
            map(() => {
               return 200;
            }),
            catchError(responce => {
               console.info(responce.error.error);
               return of(responce.status);
            })
         );
   }

   generate2FACode(email: string): Observable<boolean> {
      return this.http.post<any>(`${this.apiUrl}/auth/generate-2fa-code`, {email})
         .pipe(
            map(() => {
               return true;
            }),
            catchError(error => {
               console.info(error.error.error);
               return of(false);
            })
         );
   }

   login(login: string, password: string, generatedCode: string): Observable<boolean> {
      var body = { email: login, password, generatedCode };
      console.log("login start");
      return this.http.post<any>(`${this.apiUrl}/auth/login`, body)
         .pipe(
            map(response => {
               localStorage.setItem('jwt', response.token);
               let decoded = Object(jwtDecode(response.token));
               console.log(decoded);
               this.loginName = decoded.sub ? decoded.sub : '';
               this.role = decoded.role ? decoded.role : '';
               this.isLoggedIn = true;
               return true;
            }),
            catchError(error => {
               this.isLoggedIn = false;
               console.info(error.error.error);
               return of(false);
            })
         );
   }

   logout(): void {
      localStorage.removeItem('jwt');
      this.role = Role.GUEST;
      this.isLoggedIn = false;
   }

   register(login: string, password: string, publicName: string, generatedCode: string): Observable<number> {
      var body = { email: login, password, publicName, generatedCode };
      return this.http.post<any>(`${this.apiUrl}/auth/register`, body)
         .pipe(
            map(response => {
               localStorage.setItem('jwt', response.token);
               let decoded = Object(jwtDecode(response.token));
               console.log(decoded);
               this.loginName = decoded.sub ? decoded.sub : '';
               this.role = decoded.role ? decoded.role : '';
               this.isLoggedIn = true;
               return 200;
            }),
            catchError(response => {
               this.isLoggedIn = false;
               return of(response.status);              
            })
         );
   }

}