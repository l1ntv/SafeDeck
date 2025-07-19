import { HttpClient } from "@angular/common/http";
import { inject, Injectable, signal, WritableSignal } from "@angular/core";
import { catchError, map, Observable, of } from "rxjs";
import { UniversalResponce } from "../../shared/model/universal-responce.model";
import { Profile } from "../../shared/model/profile/profile.model";
import { environment } from "../../../environments/environment";

@Injectable({ providedIn: 'root' })
export class ProfileService {
   private readonly http = inject(HttpClient);
   private readonly apiUrl = environment.apiUrl;
   private publicNameSignal = signal<string>('Аноним');

   public get publicName(): WritableSignal<string> {
      return this.publicNameSignal;
   }

   public updateProfile() {
      this.getPublicName().subscribe(
         (profile: Profile) => this.publicNameSignal.set(profile.publicName)
      )
   }

   public changePublicName(newPublicName: string): Observable<UniversalResponce> {
      return this.http.put<any>(`${this.apiUrl}/profile`, {newPublicName})
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

   private getPublicName(): Observable<Profile> {
      return this.http.get<Profile>(`${this.apiUrl}/profile`);
   }
}