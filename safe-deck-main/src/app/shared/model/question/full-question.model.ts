import { Question } from "./question.model";

export interface FullQuestion {
   cardId: number;
   boardId: number;
   question: Question
}