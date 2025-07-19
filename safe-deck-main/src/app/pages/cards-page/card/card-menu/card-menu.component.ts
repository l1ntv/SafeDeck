import { ChangeDetectionStrategy, Component, output } from '@angular/core';
import { TuiActiveZone } from '@taiga-ui/cdk/directives/active-zone';
import { TuiObscured } from '@taiga-ui/cdk/directives/obscured';
import { TuiIcon, TuiDropdown, TuiButton } from '@taiga-ui/core';

@Component({
   selector: 'card-menu',
   imports: [TuiIcon, TuiButton, TuiDropdown, TuiActiveZone, TuiObscured],
   templateUrl: './card-menu.component.html',
   styleUrl: './card-menu.component.css',
   changeDetection: ChangeDetectionStrategy.OnPush
})
export class CardMenuComponent {
   selectMenuOption = output<number>();

   protected clickMenuOption(numberOption: number) {
      this.open = false;
      this.selectMenuOption.emit(numberOption);
   }

   protected open = false;

   protected showMenu(): void {
      this.open = !this.open;
   }

   protected onObscured(obscured: boolean): void {
      if (obscured) {
         this.open = false;
      }
   }

   protected onActiveZone(active: boolean): void {
      this.open = active && this.open;
   }
}
