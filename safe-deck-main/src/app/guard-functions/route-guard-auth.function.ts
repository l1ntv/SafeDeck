import { CanActivateFn, Router } from "@angular/router";
import { AuthorizationService } from "../services/authorization/authorization.service";
import { inject } from "@angular/core";

export const RouteGuardAuth: CanActivateFn = function() {
   const isAuthenticated: boolean = inject(AuthorizationService).isAuthenticated();
   if (!isAuthenticated) {
      const router = inject(Router);
      router.navigate(['']);
   }
   
   return isAuthenticated;
}