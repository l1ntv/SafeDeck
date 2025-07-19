import { StatusMember } from "../status-member.enum";

export interface LogData {
   logId: number,
   email: string,
   ip: string,
   viewTime: Date,
   cardName: string,
   status: StatusMember
}