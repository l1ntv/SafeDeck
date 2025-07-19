import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TuiButton, TuiDialogContext, TuiDialogService } from '@taiga-ui/core';
import { injectContext } from '@taiga-ui/polymorpheus';
import { AuthorizationService } from '../../../services/authorization/authorization.service';
import { Router } from '@angular/router';
import { DialogAuthorizationService } from '../dialog-authorization/dialog-authorization.service';

@Component({
  selector: 'app-authorization',
  imports: [TuiButton, ReactiveFormsModule],
  templateUrl: './authorization.component.html',
  styleUrl: '../../../shared/styles/auth.styles.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AuthorizationComponent {
   private readonly authService = inject(AuthorizationService);
   private readonly dialogAuthService = inject(DialogAuthorizationService);
   private readonly router = inject(Router);
   readonly context = injectContext<TuiDialogContext<void,void>>();

   protected authForm: FormGroup = new FormGroup({
      login: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required)
   })

   protected error = signal<string>('');

   protected showRegistrationDialog():void {
      this.context.completeWith();
      this.dialogAuthService.showRegisterDialog();
   }

   protected onSubmit():void {
      let email = this.authForm.controls['login'].value;
      let password = this.authForm.controls['password'].value;

      this.authService.generate2FACode(email)
      .subscribe(
         (ok) => {
            if (ok) {
               this.context.completeWith();
               this.requestConfirmationCode(email, password);
            } else {
               this.error.set('Ошибка: аккаунт с таким логином и паролем не найден');
            }
         }
      )
   }


   private requestConfirmationCode(email: string, password: string) {
      this.dialogAuthService.showConfirmationCodeDialog()
         .subscribe(
            (code: string) => {
               if (this.codeIsValid(code)) {
                  this.login(email, password, code);
               }     
            }
         )
   }
   
   private codeIsValid(code: string): boolean {
      return code.length == 5;
   }

   private login(email: string, password: string, code: string) {
      this.authService.login(email, password, code)
      .subscribe(
         (result: boolean) => {
            if (result) {
               this.router.navigate(['/boards']);
            } else {
               this.error.set('Ошибка: аккаунт с таким логином и паролем не найден');
            }
         }
      );
   }
}
