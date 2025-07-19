import { inject, Injectable } from "@angular/core";
import { TuiAlertService, TuiDialogService } from "@taiga-ui/core";
import { TUI_CONFIRM } from "@taiga-ui/kit";

@Injectable({ providedIn: 'root' })
export class AlertService {
   private readonly alerts = inject(TuiAlertService);
   private readonly dialogs = inject(TuiDialogService);

   public showError(message: string): void {
      this.alerts
         .open(message, { label: 'Ошибка', appearance: 'negative' })
         .subscribe();
   }

   public showMessage(message: string): void {
      this.alerts
         .open(message, { appearance: 'positive' })
         .subscribe();
   }

   public confirmOperation(text: string,  method: Function) {
      this.dialogs
      .open<boolean>(TUI_CONFIRM, {
         label: 'Предупреждение',
         size: 's',
         data: {
            content: text,
            yes: 'Да',
            no: 'Нет',
         },
      })
      .subscribe(response => {
         if (response) {
            method();
         }
      })
   }
}