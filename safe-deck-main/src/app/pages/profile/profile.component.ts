import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { MainMenuComponent } from '../shared/main-menu/main-menu.component';
import { TuiAvatar } from '@taiga-ui/kit';
import { TuiButton, TuiIcon } from '@taiga-ui/core';
import { AuthorizationService } from '../../services/authorization/authorization.service';
import { Router } from '@angular/router';
import { ProfileService } from '../../services/profile-service/profile.service';
import { ColorService } from '../../services/color-service/color.service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { UniversalResponce } from '../../shared/model/universal-responce.model';
import { TuiAutoFocus } from '@taiga-ui/cdk';

@Component({
  selector: 'app-profile',
  imports: [MainMenuComponent, TuiAvatar, TuiButton, TuiIcon, TuiAutoFocus, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProfileComponent {
   private readonly authService = inject(AuthorizationService);
   private readonly profileService = inject(ProfileService);
   private readonly colorService = inject(ColorService);
   private readonly router = inject(Router);
   
   protected userName = this.profileService.publicName;
   protected userNameControl = new FormControl('');

   protected isNameEdited = signal<boolean>(false);

   public startEdit() {
      this.isNameEdited.set(true);
      this.userNameControl.setValue(this.userName());
   }

   protected cancelEdit() {
      this.isNameEdited.set(false);
   }

   protected editName() {
      const newPublicName: string = this.userNameControl.value || '';
      if (newPublicName.trim() != '') {
         this.profileService.changePublicName(newPublicName)
         .subscribe(
            (result: UniversalResponce) => {
               if (result.status == 'ok') {
                  this.profileService.updateProfile();
               }
            }
         )
      }
      this.isNameEdited.set(false);
   }

   protected selectInputText(event: FocusEvent) {
      const input = event.target as HTMLInputElement;
      input?.select();
   }

   protected getAvatarText(): string {
      return this.userName()[0].toUpperCase();
   }

   protected getAvatarColor(): string {
      return this.colorService.getAccentColor(this.userName().charCodeAt(0));
   }

   protected logout() {
      this.authService.logout();
      this.router.navigateByUrl('');
   }
}
