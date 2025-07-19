import { SecureData } from "./secure-data.model";

export interface SecureDataResponce {
   status: string;
   secureData?: SecureData;
   error?: string;
   errorCode?: number
}