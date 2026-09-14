import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';

@Component({
  selector: 'app-navbar-left-menu',
  imports: [MatIconModule, MatButtonModule, MatMenuModule],
  templateUrl: './navbar-left-menu.component.html',
  styleUrl: './navbar-left-menu.component.css',
})
export class NavbarLeftMenuComponent {}
