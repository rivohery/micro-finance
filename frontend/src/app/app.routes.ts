import { Routes } from '@angular/router';
import { LoginComponent } from './features/auths/pages/login/login.component';
import { UserProfilComponent } from './features/auths/pages/user-profil/user-profil.component';
import { authsGuard } from './core/auth/guards/auths.guard';
import { CustomersComponent } from './features/customers/pages/customers/customers.component';
import { CustomerDetailsComponent } from './features/customers/pages/customer-details/customer-details.component';
import { AccountDetailsComponent } from './features/accounts/pages/account-details/account-details.component';
import { AccountListComponent } from './features/accounts/pages/account-list/account-list.component';
import { roleGuard } from './core/auth/guards/role.guard';
import { AppMainLayoutComponent } from './layouts/app-main-layout/app-main-layout.component';
import { CashOperationComponent } from './features/transactions/pages/cash-operation/cash-operation.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'change-password',
    loadComponent: () =>
      import('./features/auths/pages/change-pswd/change-pswd.component').then(
        (m) => m.ChangePswdComponent
      ),
  },
  {
    path: 'my-app',
    component: AppMainLayoutComponent,
    canActivate: [authsGuard],
    children: [
      {
        path: 'user-profil',
        component: UserProfilComponent,
      },
      {
        path: 'unauthorized',
        loadComponent: () =>
          import(
            './features/auths/pages/unauthorized/unauthorized.component'
          ).then((m) => m.UnauthorizedComponent),
      },
      {
        path: 'admin',
        canActivate: [roleGuard('ADMIN')],
        children: [
          {
            path: 'dashboard',
            loadComponent: () =>
              import(
                './features/dashboard/admin-dashboard/admin-dashboard.component'
              ).then((m) => m.AdminDashboardComponent),
          },
          {
            path: 'interest-rate-log',
            loadComponent: () =>
              import(
                './features/interest-rate/interest-rate-trace/interest-rate-trace.component'
              ).then((m) => m.InterestRateTraceComponent),
          },
          {
            path: 'utilisateurs',
            loadComponent: () =>
              import(
                './features/users/user-mangement/user-mangement.component'
              ).then((m) => m.UserMangementComponent),
          },
          {
            path: 'settings',
            loadComponent: () =>
              import(
                './features/account-type/account-type-managment/account-type-managment.component'
              ).then((m) => m.AccountTypeManagmentComponent),
          },
          {
            path: 'currencies',
            loadComponent: () =>
              import(
                './features/currency/currency-managment/currency-managment.component'
              ).then((m) => m.CurrencyManagmentComponent),
          },
          {
            path: 'create-client',
            loadComponent: () =>
              import(
                './features/customers/pages/create-customer/create-customer.component'
              ).then((m) => m.CreateCustomerComponent),
          },
          {
            path: 'clients',
            loadComponent: () =>
              import(
                './features/customers/pages/customer-admin-managment/customer-admin-managment.component'
              ).then((m) => m.CustomerAdminManagmentComponent),
          },
          {
            path: 'client/:clientId',
            loadComponent: () =>
              import(
                './features/customers/pages/customer-admin-detail/customer-admin-detail.component'
              ).then((m) => m.CustomerAdminDetailComponent),
          },
          {
            path: 'update-client/:clientId',
            loadComponent: () =>
              import(
                './features/customers/pages/update-customer/update-customer.component'
              ).then((m) => m.UpdateCustomerComponent),
          },
          {
            path: 'transactions',
            loadComponent: () =>
              import(
                './features/transactions/pages/transactions/transactions.component'
              ).then((m) => m.TransactionsComponent),
          },
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
            canActivate: [roleGuard(['EMPLOYE', 'ADMIN'])],
          },
          {
            path: 'customer-details/:customerId',
            component: CustomerDetailsComponent,
            canActivate: [roleGuard(['EMPLOYE', 'ADMIN'])],
          },
          {
            path: 'operation-cash',
            component: CashOperationComponent,
            canActivate: [roleGuard(['EMPLOYE', 'ADMIN'])],
          },
          {
            path: 'account-list',
            component: AccountListComponent,
            canActivate: [roleGuard(['EMPLOYE', 'ADMIN'])],
          },
        ],
      },
      {
        path: 'clients',
        canActivate: [authsGuard],
        children: [
          {
            path: 'my-accounts',
            loadComponent: () =>
              import(
                './features/accounts/pages/my-account/my-account.component'
              ).then((m) => m.MyAccountComponent),
          },
          {
            path: 'transfert',
            loadComponent: () =>
              import(
                './features/transactions/pages/transfert/transfert.component'
              ).then((m) => m.TransfertComponent),
          },
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
