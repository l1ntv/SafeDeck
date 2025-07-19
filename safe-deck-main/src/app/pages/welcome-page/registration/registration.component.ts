import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { AuthorizationService } from '../../../services/authorization/authorization.service';
import { Router } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TuiButton, TuiDialogContext } from '@taiga-ui/core';
import { injectContext } from '@taiga-ui/polymorpheus';
import { DialogAuthorizationService } from '../dialog-authorization/dialog-authorization.service';
import { RegisterData } from '../../../shared/model/auth/register.model';
import { ProfileService } from '../../../services/profile-service/profile.service';

@Component({
  selector: 'app-registration',
  imports: [TuiButton, ReactiveFormsModule],
  templateUrl: './registration.component.html',
  styleUrl: '../../../shared/styles/auth.styles.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RegistrationComponent {
   private readonly authService = inject(AuthorizationService);
   private readonly profileService = inject(ProfileService);
   private readonly dialogAuthService = inject(DialogAuthorizationService);
   private readonly router = inject(Router);
   readonly context = injectContext<TuiDialogContext<void,void>>();

   private readonly errors: Record<string, string> = {
      '400': 'Ошибка: некорректные данные',
      '409': 'Ошибка: такой email уже зарегистрирован',
      '500': 'Ошибка сервера. Проверьте интернет-соединение',
      '503': 'Ошибка сервера. Проверьте интернет-соединение',
   }

   protected authForm: FormGroup = new FormGroup({
      login: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', Validators.required),
      publicName: new FormControl('', Validators.required)
   })
   protected error = signal<string>('');

   protected showAuthDialog():void {
      this.context.completeWith();
      this.dialogAuthService.showAuthDialog();
   }

   protected onSubmit():void {
      let email = this.authForm.controls['login'].value;
      let password = this.authForm.controls['password'].value;
      let publicName = this.authForm.controls['publicName'].value;
      let data = {email, password, publicName};

      this.authService.generateRegisterCode(email)
      .subscribe(
         (resultCode: number) => {
            if (resultCode == 200) {
               this.context.completeWith();
               this.requestConfirmationCode(data);
            } else {
               var errorText: string | undefined = this.errors[resultCode];
               if (!errorText) {
                  errorText = 'Неизвестная ошибка';
               }
               this.error.set(errorText);
            }
         }
      )
   }

   private requestConfirmationCode(data: RegisterData) {
      this.dialogAuthService.showConfirmationCodeDialog()
         .subscribe(
            (code: string) => {
               if (this.codeIsValid(code)) {
                  data.code = code;
                  this.registerAccount(data);
               }     
            }
         )
   }

   private codeIsValid(code: string): boolean {
      return code.length == 5;
   }

   private registerAccount(data: RegisterData) {
      this.authService.register(data.email, data.password, data.publicName, data?.code || '')
      .subscribe(
         (resultCode: number) => {
            if (resultCode == 200) {
               this.error.set('');
               this.router.navigate(['/boards']);
               this.context.completeWith();
            } else {
               var errorText: string | undefined = this.errors[resultCode];
               if (!errorText) {
                  errorText = 'Неизвестная ошибка';
               }
               this.error.set(errorText);
            }
         }
      );
   }

   protected isAuthenticated(): boolean {
      return this.authService.isAuthenticated();
   }
}

