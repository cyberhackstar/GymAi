import { Component, HostListener } from '@angular/core';
import { Token } from '../../core/services/token';
import { NavigationEnd, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs';

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
  userRole: string | null = null; // NEW: track user role
  currentRoute = '';

  constructor(private tokenService: Token, private router: Router) {
    this.checkLoginStatus();

    // Subscribe to token changes to update login state dynamically
    this.tokenService.token$.subscribe(() => this.checkLoginStatus());

    // Subscribe to route changes to track current route
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.url;
      });
  }

  checkLoginStatus() {
    // this.isLoggedIn = !!this.tokenService.getToken();
    const token = this.tokenService.getToken();
    this.isLoggedIn = !!token;

    if (this.isLoggedIn) {
      // Assuming token contains role claim
      this.userRole = this.tokenService.getName() ?? null; // implement in Token service
    } else {
      this.userRole = null;
    }
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
    if (!this.menuOpen) this.activeDropdown = null;

    // Prevent body scroll when menu is open
    if (this.menuOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
  }

  toggleDropdown(name: string) {
    this.activeDropdown = this.activeDropdown === name ? null : name;
  }

  logout(): void {
    this.tokenService.clearToken();
    this.router.navigate(['/login']);
    this.closeMenu();
  }

  closeMenu() {
    this.menuOpen = false;
    this.activeDropdown = null;
    document.body.style.overflow = '';
  }

  @HostListener('document:keydown', ['$event'])
  onEscape(event: KeyboardEvent) {
    if (event.key === 'Escape' && this.menuOpen) {
      this.closeMenu();
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    if (event.target.innerWidth > 991 && this.menuOpen) {
      this.closeMenu();
    }
  }
}
