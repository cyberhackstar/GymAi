import { Component } from '@angular/core';
import { Token } from '../../../core/services/token';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  isLoggedIn = false;

  constructor(private tokenService: Token, private router: Router) {
    this.isLoggedIn = !!this.tokenService.getToken();
  }

  logout(): void {
    this.tokenService.clearToken();
    this.router.navigate(['/auth/login']);
  }
}
