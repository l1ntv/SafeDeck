import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { catchError, map, Observable, of } from "rxjs";
import { SecureData } from "../../shared/model/secure/secure-data.model";
import { environment } from "../../../environments/environment";
import { SecureDataResponce } from "../../shared/model/secure/secure-data-responce.model";

@Injectable({ providedIn: 'root' })
export class SecureDataService {
   private readonly http = inject(HttpClient);
   private readonly apiUrl = environment.apiUrl;
   
   public getSecureData(cardId: number): Observable<SecureDataResponce> {
      return this.http.get<SecureData>(`${this.apiUrl}/secure-data/${cardId}`)
      .pipe(
         map(
            secureData => {
               return {status: 'ok', secureData};
            }
         ),
         catchError(
            responce => {
               console.log(responce);
               return of({status: 'error', error: responce.message, errorCode: responce.status});
            }
         )
      );
   }
}