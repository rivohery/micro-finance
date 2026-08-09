import {
  BreakpointObserver,
  Breakpoints,
  LayoutModule,
} from '@angular/cdk/layout';
import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { NavbarLeftMenuComponent } from '../navbar-left-menu/navbar-left-menu.component';

@Component({
  selector: 'app-app-main-layout',
  imports: [
    RouterOutlet,
    MatToolbarModule, // Pour la barre du haut
    MatSidenavModule, // Pour le mat-drawer-container et mat-drawer
    MatIconModule, // Pour les icônes
    MatButtonModule, // Pour les boutons (mat-button, mat-icon-button)
    LayoutModule, // Indispensable pour BreakpointObserver],
    SidebarComponent,
    NavbarLeftMenuComponent,
  ],
  templateUrl: './app-main-layout.component.html',
  styleUrl: './app-main-layout.component.css',
})
export class AppMainLayoutComponent {
  isMobile = false;
  breakpointObserver = inject(BreakpointObserver);

  ngOnInit() {
    this.breakpointObserver
      .observe([Breakpoints.Handset])
      .subscribe((result) => {
        this.isMobile = result.matches;
      });
  }
}
