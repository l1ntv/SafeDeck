import { ChangeDetectionStrategy, Component, inject, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TuiButton, TuiDialogService, TuiIcon, TuiScrollbar } from '@taiga-ui/core';
import { MainMenuComponent } from '../shared/main-menu/main-menu.component';
import { SelectRolesComponent } from '../shared/select-roles/select-roles.component';
import { BoardMemberService } from '../../services/board-member-service/board-member.service';
import { RoleCard } from '../../shared/model/roles/role.model';
import { BoardMemberResponce } from '../../shared/model/board-members/member-responce.model';
import { AlertService } from '../../services/alert-service/alert.service';
import { TUI_CONFIRM } from '@taiga-ui/kit';
import { BoardMember } from '../../shared/model/board-members/board-member.model';

@Component({
  selector: 'app-member-page',
  imports: [MainMenuComponent, SelectRolesComponent,
   TuiButton, TuiIcon, TuiScrollbar,
   FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './member-page.component.html',
  styleUrl: './member-page.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MemberPageComponent implements OnInit {
   private readonly activateRoute = inject(ActivatedRoute);
   private readonly boardMemberService = inject(BoardMemberService);
   private readonly router = inject(Router);
   private readonly alertService = inject(AlertService);

   protected boardId: number = -1;
   protected memberId: number = -1;

   protected memberForm: FormGroup = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email])
   })

   @ViewChild(SelectRolesComponent)
   protected selectRolesComponent!: SelectRolesComponent;

   private readonly errors: Record<string, string> = {
      '404': 'Пользователь не найден',
      '409': 'Пользователь уже добавлен',
      '500': 'Проверьте интернет-соединение',
   }

   ngOnInit() {
      this.activateRoute.params.subscribe(params => {
         this.boardId = params["board-id"];
         this.memberId = params["member-id"];
         if (this.memberId >= 0) {
            this.boardMemberService.getBoardMemberById(this.boardId, this.memberId)
            .subscribe(
               (boardMember: BoardMember) => {
                  this.memberForm.controls['email'].setValue(boardMember.email);
                  this.selectRolesComponent.setSelectedRoles(boardMember.roles);
               }
            )
         }
         
      });
   }

   protected addBoardMember() {
      const email: string = this.memberForm.controls['email'].value;
      const roles: RoleCard[] = this.selectRolesComponent.getSelectedRoles();
      this.boardMemberService.addBoardMember(this.boardId, email, roles)
         .subscribe(
            (result: BoardMemberResponce) => {
               if (result.status == 'ok') {
                  this.router.navigate(['/members', this.boardId]);
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
   }

   protected updateBoardMember() {
      const roles: RoleCard[] = this.selectRolesComponent.getSelectedRoles();
      console.log(roles);
      this.boardMemberService.updateBoardMember(this.boardId, this.memberId, roles)
         .subscribe(
            (result: BoardMemberResponce) => {
               if (result.status == 'ok') {
                  this.router.navigate(['/members', this.boardId]);
               } else {
                  var errorText: string | undefined;
                  if (result.error) {
                     errorText = this.errors[result.error];
                     console.groupCollapsed(result.error);
                  }
                  if (!errorText) {
                     errorText = "Проверьте введённые данные или попробуйте позже";
                  }
                  this.alertService.showError(errorText)
               }
            }
         )
   }
}
