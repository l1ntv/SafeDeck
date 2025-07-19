import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { Board } from "../../shared/model/boards/board.model";
import { map } from "rxjs/internal/operators/map";
import { catchError, Observable, of } from "rxjs";
import { BoardResponce } from "../../shared/model/boards/board-responce.model";
import { environment } from "../../../environments/environment";

@Injectable({ providedIn: 'root' })
export class BoardService {
   private readonly http = inject(HttpClient);
   private readonly apiUrl = environment.apiUrl;

   private boardsSignal = signal<Board[]>([]);

   private currentBoardnameSignal = signal<string>('');

   public updateUserBoards() {
      this.getUserBoards().subscribe(
         boards => this.boardsSignal.set(boards)
      )
   }

   public get boards() {
      return this.boardsSignal;
   }

   public get currentBoardName() {
      return this.currentBoardnameSignal;
   }

   public updateCurrentBoardName(name: string) {
      this.currentBoardnameSignal.set(name);
   }

   private getUserBoards(): Observable<Board[]>  {
      return this.http.get<Board[]>(`${this.apiUrl}/boards`);
   }

   public createBoard(boardName: string): Observable<BoardResponce> {
      return this.http.post<any>(`${this.apiUrl}/boards`, {boardName})
      .pipe(
         map(
            board => {
               return {status: 'ok', board: board};
            }
         ),
         catchError(
            error => {
               return of({status: 'error', error: `Ошибка ${error}!`});
            }
         )
      )
   }

   public deleteBoard(boardId: number): Observable<string> {
      return this.http.delete<any>(`${this.apiUrl}/boards/${boardId}/delete`)
      .pipe(
         map(
            () => 'ok'
         ),
         catchError(
            error => {
               return of(`Ошибка ${error.status}!`)
            }
         )
      )
   }

   public renameBoard(boardId: number, newBoardName: string): Observable<BoardResponce> {
      return this.http.patch<any>(`${this.apiUrl}/boards/${boardId}/rename`, {newBoardName})
      .pipe(
         map(
            board => {
               return {status: 'ok', board: board};
            }
         ),
         catchError(
            error => {
               return of({status: 'error', error: `Ошибка ${error}!`});
            }
         )
      )
   }
}