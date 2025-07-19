import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { TuiButton, TuiDialogContext, TuiIcon } from '@taiga-ui/core';
import { injectContext } from '@taiga-ui/polymorpheus';
import { SendSecureService } from '../../../services/send-secure/send-secure.service';
import { SendSecureResponce } from '../../../shared/model/secure/send-secure-responce.model';
import { environment } from '../../../../environments/environment';
import { AlertService } from '../../../services/alert-service/alert.service';

@Component({
  selector: 'send-secure',
  imports: [TuiButton, TuiIcon],
  templateUrl: './send-secure.component.html',
  styleUrl: './send-secure.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SendSecureComponent {
   public readonly context = injectContext<TuiDialogContext<void,number>>();
   private readonly sendSecureService = inject(SendSecureService);
   private readonly alertService = inject(AlertService);

   protected link: string = '';
   protected hasLink = signal(false);

   protected generateLink() {
      this.sendSecureService.createSendSecureToken(this.context.data)
      .subscribe(
         (result: SendSecureResponce) => {
            if (result.status == 'ok') {
               this.hasLink.set(true);
               this.link = `${environment.frontUrl}/send-secure/${result.token}`;
            } else {
               this.hasLink.set(false);
               this.alertService.showError("Не удалось создать ссылку");
            }
         }
      )
   }

   protected copyToClipboard() {
      navigator.clipboard.writeText(this.link)
      .then(
         () => this.alertService.showMessage("Ссылка скопирована")
      )
      .catch(err => {
         this.alertService.showError("Не удалось скопировать в буфер обмена")
         console.error(err);
      });
   }
}
