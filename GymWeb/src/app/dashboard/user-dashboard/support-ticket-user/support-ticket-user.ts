import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface Ticket {
  id: number;
  subject: string;
  description: string;
  status: 'Open' | 'In Progress' | 'Resolved';
  date: string;
}

@Component({
  selector: 'app-support-ticket-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './support-ticket-user.html',
  styleUrls: ['./support-ticket-user.css'],
})
export class SupportTicketUser {
  tickets: Ticket[] = [
    {
      id: 101,
      subject: 'Login issue',
      description: 'Unable to login after reset.',
      status: 'Open',
      date: '2025-08-12',
    },
    {
      id: 102,
      subject: 'Payment failed',
      description: 'Tried to pay but got error.',
      status: 'Resolved',
      date: '2025-08-10',
    },
    {
      id: 103,
      subject: 'Workout plan not loading',
      description: 'The workout plan page stays blank.',
      status: 'Resolved',
      date: '2025-08-09',
    },
  ];

  newTicket: Partial<Ticket> = {};
  selectedTicket: Ticket | null = null;

  submitTicket() {
    if (this.newTicket.subject && this.newTicket.description) {
      const ticket: Ticket = {
        id: Math.floor(Math.random() * 1000),
        subject: this.newTicket.subject,
        description: this.newTicket.description,
        status: 'Open',
        date: new Date().toISOString().split('T')[0],
      };
      this.tickets.unshift(ticket);
      this.newTicket = {};
      alert('Ticket submitted successfully!');
    }
  }

  openTicket(ticket: Ticket) {
    this.selectedTicket = ticket;
  }

  closeModal(event: MouseEvent) {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.selectedTicket = null;
    }
  }
}
