import { HttpClient } from "@angular/common/http";
import { Injectable, inject, signal } from "@angular/core";
import { BoardMember } from "../../shared/model/board-members/board-member.model";
import { catchError, map, Observable, of } from "rxjs";
import { RoleCard } from "../../shared/model/roles/role.model";
import { BoardMemberResponce } from "../../shared/model/board-members/member-responce.model";
import { UniversalResponce } from "../../shared/model/universal-responce.model";
import { environment } from "../../../environments/environment";

@Injectable({ providedIn: 'root' })
export class BoardMemberService {
   private readonly http = inject(HttpClient);
   private readonly apiUrl = environment.apiUrl;

   private membersSignal = signal<BoardMember[]>([]);

   public updateBoardMembers(boardId: number) {
      this.getBoardMembers(boardId).subscribe(
         boardMembers => this.membersSignal.set(boardMembers)
      )
   }

   public addBoardMember(boardId: number, email: string, roles: RoleCard[]): Observable<BoardMemberResponce> {
      return this.http.post<any>(`${this.apiUrl}/board-members/${boardId}`, {email, roles})
      .pipe(
         map(
            boardMember => {
               return {status: 'ok', boardMember};
            }
         ),
         catchError(
            error => {
               console.log(error);
               return of({status: 'error', error: error.status});
            }
         )
      )
   }

   public deleteBoardMember(boardId: number, userId: number): Observable<UniversalResponce> {
      return this.http.delete<any>(`${this.apiUrl}/board-members/${boardId}/${userId}`)
      .pipe(
         map(
            () => {
               return {status: 'ok'};
            }
         ),
         catchError(
            error => {
               console.log(error);
               return of({status: 'error', error: error.status});
            }
         )
      )
   }

   public updateBoardMember(boardId: number, memberId: number, roles: RoleCard[]): Observable<BoardMemberResponce> {
      return this.http.patch<any>(`${this.apiUrl}/board-members/${boardId}/${memberId}`, roles)
      .pipe(
         map(
            boardMember => {
               return {status: 'ok', boardMember};
            }
         ),
         catchError(
            error => {
               console.log(error);
               return of({status: 'error', error: error.status});
            }
         )
      )
   }

   public getBoardMemberById(boardId: number, memberId: number): Observable<BoardMember> {
      return this.http.get<BoardMember>(`${this.apiUrl}/board-members/${boardId}/${memberId}`);
   }

   public get boardMembers() {
      return this.membersSignal;
   }

   private getBoardMembers(boardId: number): Observable<BoardMember[]>  {
      return this.http.get<BoardMember[]>(`${this.apiUrl}/board-members/${boardId}`);
   }
}