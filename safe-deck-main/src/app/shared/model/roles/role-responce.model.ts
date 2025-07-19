import { RoleCard } from "./role.model";

export interface RoleResponce {
   status: string;
   role?: RoleCard;
   error?: string;
}