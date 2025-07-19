import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TuiTable } from '@taiga-ui/addon-table';
import { TuiButton, TuiIcon, TuiScrollbar } from '@taiga-ui/core';
import { MainMenuComponent } from '../shared/main-menu/main-menu.component';
import { LogService } from '../../services/log-service/log.service';

@Component({
  selector: 'app-logs',
  imports: [MainMenuComponent, RouterLink, TuiIcon, TuiButton, TuiTable, TuiScrollbar],
  templateUrl: './logs.component.html',
  styleUrl: './logs.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LogsComponent implements OnInit {
   private readonly activateRoute = inject(ActivatedRoute);
   private readonly logService = inject(LogService);

   protected readonly columns = ['email', 'ip', 'card', 'time', 'status'];
   protected readonly colors: Record<string, string> = {
      'OK': "green",
      'SUSPECT': "yellow",
      'HACK': "red"
   }
   protected readonly statusTexts: Record<string, string> = {
      'OK': "Проверено",
      'SUSPECT': "Подозрительно",
      'HACK': "Взлом"
   }

   protected boardId: number = -1;
   protected logs = this.logService.logs;

   ngOnInit() {
      this.activateRoute.params.subscribe(params => {
         this.boardId = params["board-id"];
         this.logService.updateBoardLogs(this.boardId);
      });
   }

   protected getDatetimeString(datetime: Date) {
      return `${datetime.toLocaleTimeString('ru-RU').slice(0, 5)} - ${datetime.toLocaleDateString('ru-RU')}`;
   }
}
