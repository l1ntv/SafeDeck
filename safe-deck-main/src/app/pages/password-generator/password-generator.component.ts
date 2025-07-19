import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FormGroup, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { TuiIcon, TuiButton } from '@taiga-ui/core';
import { TuiCheckbox } from '@taiga-ui/kit';
import { MainMenuComponent } from '../shared/main-menu/main-menu.component';
import { AlertService } from '../../services/alert-service/alert.service';

@Component({
   selector: 'password-generator',
   imports: [MainMenuComponent, TuiIcon, TuiButton, TuiCheckbox, ReactiveFormsModule],
   templateUrl: './password-generator.component.html',
   styleUrl: './password-generator.component.css',
   changeDetection: ChangeDetectionStrategy.OnPush
})
export class PasswordGeneratorComponent implements OnInit {
   private readonly alertService = inject(AlertService);

   private readonly lowercaseChars = 'abcdefghijklmnopqrstuvwxyz';
   private readonly uppercaseChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
   private readonly numberChars = '0123456789';
   private readonly specialChars = '!@#$%^&*()_+-=[]{}|;:,.<>?';

   protected form = new FormGroup({
      lowercase: new FormControl(true),
      uppercase: new FormControl(true),
      numbers: new FormControl(true),
      special: new FormControl(true),
      length: new FormControl(12, [Validators.min(1)])
   });

   protected generatedPassword = '';
   protected passwordStrength: 'weak' | 'medium' | 'strong' = 'weak';


   get length() {
      return this.form.get('length')?.value || 12;
   }

   get selectedGroups() {
      const groups = [];

      if (this.form.get('lowercase')?.value) groups.push(this.lowercaseChars);
      if (this.form.get('uppercase')?.value) groups.push(this.uppercaseChars);
      if (this.form.get('numbers')?.value) groups.push(this.numberChars);
      if (this.form.get('special')?.value) groups.push(this.specialChars);

      return groups;
   }

   ngOnInit(): void {
      this.generatePassword();
   }

   generatePassword(): void {
      const groups = this.selectedGroups;

      if (groups.length === 0) {
         this.alertService.showError("Выберите хотя бы один тип символов");
         return;
      }

      const minCharsPerGroup = 1;
      const totalMinChars = groups.length * minCharsPerGroup;

      if (this.length < totalMinChars) {
         this.alertService.showError(`Длина пароля должна быть не меньше ${totalMinChars}`)
         return;
      }

      let password = '';

      // гарантируем наличие хотя бы одного символа из каждой группы
      for (const group of groups) {
         password += this.getRandomCharFromGroup(group);
      }

      // заполняем оставшееся пространство случайными символами
      const remainingLength = this.length - totalMinChars;
      for (let i = 0; i < remainingLength; i++) {
         const randomGroup = groups[Math.floor(Math.random() * groups.length)];
         password += this.getRandomCharFromGroup(randomGroup);
      }

      // перемешиваем
      password = password
         .split('')
         .sort(() => 0.5 - Math.random())
         .join('');

      this.generatedPassword = password;
      this.evaluatePasswordStrength(password);
   }

   copyToClipboard(): void {
      navigator.clipboard.writeText(this.generatedPassword)
      .then(
         () => this.alertService.showMessage("Пароль скопирован")
      )
      .catch(err => {
         this.alertService.showError("Не удалось скопировать в буфер обмена")
         console.error(err);
      });
   }

   private getRandomCharFromGroup(group: string): string {
      const index = Math.floor(Math.random() * group.length);
      return group[index];
   }

   private evaluatePasswordStrength(password: string): void {
      let score = 0;

      if (password.length >= 12) score += 2;
      else if (password.length >= 8) score += 1;

      if (/[a-z]/.test(password)) score += 1;
      if (/[A-Z]/.test(password)) score += 1;
      if (/[0-9]/.test(password)) score += 1;
      if (/[!@#$%^&*()_+=[\]{}|;:,.<>?]/.test(password)) score += 1;

      if (score >= 6) this.passwordStrength = 'strong';
      else if (score >= 4) this.passwordStrength = 'medium';
      else this.passwordStrength = 'weak';
   }
}
