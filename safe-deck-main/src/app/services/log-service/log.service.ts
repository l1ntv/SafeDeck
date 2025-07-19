import { HttpClient } from "@angular/common/http";
import { Injectable, inject, signal } from "@angular/core";
import { environment } from "../../../environments/environment";
import { LogData } from "../../shared/model/log/log.model";
import { StatusMember } from "../../shared/model/status-member.enum";
import { map, Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class LogService {
   private readonly http = inject(HttpClient);
   private readonly apiUrl = environment.apiUrl;

   private logSignal = signal<LogData[]>([]);

   public get logs() {
      return this.logSignal;
   }

   public updateBoardLogs(boardId: number) {
      this.getBoardLogs(boardId).subscribe(
         (logs: LogData[]) => this.logSignal.set(logs)
      )
   }

   public updateBoardLogsMoke(boardId: number) {
      this.logSignal.set([
         {logId: 1, email: "zverev19728@gmail.com", cardName: "Карточка 1", viewTime: new Date(), ip: "123.0.0.13", status: StatusMember.OK},
         {logId: 2, email: "zverev19728@gmail.com", cardName: "Карточка 1", viewTime: new Date(), ip: "123.0.0.13", status: StatusMember.OK},
         {logId: 3, email: "zverev19728@gmail.com", cardName: "Карточка 2", viewTime: new Date(), ip: "123.0.0.13", status: StatusMember.HACK},
         {logId: 4, email: "zverev220@gmail.com", cardName: "Карточка 1", viewTime: new Date(), ip: "123.0.0.13", status: StatusMember.OK},
         {logId: 5, email: "zverev19728@gmail.com", cardName: "Карточка 4", viewTime: new Date(), ip: "123.0.0.13", status: StatusMember.SUSPECT},
         {logId: 6, email: "zverev19728@gmail.com", cardName: "Карточка 3", viewTime: new Date(), ip: "123.0.0.13", status: StatusMember.OK},
         {logId: 7, email: "vlint@gmail.com", cardName: "Карточка 5", viewTime: new Date(), ip: "123.0.0.13", status: StatusMember.SUSPECT},
         {logId: 8, email: "zverev19728@gmail.com", cardName: "Карточка 1", viewTime: new Date(), ip: "123.0.0.13", status: StatusMember.OK},
      ])
   }

   private getBoardLogs(boardId: number): Observable<LogData[]> {
      return this.http.get<LogData[]>(`${this.apiUrl}/logs/${boardId}`)
      .pipe(
         map(events => events.map(log => ({
            ...log,
            viewTime: new Date(log.viewTime)
         })))
       )
   }
}