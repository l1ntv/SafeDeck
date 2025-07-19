import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { environment } from "../../../environments/environment";
import { Question } from "../../shared/model/question/question.model";
import { catchError, map, Observable, of } from "rxjs";
import { UniversalResponce } from "../../shared/model/universal-responce.model";

@Injectable({ providedIn: 'root' })
export class ControlQuestionService {
   private readonly http = inject(HttpClient);
   private readonly apiUrl = environment.apiUrl;

   public checkAnswer(questionId: number, givenAnswer: string, boardId: number, cardId: number): Observable<UniversalResponce> {
      return this.http.post<any>(`${this.apiUrl}/questions/${questionId}/check-question`, {givenAnswer, boardId, cardId})
         .pipe(
            map(
               (responce) => {
                  console.log(responce);
                  return {status: 'ok'};
               }
            ),
            catchError(
               responce => {
                  return of({status: 'error', error: responce.status});
               }
            )
         )
   }

   public getRandomQuestion(boardId: number): Observable<Question> {
      return this.http.get<Question>(`${this.apiUrl}/questions/${boardId}/random`);
   }
}