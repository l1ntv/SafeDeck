import { CredentialPair } from "../secure/credential-pair.model";
import { RoleCard } from "../roles/role.model";

export interface CreatingCard {
   cardName: string;
   cardDescription: string;
   roles: RoleCard[];
   secureData: CredentialPair[]
}