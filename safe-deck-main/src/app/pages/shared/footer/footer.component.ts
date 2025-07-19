import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'footer',
  imports: [],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FooterComponent {
   scrollToStart() {
      window.scrollTo({top: 0, behavior: 'smooth'});
   }
}
