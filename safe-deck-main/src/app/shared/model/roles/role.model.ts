import { Card } from "../cards/card.model";

export interface RoleCard {
   roleId: number;
   roleName: string;
   cards?: Card[]
}