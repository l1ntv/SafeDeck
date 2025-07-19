import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { SendSecureData } from '../../shared/model/secure/send-secure.model';
import { SendSecureService } from '../../services/send-secure/send-secure.service';
import { TuiTable } from '@taiga-ui/addon-table';
import { TuiButton } from '@taiga-ui/core';

@Component({
  selector: 'send-secure-view',
  imports: [TuiTable, TuiButton, RouterLink],
  templateUrl: './send-secure-view.component.html',
  styleUrl: './send-secure-view.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SendSecureViewComponent implements OnInit {
   private readonly activateRoute = inject(ActivatedRoute);
   private readonly sendSecureService = inject(SendSecureService);

   protected readonly columns = ['name', 'value'];

   protected data?: SendSecureData;
   protected hasData = signal<boolean>(false);
   
   ngOnInit() {
      this.activateRoute.params.subscribe(params => {
         const token = params["token"];
         this.sendSecureService.getSendSecureData(token).subscribe(
            data => {
               this.data = data;
               this.hasData.set(true);
            }
         )
      });
   }
}
