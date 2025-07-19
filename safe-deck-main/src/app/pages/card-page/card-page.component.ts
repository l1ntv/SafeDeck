import { ChangeDetectionStrategy, Component, inject, OnInit, signal, ViewChild } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule, FormsModule, FormArray } from '@angular/forms';
import { MainMenuComponent } from '../shared/main-menu/main-menu.component';
import { TuiButton, TuiIcon, TuiScrollbar, TuiTextfield } from '@taiga-ui/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CardService } from '../../services/card-service/card.service';
import { TuiTable } from '@taiga-ui/addon-table';
import { map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { CredentialPair } from '../../shared/model/secure/credential-pair.model';
import { CreatingCard } from '../../shared/model/cards/creating-card.model';
import { CardResponce } from '../../shared/model/cards/card-responce.model';
import { SelectRolesComponent } from '../shared/select-roles/select-roles.component';
import { AlertService } from '../../services/alert-service/alert.service';

@Component({
   selector: 'card-page',
   imports: [MainMenuComponent, TuiIcon, TuiButton, RouterLink, TuiScrollbar,
      FormsModule, ReactiveFormsModule, TuiTable, TuiTextfield, SelectRolesComponent],
   templateUrl: './card-page.component.html',
   styleUrl: './card-page.component.css',
   changeDetection: ChangeDetectionStrategy.OnPush
})
export class CardPageComponent implements OnInit {
   private readonly activateRoute = inject(ActivatedRoute);
   private readonly cardService = inject(CardService);
   private readonly router = inject(Router);
   private readonly alertService = inject(AlertService);

   private readonly defaultValues = [
      { name: 'URL', value: '' },
      { name: 'Логин', value: '' },
      { name: 'Пароль', value: '' },
   ] as const

   private readonly errors: Record<string, string> = {
      '404': 'Роль не найдена',
      '409': 'Карточка уже существует',
      '503': 'Проверьте интернет-соединение',
   }

   protected readonly array = new FormArray<FormGroup>([]);

   protected readonly items = toSignal(
      this.array.valueChanges.pipe(map(() => [...this.array.controls])),
      { initialValue: [] },
   );

   protected readonly columns = ['name', 'value', 'icon'];

   @ViewChild(SelectRolesComponent)
   protected selectRolesComponent!: SelectRolesComponent;

   public indexPasswordVisible = signal(-1);

   protected boardId: number = -1;
   protected cardId: number = -1;

   protected cardForm: FormGroup = new FormGroup({
      cardName: new FormControl('', Validators.required),
      cardDescription: new FormControl('')
   })

   ngOnInit() {
      this.activateRoute.params.subscribe(params => {
         this.boardId = params["board-id"];
         this.cardId = params["card-id"];
      });
      this.defaultValues.forEach(val => {
         this.array.push(
            new FormGroup({
               name: new FormControl(val.name, { updateOn: 'blur' }),
               value: new FormControl(val.value, { updateOn: 'blur' }),
            })
         );
      });
   }

   onSubmit() {
      let secureData: CredentialPair[] = [];
      this.items().forEach(
         (formGroup: FormGroup) => {
            secureData.push({
               field: formGroup.controls['name'].value,
               password: formGroup.controls['value'].value,
            })
         }
      )
      let creatingCard: CreatingCard = {
         cardName: this.cardForm.controls['cardName'].value,
         cardDescription: this.cardForm.controls['cardDescription'].value,
         roles: this.selectRolesComponent.getSelectedRoles(),
         secureData
      }
      this.cardService.createCard(this.boardId, creatingCard)
         .subscribe(
            (result: CardResponce) => {
               if (result.status == 'ok') {
                  this.router.navigate(['/cards', this.boardId]);
               } else {
                  var errorText: string | undefined;
                  if (result.error) {
                     errorText = this.errors[result.error];
                  }
                  if (!errorText) {
                     errorText = "Проверьте введённые данные или попробуйте позже";
                  }
                  this.alertService.showError(errorText)
               }
            }
         )
      console.log(creatingCard);
   }

   public addRow(): void {
      this.array.push(
         new FormGroup({
            name: new FormControl('', { updateOn: 'blur' }),
            value: new FormControl('', { updateOn: 'blur' }),
         }),
      );
   }

   public deleteRow(id: number): void {
      this.array.removeAt(id);
   }

   public isPasswordVisible(id: number): boolean {
      return this.indexPasswordVisible() == id;
   }

   public togglePasswordVisibility(id: number): void {
      if (this.indexPasswordVisible() == id) {
         this.indexPasswordVisible.set(-1);
      } else {
         this.indexPasswordVisible.set(id);
      }
   }

}
