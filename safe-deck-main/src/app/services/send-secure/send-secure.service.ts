import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { environment } from "../../../environments/environment";
import { catchError, map, Observable, of } from "rxjs";
import { SendSecureResponce } from "../../shared/model/secure/send-secure-responce.model";
import { SendSecureData } from "../../shared/model/secure/send-secure.model";

@Injectable({ providedIn: 'root' })
export class SendSecureService {
   private readonly http = inject(HttpClient);
   private readonly apiUrl = environment.apiUrl;

   public createSendSecureToken(cardId: number): Observable<SendSecureResponce> {
      return this.http.post<any>(`${this.apiUrl}/send-secure/${cardId}`, {})
      .pipe(
         map(
            responce => {
               return {status: 'ok', token: responce.token};
            }
         ),
         catchError(
            responce => {
               return of({status: 'error', error: responce.message, errorCode: responce.status});
            }
         )
      )
   }
   
   public getSendSecureData(token: string): Observable<SendSecureData> {
      return this.http.get<SendSecureData>(`${this.apiUrl}/send-secure/${token}`);
   }
}