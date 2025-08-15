import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface Ticket {
  id: number;
  title: string;
  description: string;
  status: string;
  createdAt: Date;
}

@Component({
  selector: 'app-support-ticket-admin',
  imports: [CommonModule, FormsModule],
  templateUrl: './support-ticket-admin.html',
  styleUrls: ['./support-ticket-admin.css'],
})
export class SupportTicketAdmin implements OnInit {
  tickets: Ticket[] = [];
  selectedTicket?: Ticket;

  constructor() {}

  ngOnInit(): void {
    // Example tickets (replace with API call)
    this.tickets = [
      {
        id: 1,
        title: 'Login Issue',
        description: 'Cannot login to account',
        status: 'Open',
        createdAt: new Date(),
      },
      {
        id: 2,
        title: 'Payment Failed',
        description: 'Payment did not go through',
        status: 'Pending',
        createdAt: new Date(),
      },
    ];
  }

  selectTicket(ticket: Ticket): void {
    this.selectedTicket = ticket;
  }

  getTicketStatus(): string {
    return this.selectedTicket?.status?.replace(' ', '') || '';
  }

  closeTicket(): void {
    if (this.selectedTicket) {
      this.selectedTicket.status = 'Closed';
      this.selectedTicket = undefined; // Deselect after closing
    }
  }
}
