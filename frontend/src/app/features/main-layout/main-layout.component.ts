import {
  BreakpointObserver,
  Breakpoints,
  LayoutModule,
} from '@angular/cdk/layout';
import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../../shared/components/layout/sidebar/sidebar.component';
import { NavbarLeftMenuComponent } from '../../shared/components/layout/navbar-left-menu/navbar-left-menu.component';

@Component({
  selector: 'app-main-layout',
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
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.css',
})
export class MainLayoutComponent implements OnInit {
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
