import { ChangeDetectionStrategy, Component, inject, input, signal } from '@angular/core';
import { Board } from '../../../shared/model/boards/board.model';
import { TuiButton, TuiIcon } from '@taiga-ui/core';
import { BoardService } from '../../../services/board-service/board.service';
import { BoardResponce } from '../../../shared/model/boards/board-responce.model';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import {TuiAutoFocus} from '@taiga-ui/cdk';
import { AlertService } from '../../../services/alert-service/alert.service';

@Component({
   selector: 'board-info',
   imports: [TuiIcon, TuiButton, TuiAutoFocus, ReactiveFormsModule],
   templateUrl: './board-info.component.html',
   styleUrl: './board-info.component.css',
   changeDetection: ChangeDetectionStrategy.OnPush
})
export class BoardInfoComponent {
   private readonly boardService = inject(BoardService);
   private readonly alertService = inject(AlertService);

   board = input.required<Board>();

   protected boardName = new FormControl('');

   protected isNameEdited = signal<boolean>(false);

   public startEdit() {
      this.isNameEdited.set(true);
      this.boardName.setValue(this.board().boardName);
   }

   protected cancelEdit() {
      this.isNameEdited.set(false);
   }

   protected editName() {
      const boardId: number = this.board().boardId || -1;
      const newBoardName: string = this.boardName.value || '';
      console.log(`edit name: id = ${boardId}, name = ${newBoardName}`);
      if (boardId >= 0 && newBoardName.trim() != '') {
         this.boardService.renameBoard(boardId, newBoardName)
            .subscribe(
               (result: BoardResponce) => {
                  if (result.status == 'ok') {
                     this.boardService.updateUserBoards();
                  } else {
                     // здесь будет вывод ошибок пользователю
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

   protected confirmDeleteBoard() {
      this.alertService.confirmOperation(
         "Вы уверены, что хотите удалить проект? Это действие необратимо",
         this.deleteBoard.bind(this)
      )
   }

   private deleteBoard() {
      const boardId: number = this.board().boardId || -1;
      if (boardId >= 0) {
         this.boardService.deleteBoard(boardId)
            .subscribe(
               (result: string) => {
                  if (result == 'ok') {
                     this.boardService.updateUserBoards();
                  }
               }
            )
      }
   }
}
