import { CredentialPair } from "./credential-pair.model";

export interface SendSecureData {
   cardName: string,
   cardDescription: string,
   credentials: CredentialPair[]
}