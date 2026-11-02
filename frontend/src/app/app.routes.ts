import { Routes } from '@angular/router';
import { LoginComponent } from './features/auths/pages/login/login.component';
import { authsGuard } from './core/auth/guards/auths.guard';
import { roleGuard } from './core/auth/guards/role.guard';
import { AppMainLayoutComponent } from './layouts/app-main-layout/app-main-layout.component';

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
        loadComponent: () =>
          import(
            './features/auths/pages/user-profil/user-profil.component'
          ).then((m) => m.UserProfilComponent),
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
            canActivate: [authsGuard],
            loadComponent: () =>
              import(
                './features/accounts/pages/account-details/account-details.component'
              ).then((m) => m.AccountDetailsComponent),
          },
          {
            path: 'customers',
            canActivate: [roleGuard(['EMPLOYE', 'ADMIN'])],
            loadComponent: () =>
              import(
                './features/customers/pages/customers/customers.component'
              ).then((m) => m.CustomersComponent),
          },
          {
            path: 'customer-details/:customerId',
            canActivate: [roleGuard(['EMPLOYE', 'ADMIN'])],
            loadComponent: () =>
              import(
                './features/customers/pages/customer-details/customer-details.component'
              ).then((m) => m.CustomerDetailsComponent),
          },
          {
            path: 'operation-cash',
            canActivate: [roleGuard(['EMPLOYE', 'ADMIN'])],
            loadComponent: () =>
              import(
                './features/transactions/pages/cash-operation/cash-operation.component'
              ).then((m) => m.CashOperationComponent),
          },
          {
            path: 'account-list',
            canActivate: [roleGuard(['EMPLOYE', 'ADMIN'])],
            loadComponent: () =>
              import(
                './features/accounts/pages/account-list/account-list.component'
              ).then((m) => m.AccountListComponent),
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
