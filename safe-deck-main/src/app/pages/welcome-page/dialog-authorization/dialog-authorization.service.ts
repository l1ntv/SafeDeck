import { Injectable } from "@angular/core";
import { tuiDialog } from "@taiga-ui/core";
import { AuthorizationComponent } from "../authorization/authorization.component";
import { RegistrationComponent } from "../registration/registration.component";
import { ConfirmationCodeComponent } from "../confirmation-code/confirmation-code.component";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class DialogAuthorizationService {
   private readonly authDialog = tuiDialog(AuthorizationComponent, {
      dismissible: true,
      size: 's',
   });

   private readonly registerDialog = tuiDialog(RegistrationComponent, {
      dismissible: true,
      size: 's',
   });

   private readonly confirmationCodeDialog = tuiDialog(ConfirmationCodeComponent, {
      dismissible: true,
      size: 's',
   });

   public showAuthDialog(): void {
      this.authDialog().subscribe();
   }

   public showRegisterDialog(): void {
      this.registerDialog().subscribe();
   }

   public showConfirmationCodeDialog(): Observable<string> {
      return this.confirmationCodeDialog();
   }

}