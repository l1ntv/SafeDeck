import { Component } from '@angular/core';
import { CredentialPair } from '../../../shared/model/secure/credential-pair.model';
import { TuiButton, TuiDialogContext } from '@taiga-ui/core';
import { injectContext } from '@taiga-ui/polymorpheus';
import { CardSecure } from '../../../shared/model/secure/card-secure.model';
import { TuiTable } from '@taiga-ui/addon-table';

@Component({
  selector: 'secure-data',
  imports: [TuiTable, TuiButton],
  templateUrl: './secure-data.component.html',
  styleUrl: './secure-data.component.css'
})
export class SecureDataComponent {
   public readonly context = injectContext<TuiDialogContext<void,CardSecure>>();
   protected readonly columns = ['name', 'value'];

   protected get cardId(): number {
      return this.context.data.cardId;
   }

   protected get cardName(): string {
      return this.context.data.cardName;
   }

   protected get credentials(): CredentialPair[] {
      return this.context.data.credentials;
   }

   protected closeDialog() {
      this.context.completeWith();
   }
}
