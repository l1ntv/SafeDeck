import { Card } from "./card.model";

export interface CardResponce {
   status: string;
   card?: Card;
   error?: string;
}