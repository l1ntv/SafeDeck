import { CredentialPair } from "./credential-pair.model";

export interface CardSecure {
   cardId: number;
   cardName: string;
   credentials: CredentialPair[];
}