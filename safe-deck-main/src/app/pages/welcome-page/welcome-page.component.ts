import { ChangeDetectionStrategy, Component } from '@angular/core';
import { TuiButton } from '@taiga-ui/core';
import { HeaderComponent } from '../shared/header/header.component';
import { FooterComponent } from '../shared/footer/footer.component';
import { InfoCardComponent } from './info-card/info-card.component';

@Component({
   selector: 'app-welcome-page',
   imports: [TuiButton, HeaderComponent, FooterComponent, InfoCardComponent],
   templateUrl: './welcome-page.component.html',
   styleUrl: './welcome-page.component.css',
   changeDetection: ChangeDetectionStrategy.OnPush
})
export class WelcomePageComponent {

}
