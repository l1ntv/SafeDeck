import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FullQuestion } from '../../../shared/model/question/full-question.model';
import { TuiButton, TuiDialogContext } from '@taiga-ui/core';
import { injectContext } from '@taiga-ui/polymorpheus';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ControlQuestionService } from '../../../services/control-questions/control-questions.service';

@Component({
  selector: 'app-control-question',
  imports: [TuiButton, FormsModule, ReactiveFormsModule],
  templateUrl: './control-question.component.html',
  styleUrl: './control-question.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ControlQuestionComponent {
   public readonly context = injectContext<TuiDialogContext<void,FullQuestion>>();
   private readonly questionService = inject(ControlQuestionService);

   protected answerForm: FormGroup = new FormGroup({
      answer: new FormControl('', Validators.required)
   })

   protected get questionText(): string {
      return this.context.data.question.question;
   }

   protected checkAnswer() {
      const questionId = this.context.data.question.questionId;
      const givenAnswer = this.answerForm.controls['answer'].value;
      const boardId = this.context.data.boardId;
      const cardId = this.context.data.cardId;
      this.questionService.checkAnswer(questionId, givenAnswer, boardId, cardId)
      .subscribe(
         () => {
            this.context.completeWith();
         }
      )
   }
}
