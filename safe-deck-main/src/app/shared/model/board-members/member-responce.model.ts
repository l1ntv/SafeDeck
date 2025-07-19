import { BoardMember } from "./board-member.model";

export interface BoardMemberResponce {
   status: string;
   boardMember?: BoardMember;
   error?: string;
}