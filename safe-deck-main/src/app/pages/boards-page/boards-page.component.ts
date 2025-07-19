import { ChangeDetectionStrategy, Component, inject, QueryList, ViewChildren } from '@angular/core';
import { MainMenuComponent } from "../shared/main-menu/main-menu.component";
import { BoardInfoComponent } from './board-info/board-info.component';
import { BoardService } from '../../services/board-service/board.service';
import { TuiIcon, TuiScrollbar } from '@taiga-ui/core';
import { BoardResponce } from '../../shared/model/boards/board-responce.model';

@Component({
  selector: 'boards-page',
  imports: [MainMenuComponent, BoardInfoComponent, TuiIcon, TuiScrollbar],
  templateUrl: './boards-page.component.html',
  styleUrl: './boards-page.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BoardsPageComponent {
   private readonly boardService = inject(BoardService);
   
   protected boards = this.boardService.boards;

   @ViewChildren(BoardInfoComponent)
   protected boardsElements!: QueryList<BoardInfoComponent>;

   protected createBoard() {
      const boardName: string = `Проект ${this.boards().length + 1}`;
      this.boardService.createBoard(boardName).subscribe(
         (result: BoardResponce) => {
            this.boardService.updateUserBoards();
            
            // выделяем созданный board для редактирования
            if (result.status == 'ok') {
               setTimeout(() => {
                  var elem = this.boardsElements.find(
                     boardsElement => boardsElement.board()?.boardId == result?.board?.boardId
                  );
                  //console.log(`elem.id = ${elem?.board()?.boardId}, result.board.id = ${result?.board?.boardId}`);
                  elem?.startEdit();
               }, 100);
            }
            
         }
      )
   }
}
