import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TuiButton } from '@taiga-ui/core';
import { DialogAuthorizationService } from '../../welcome-page/dialog-authorization/dialog-authorization.service';

@Component({
  selector: 'header',
  imports: [RouterLink, TuiButton],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HeaderComponent {
   protected dialogAuthService = inject(DialogAuthorizationService);

   scrollToFunctions() {
      document.getElementById('functions')?.scrollIntoView({behavior: 'smooth'});
   }
}
