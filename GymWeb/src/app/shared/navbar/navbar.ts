import { Component, HostListener } from '@angular/core';
import { Token } from '../../core/services/token';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css'],
})
export class Navbar {
  menuOpen = false;
  activeDropdown: string | null = null;
  isLoggedIn = false;

  constructor(private tokenService: Token, private router: Router) {
    this.checkLoginStatus();
    // subscribe to token changes to update login state dynamically
    this.tokenService.token$.subscribe(() => this.checkLoginStatus());
  }

  checkLoginStatus() {
    this.isLoggedIn = !!this.tokenService.getToken();
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
    if (!this.menuOpen) this.activeDropdown = null;
  }

  toggleDropdown(name: string) {
    this.activeDropdown = this.activeDropdown === name ? null : name;
  }

  logout(): void {
    this.tokenService.clearToken();
    this.router.navigate(['/auth/login']);
  }

  // Close menu when clicking outside or on any menu item
  closeMenu() {
    this.menuOpen = false;
    this.activeDropdown = null;
  }

  // Optional: close menu on ESC key
  // @HostListener('document:keydown.escape', ['$event'])
  // onEscape(event: KeyboardEvent) {
  //   this.closeMenu();
  // }
}
