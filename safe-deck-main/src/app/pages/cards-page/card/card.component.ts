import { ChangeDetectionStrategy, Component, inject, input, signal } from '@angular/core';
import { Card } from '../../../shared/model/cards/card.model';
import { TuiButton, tuiDialog, TuiDialogService } from '@taiga-ui/core';
import { ColorService } from '../../../services/color-service/color.service';
import { CardService } from '../../../services/card-service/card.service';
import { CardResponce } from '../../../shared/model/cards/card-responce.model';
import { CardMenuComponent } from './card-menu/card-menu.component';
import { UniversalResponce } from '../../../shared/model/universal-responce.model';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { TuiAutoFocus } from '@taiga-ui/cdk';
import { SecureDataComponent } from '../../shared/secure-data/secure-data.component';
import { SecureDataService } from '../../../services/secure-data/secure-data.service';
import { AlertService } from '../../../services/alert-service/alert.service';
import { SendSecureComponent } from '../../shared/send-secure/send-secure.component';
import { ControlQuestionComponent } from '../../shared/control-question/control-question.component';
import { SecureDataResponce } from '../../../shared/model/secure/secure-data-responce.model';
import { ControlQuestionService } from '../../../services/control-questions/control-questions.service';
import { Question } from '../../../shared/model/question/question.model';
import { FullQuestion } from '../../../shared/model/question/full-question.model';
import { AuthorizationService } from '../../../services/authorization/authorization.service';
import { Router } from '@angular/router';

@Component({
  selector: 'card',
  imports: [TuiButton, CardMenuComponent, TuiAutoFocus, ReactiveFormsModule],
  templateUrl: './card.component.html',
  styleUrl: './card.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CardComponent {
   private readonly colorService = inject(ColorService);
   private readonly cardService = inject(CardService);
   private readonly secureDataService = inject(SecureDataService);
   private readonly alertService = inject(AlertService);
   private readonly questionsService = inject(ControlQuestionService);
   private readonly authService = inject(AuthorizationService);
   private readonly router = inject(Router);

   private readonly secureDataDialog = tuiDialog(SecureDataComponent, {
      dismissible: true,
      size: 's',
   });

   private readonly sendSecureDialog = tuiDialog(SendSecureComponent, {
      dismissible: true,
      size: 's',
   });

   private readonly controlQuestionDialog = tuiDialog(ControlQuestionComponent, {
      dismissible: true,
      size: 's',
   });

   card = input.required<Card>();
   boardId = input.required<number>();

   protected cardName = new FormControl('');
   protected cardDescription = new FormControl('');

   protected isNameEdited = signal<boolean>(false);
   protected isDescriptionEdited = signal<boolean>(false);

   protected selectMenuOption(numberOption: number) {
      const menuOptions = [this.startEditName, this.startEditDescription, this.confirmDeleteСard, this.showSendSecure];
      menuOptions[numberOption].call(this);
   }

   protected startEditName() {
      this.isNameEdited.set(true);
      this.cardName.setValue(this.card().cardName)
   }
   
   protected cancelEdit() {
      this.isNameEdited.set(false);
   }

   protected editName() {
      const cardId: number = this.card().cardId || -1;
      const newCardName: string = this.cardName.value || '';
      if (cardId >= 0 && newCardName.trim() != '') {
         this.cardService.renameCard(this.boardId(), cardId, newCardName)
            .subscribe(
               (result: CardResponce) => {
                  if (result.status == 'ok') {
                     this.cardService.updateAccesibleCards(this.boardId());
                  } else {
                     // здесь может быть вывод ошибок пользователю
                  }
               }
            )
      }
      this.isNameEdited.set(false);
   }

   protected startEditDescription() {
      this.isDescriptionEdited.set(true);
      this.cardDescription.setValue(this.card().cardDescription);
   }
   
   protected cancelEditDescription() {
      this.isDescriptionEdited.set(false);
   }

   protected editDescription() {
      const cardId: number = this.card().cardId || -1;
      const newCardDescription: string = this.cardDescription.value || '';
      if (cardId >= 0 && newCardDescription.trim() != '') {
         this.cardService.changeDescriptionCard(this.boardId(), cardId, newCardDescription)
            .subscribe(
               (result: CardResponce) => {
                  if (result.status == 'ok') {
                     this.cardService.updateAccesibleCards(this.boardId());
                  } else {
                     // здесь может быть вывод ошибок пользователю
                  }
               }
            )
      }
      this.isDescriptionEdited.set(false);
   }

   protected confirmDeleteСard() {
      this.alertService.confirmOperation(
         "Вы уверены, что хотите удалить карточку? Это действие необратимо",
         this.deleteCard.bind(this)
      )
   }

   protected deleteCard() {
      const cardId: number = this.card().cardId || -1;
      if (cardId >= 0) {
         this.cardService.deleteCard(this.boardId(), cardId)
            .subscribe(
               (result: UniversalResponce) => {
                  if (result.status == 'ok') {
                     this.cardService.updateAccesibleCards(this.boardId());
                  } else {
                     // здесь может быть вывод ошибок пользователю
                  }
               }
            )
      }
   }

   protected showSecureData() {
      this.secureDataService.getSecureData(this.card().cardId)
      .subscribe(
         (responce: SecureDataResponce) => {
            if (responce.status == "ok") {
               this.secureDataDialog({
                  cardId: this.card().cardId,
                  cardName: this.card().cardName,
                  credentials: responce.secureData?.credentials || []
               }).subscribe();
            } else {
               if (responce.errorCode == 418) {
                  this.questionsService.getRandomQuestion(this.boardId())
                  .subscribe(
                     (question: Question) => {
                        const fullQuestion: FullQuestion = {
                           question, boardId: this.boardId(), cardId: this.card().cardId
                        }
                        this.controlQuestionDialog(fullQuestion).subscribe();
                     }
                  )
               } else if (responce.errorCode == 423) {
                  this.authService.logout();
                  this.router.navigateByUrl("/")
               } else {
                  this.alertService.showError("Неизвестная ошибка");
               }
            }         
         }
      )
   }

   protected showSendSecure() {
      this.sendSecureDialog(this.card().cardId).subscribe();
   }
   
   protected selectInputText(event: FocusEvent) {
      const input = event.target as HTMLInputElement;
      input?.select();
   }

   protected getBorderCard():string {
      return '6px solid' + this.colorService.getAccentColor(this.card().cardId);
   }

   protected getBackgroundColor():string {
      return this.colorService.getBackgroundColor(this.card().cardId);
   }
}
