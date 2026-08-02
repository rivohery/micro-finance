import { Routes } from '@angular/router';
import { LoginComponent } from './features/auths/login/login.component';
import { ChangePswdComponent } from './features/auths/change-pswd/change-pswd.component';
import { MainLayoutComponent } from './features/main-layout/main-layout.component';
import { DashboardComponent } from './features/main-layout/admin/dashboard/dashboard.component';
import { UserMangementComponent } from './features/main-layout/admin/user-mangement/user-mangement.component';
import { SettingComponent } from './features/main-layout/admin/setting/setting.component';
import { CashOperationComponent } from './features/main-layout/employes/cash-operation/cash-operation.component';
import { ListClientComponent } from './features/main-layout/admin/customer-manage/list-client/list-client.component';
import { MyAccountComponent } from './features/main-layout/clients/my-account/my-account.component';
import { TransfertRequestComponent } from './features/main-layout/clients/transfert-request/transfert-request.component';
import { UserProfilComponent } from './features/main-layout/common/user-profil/user-profil.component';
import { authsGuard } from './core/guards/auths.guard';
import { UnauthorizedComponent } from './features/main-layout/common/unauthorized/unauthorized.component';
import { adminGuard } from './core/guards/admin.guard';
import { employeGuard } from './core/guards/employe.guard';
import { CreateClientComponent } from './features/main-layout/admin/customer-manage/create-client/create-client.component';
import { DetailsClientComponent } from './features/main-layout/admin/customer-manage/details-client/details-client.component';
import { CurrenciesComponent } from './features/main-layout/admin/currencies/currencies.component';
import { UpdateClientComponent } from './features/main-layout/admin/customer-manage/update-client/update-client.component';
import { CustomersComponent } from './features/main-layout/employes/customers/customers.component';
import { CustomerDetailsComponent } from './features/main-layout/employes/customer-details/customer-details.component';
import { AccountDetailsComponent } from './features/main-layout/employes/account-details/account-details.component';
import { AccountListComponent } from './features/main-layout/employes/account-list/account-list.component';
import { TransactionsComponent } from './features/main-layout/admin/transactions/transactions.component';
import { InterestRateTraceComponent } from './features/main-layout/admin/interest-rate-trace/interest-rate-trace.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'change-password',
    component: ChangePswdComponent,
  },
  {
    path: 'my-app',
    component: MainLayoutComponent,
    canActivate: [authsGuard],
    children: [
      {
        path: 'user-profil',
        component: UserProfilComponent,
      },
      {
        path: 'unauthorized',
        component: UnauthorizedComponent,
      },
      {
        path: 'admin',
        canActivate: [adminGuard],
        children: [
          { path: 'dashboard', component: DashboardComponent },
          { path: 'interest-rate-log', component: InterestRateTraceComponent },
          { path: 'utilisateurs', component: UserMangementComponent },
          { path: 'settings', component: SettingComponent },
          { path: 'currencies', component: CurrenciesComponent },
          { path: 'create-client', component: CreateClientComponent },
          { path: 'clients', component: ListClientComponent },
          { path: 'client/:clientId', component: DetailsClientComponent },
          { path: 'update-client/:clientId', component: UpdateClientComponent },
          { path: 'transactions', component: TransactionsComponent },
        ],
      },
      {
        path: 'managment',
        children: [
          {
            path: 'account-details/:accountNumber',
            component: AccountDetailsComponent,
            canActivate: [authsGuard],
          },
          {
            path: 'customers',
            component: CustomersComponent,
            canActivate: [employeGuard],
          },
          {
            path: 'customer-details/:customerId',
            component: CustomerDetailsComponent,
            canActivate: [employeGuard],
          },
          {
            path: 'operation-cash',
            component: CashOperationComponent,
            canActivate: [employeGuard],
          },
          {
            path: 'account-list',
            component: AccountListComponent,
            canActivate: [employeGuard],
          },
        ],
      },
      {
        path: 'clients',
        canActivate: [authsGuard],
        children: [
          { path: 'my-accounts', component: MyAccountComponent },
          { path: 'transfert', component: TransfertRequestComponent },
        ],
      },
    ],
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full',
  },
];
