import { RoleCard } from "../roles/role.model";

export interface BoardMember {
   id: number;
   publicName: string;
   email: string;
   roles: RoleCard[]
}