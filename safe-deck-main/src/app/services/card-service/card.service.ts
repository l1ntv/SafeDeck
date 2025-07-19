import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal } from "@angular/core";
import { catchError, map, Observable, of } from "rxjs";
import { AccessibleCards } from "../../shared/model/cards/accessible-cards.model";
import { CardResponce } from "../../shared/model/cards/card-responce.model";
import { UniversalResponce } from "../../shared/model/universal-responce.model";
import { AccessLevel } from "../../shared/model/cards/access-level.enum";
import { CreatingCard } from "../../shared/model/cards/creating-card.model";
import { environment } from "../../../environments/environment";

@Injectable({ providedIn: 'root' })
export class CardService {
   private readonly http = inject(HttpClient);
   private readonly apiUrl = environment.apiUrl;
   private accessibleCardsSignal = signal<AccessibleCards>({accessibleCards: [], accessLevel: AccessLevel.GUEST});

   public getAccessibleCardsSignal() {
      return this.accessibleCardsSignal;
   }

   public updateAccesibleCards(boardId: number) {
      this.getAccessibleCards(boardId).subscribe({
         next: accessibleCards => this.accessibleCardsSignal.set(accessibleCards),
         error: error => this.accessibleCardsSignal.set({accessibleCards: [], accessLevel: AccessLevel.GUEST})
      })
   }

   private getAccessibleCards(boardId: number): Observable<AccessibleCards> {
      return this.http.get<AccessibleCards>(`${this.apiUrl}/cards/${boardId}`);
   }

   public renameCard(boardId: number, cardId: number, newCardName: string): Observable<CardResponce> {
      return this.http.patch<any>(`${this.apiUrl}/cards/rename/${boardId}/${cardId}`, {newCardName})
         .pipe(
            map(
               card => {
                  return {status: 'ok', card: card};
               }
            ),
            catchError(
               error => {
                  return of({status: 'error', error: `Ошибка ${error}!`});
               }
            )
         )
   }

   public createCard(boardId: number, card: CreatingCard): Observable<CardResponce> {
      return this.http.post<any>(`${this.apiUrl}/cards/${boardId}`, card)
         .pipe(
            map(
               card => {
                  return {status: 'ok', card: card};
               }
            ),
            catchError(
               error => {
                  return of({status: 'error', error: `Ошибка ${error}!`});
               }
            )
         )
   }

   public changeDescriptionCard(boardId: number, cardId: number, newCardDescription: string): Observable<CardResponce> {
      return this.http.patch<any>(`${this.apiUrl}/cards/change-description/${boardId}/${cardId}`, {newCardDescription})
         .pipe(
            map(
               card => {
                  return {status: 'ok', card: card};
               }
            ),
            catchError(
               error => {
                  return of({status: 'error', error: `Ошибка ${error}!`});
               }
            )
         )
   }

   public deleteCard(boardId: number, cardId: number): Observable<UniversalResponce> {
      return this.http.delete<any>(`${this.apiUrl}/cards/${boardId}/${cardId}`)
         .pipe(
            map(
               () => {return {status: 'ok'}}
            ),
            catchError(
               error => {
                  return of({status: 'error', error: `Ошибка ${error}!`});
               }
            )
         )
   }
}