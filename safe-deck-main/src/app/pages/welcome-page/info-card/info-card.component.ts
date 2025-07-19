import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { TuiIcon } from '@taiga-ui/core';

@Component({
  selector: 'info-card',
  imports: [TuiIcon],
  templateUrl: './info-card.component.html',
  styleUrl: './info-card.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InfoCardComponent {
   @Input() iconName!: string;
   @Input() text!: string;

   getIconTag(): string {
      return "@tui." + (this.iconName || 'ban');
   }
}
