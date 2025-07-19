import { Routes } from '@angular/router';
import { WelcomePageComponent } from './pages/welcome-page/welcome-page.component';
import { BoardsPageComponent } from './pages/boards-page/boards-page.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { RouteGuardAuth } from './guard-functions/route-guard-auth.function';
import { CardsPageComponent } from './pages/cards-page/cards-page.component';
import { CardPageComponent } from './pages/card-page/card-page.component';
import { RolesPageComponent } from './pages/roles-page/roles-page.component';
import { MembersPageComponent } from './pages/members-page/members-page.component';
import { MemberPageComponent } from './pages/member-page/member-page.component';
import { PasswordGeneratorComponent } from './pages/password-generator/password-generator.component';
import { LogsComponent } from './pages/logs/logs.component';
import { SendSecureViewComponent } from './pages/send-secure-view/send-secure-view.component';

export const routes: Routes = [
   {path: 'boards', component: BoardsPageComponent, canActivate: [RouteGuardAuth]},
   {path: 'send-secure/:token', component: SendSecureViewComponent},
   {path: 'roles/:board-id', component: RolesPageComponent, canActivate: [RouteGuardAuth]},
   {path: 'logs/:board-id', component: LogsComponent, canActivate: [RouteGuardAuth]},
   {path: 'members/:board-id', component: MembersPageComponent, canActivate: [RouteGuardAuth]},
   {path: 'member/:board-id/:member-id', component: MemberPageComponent, canActivate: [RouteGuardAuth]},
   {path: 'cards/:board-id', component: CardsPageComponent, canActivate: [RouteGuardAuth]},
   {path: 'card/:board-id/:card-id', component: CardPageComponent, canActivate: [RouteGuardAuth]},
   {path: 'profile', component: ProfileComponent, canActivate: [RouteGuardAuth]},
   {path: 'password-generator', component: PasswordGeneratorComponent},
   {path: '**', component: WelcomePageComponent},
];
