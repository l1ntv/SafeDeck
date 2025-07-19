import { Board } from "./board.model";

export interface BoardResponce {
   status: string;
   board?: Board;
   error?: string;
}