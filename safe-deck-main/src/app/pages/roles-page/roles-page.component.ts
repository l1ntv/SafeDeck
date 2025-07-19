import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { MainMenuComponent } from '../shared/main-menu/main-menu.component';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TuiButton, TuiDialogService, TuiIcon, TuiScrollbar } from '@taiga-ui/core';
import { RoleService } from '../../services/role-service/role.service';
import { CardService } from '../../services/card-service/card.service';
import { AccessLevel } from '../../shared/model/cards/access-level.enum';
import { Card } from '../../shared/model/cards/card.model';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TuiAutoFocus } from '@taiga-ui/cdk';
import { TUI_CONFIRM } from '@taiga-ui/kit';
import { RoleResponce } from '../../shared/model/roles/role-responce.model';
import { RoleCard } from '../../shared/model/roles/role.model';
import { TuiCheckbox } from '@taiga-ui/kit';
import { AlertService } from '../../services/alert-service/alert.service';

@Component({
  selector: 'app-roles-page',
  imports: [MainMenuComponent, RouterLink, TuiIcon, TuiButton, TuiAutoFocus, TuiCheckbox, ReactiveFormsModule, TuiScrollbar],
  templateUrl: './roles-page.component.html',
  styleUrl: './roles-page.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RolesPageComponent implements OnInit {
   private readonly activateRoute = inject(ActivatedRoute);
   private readonly roleService = inject(RoleService);
   private readonly cardService = inject(CardService);
   private readonly dialogs = inject(TuiDialogService);
   private readonly formBuilder = inject(FormBuilder);
   private readonly alertService = inject(AlertService);

   protected roles = this.roleService.boardRoles;
   protected boardId: number = -1;

   protected selectRoleIndex = signal<number>(-1);
   protected editRoleIndex = signal<number>(-1);
   protected isCreateRole = signal<boolean>(false);

   protected createdRoleName = new FormControl('');
   protected editedRoleName = new FormControl('');

   protected get cards(): Card[] {
      return this.AccessibleCards().accessibleCards;
   }

   protected get accessLevel(): AccessLevel {
      return this.AccessibleCards().accessLevel;
   }

   protected roleForm!: FormGroup;

   private AccessibleCards = this.cardService.getAccessibleCardsSignal();

   ngOnInit() {
      this.activateRoute.params.subscribe(params => {
         this.boardId = params["board-id"];
         this.roleService.updateBoardRoles(this.boardId);
         this.cardService.updateAccesibleCards(this.boardId);
      });
   }

   protected startCreatingRole() {
      this.isCreateRole.set(true);
      this.selectRoleIndex.set(-1);
   }

   protected cancelCreatingRole() {
      this.isCreateRole.set(false);
   }

   protected createRole() {
      let nameRole = this.createdRoleName.value;
      if (nameRole && nameRole.trim() != '') {
         this.roleService.createRole(this.boardId, nameRole).subscribe(
            (roleResponce: RoleResponce) => {
               this.roleService.updateBoardRoles(this.boardId);
               // выделяем созданную роль
               setTimeout(() => {
                  let index = this.roles().findIndex(role => role.roleId == roleResponce.role?.roleId);
                  this.selectRow(index)
               }, 100)
            }
         );
      }
      this.isCreateRole.set(false);
      this.createdRoleName.setValue('');
   }

   protected selectRow(index: number) {
      this.selectRoleIndex.set(index);
      let roleId = this.roles()[index].roleId;

      // создаём массив controls выбранных галочек в зависимости от доступных для роли карточки
      let checkboxControls = this.cards.map(
         (card: Card) => {
            let role = card.roles.find((role:RoleCard) => role.roleId == roleId);
            if (role) {
               return new FormControl(true);
            } else {
               return new FormControl(false);
            }
         }
      )
      this.roleForm = this.formBuilder.group({
         checkboxes: new FormArray(checkboxControls)
      });
   }

   protected confirmDeleteRole() {
      this.alertService.confirmOperation(
         "Вы уверены, что хотите удалить роль? Это действие необратимо",
         this.deleteRole.bind(this)
      )
   }

   protected deleteRole() {
      let index: number = this.selectRoleIndex();
      if (index >= 0) {
         let roleId = this.roles()[index].roleId;
         this.roleService.deleteRole(this.boardId, roleId)
         .subscribe(
            (resultCode: number) => {
               if (resultCode == 200) {
                  this.roleService.updateBoardRoles(this.boardId);
                  this.selectRoleIndex.set(-1);
               }
            }
         )
      }
   }

   protected updateCardsRole() {
      const index: number = this.selectRoleIndex();
      if (index >= 0) {
         const selectedValues: boolean[] = this.roleForm.value.checkboxes;
         const cards = this.cards.filter(
            (card: Card, index: number) => selectedValues[index]
         )
         console.log(cards);
         const roleId = this.roles()[index].roleId;
         console.log(`roleId = ${roleId}`);
         this.roleService.updateCardsRole(this.boardId, roleId, cards)
         .subscribe(
            (resultCode: number) => {
               if (resultCode == 200) {
                  this.cardService.updateAccesibleCards(this.boardId);
                  this.alertService.showMessage("Данные обновлены");
               } else {
                  this.alertService.showError("Ошибка");
               }
            }
         )
      } 
   }

   protected startEditingRole() {
      this.editRoleIndex.set(this.selectRoleIndex());
      this.editedRoleName.setValue(this.roles()[this.selectRoleIndex()].roleName);
      this.selectRoleIndex.set(-1);
   }

   protected cancelEditingRole() {
      this.selectRoleIndex.set(this.editRoleIndex());
      this.editRoleIndex.set(-1);
   }

   protected editRole(roleId: number) {
      let nameRole = this.editedRoleName.value;
      if (nameRole && nameRole.trim() != '') {
         this.roleService.renameRole(this.boardId, roleId, nameRole).subscribe(
            () => {
               this.roleService.updateBoardRoles(this.boardId);
               this.selectRoleIndex.set(this.editRoleIndex());
               this.editRoleIndex.set(-1);
            }
         )
      }
   }
}
