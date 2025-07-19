import { AccessLevel } from "./access-level.enum";
import { Card } from "./card.model";

export interface AccessibleCards {
   accessibleCards: Card[];
   accessLevel: AccessLevel;
}