import { ChangeDetectionStrategy, Component, inject, input, OnInit, signal } from '@angular/core';
import { RoleCard } from '../../../shared/model/roles/role.model';
import { RoleService } from '../../../services/role-service/role.service';
import { TuiButton, TuiDropdown, TuiIcon } from '@taiga-ui/core';
import { TuiActiveZone, TuiObscured } from '@taiga-ui/cdk';

@Component({
   selector: 'select-roles',
   imports: [TuiIcon, TuiButton, TuiDropdown, TuiActiveZone, TuiObscured],
   templateUrl: './select-roles.component.html',
   styleUrl: './select-roles.component.css',
   changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectRolesComponent implements OnInit {
   private readonly roleService = inject(RoleService);

   public boardId = input.required<number>();

   protected selectedRoles = signal<RoleCard[]>([]);
   protected roles = this.roleService.boardRoles; //signal<RoleCard[]>([]);
   protected openDropDown = false;

   ngOnInit() {
      //this.roleService.updateBoardRoles(this.boardId());
      this.roleService.getBoardRoles(this.boardId())
      .subscribe(
         (roles: RoleCard[]) => {
            this.roles.set(
               roles.filter(role => role.cards && role.cards.length > 0)
            )
         }
      )
   }

   public getSelectedRoles(): RoleCard[] {
      return this.selectedRoles();
   }

   public setSelectedRoles(roles: RoleCard[]) {
      this.selectedRoles.set(roles);
   }

   protected addRole(role: RoleCard): void {
      this.selectedRoles.update(roles => [...roles, role]);
   }

   protected removeRole(role: RoleCard): void {
      this.selectedRoles.update(roles => roles.filter((role2) => role.roleId !== role2.roleId));
   }

   protected selectRoleToAdd(role: RoleCard) {
      this.openDropDown = false;
      if (!this.selectedRoles().find(role2 => role.roleId == role2.roleId)) {
         this.addRole(role);
      } 
   }

   protected showMenu(): void {
      this.openDropDown = !this.openDropDown;
   }

   protected onObscured(obscured: boolean): void {
      if (obscured) {
         this.openDropDown = false;
      }
   }

   protected onActiveZone(active: boolean): void {
      this.openDropDown = active && this.openDropDown;
   }
}
