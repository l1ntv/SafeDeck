import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { RoleCard } from "../../shared/model/roles/role.model";
import { catchError, map, Observable, of } from "rxjs";
import { RoleResponce } from "../../shared/model/roles/role-responce.model";
import { Card } from "../../shared/model/cards/card.model";
import { environment } from "../../../environments/environment";

@Injectable({ providedIn: 'root' })
export class RoleService {
   private readonly http = inject(HttpClient);
   private readonly apiUrl = environment.apiUrl;
   private rolesSignal = signal<RoleCard[]>([]);

   public get boardRoles() {
      return this.rolesSignal;
   }

   public updateBoardRoles(boardId: number) {
      this.getBoardRoles(boardId).subscribe(
         roles => this.rolesSignal.set(roles)
      )
   }

   public createRole(boardId: number, roleName: string): Observable<RoleResponce> {
      return this.http.post<any>(`${this.apiUrl}/roles/${boardId}`, {roleName})
      .pipe(
         map(
            role => {
               return {status: 'ok', role};
            }
         ),
         catchError(
            error => {
               return of({status: 'error', error: `Ошибка ${error}!`});
            }
         )
      )
   }

   public renameRole(boardId: number, roleId: number, newRoleName: string): Observable<RoleResponce> {
      return this.http.patch<any>(`${this.apiUrl}/roles/${boardId}/rename/${roleId}`, {newRoleName})
      .pipe(
         map(
            role => {
               return {status: 'ok', role};
            }
         ),
         catchError(
            error => {
               return of({status: 'error', error: `Ошибка ${error}!`});
            }
         )
      )
   }

   public deleteRole(boardId: number, roleId: number): Observable<number> {
      return this.http.delete<any>(`${this.apiUrl}/roles/${boardId}/${roleId}`)
      .pipe(
         map(
            () => 200
         ),
         catchError(
            responce => of(responce.status)
         )
      )
   }

   public updateCardsRole(boardId: number, roleId: number, cards: Card[]): Observable<number> {
      return this.http.patch<any>(`${this.apiUrl}/roles/${boardId}/${roleId}`, cards)
      .pipe(
         map(
            () => 200
         ),
         catchError(
            responce => of(responce.status)
         )
      )
   }

   public getBoardRoles(boardId: number): Observable<RoleCard[]>  {
      return this.http.get<RoleCard[]>(`${this.apiUrl}/roles/${boardId}`);
   }
}