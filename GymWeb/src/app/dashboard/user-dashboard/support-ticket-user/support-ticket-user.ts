import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface Ticket {
  id: number;
  subject: string;
  description: string;
  status: 'Open' | 'In Progress' | 'Resolved';
  date: string;
  priority?: 'Low' | 'Medium' | 'High';
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
      description:
        'Unable to login after password reset. Getting authentication error message.',
      status: 'Open',
      date: '2025-08-12',
      priority: 'High',
    },
    {
      id: 102,
      subject: 'Payment failed',
      description:
        'Credit card payment failed during subscription renewal. Transaction was declined.',
      status: 'Resolved',
      date: '2025-08-10',
      priority: 'Medium',
    },
    {
      id: 103,
      subject: 'Workout plan not loading',
      description:
        'The workout plan page stays blank after clicking on my personalized plan.',
      status: 'In Progress',
      date: '2025-08-09',
      priority: 'Medium',
    },
    {
      id: 104,
      subject: 'Profile picture upload',
      description:
        'Cannot upload profile picture. File size seems to be within limits.',
      status: 'Open',
      date: '2025-08-08',
      priority: 'Low',
    },
  ];

  newTicket: Partial<Ticket> = {
    priority: 'Medium',
  };
  selectedTicket: Ticket | null = null;
  isLoading: boolean = false;
  filterStatus: string = 'all';

  get filteredTickets() {
    if (this.filterStatus === 'all') {
      return this.tickets;
    }
    return this.tickets.filter(
      (ticket) =>
        ticket.status.toLowerCase().replace(' ', '') === this.filterStatus
    );
  }

  submitTicket() {
    if (this.newTicket.subject && this.newTicket.description) {
      this.isLoading = true;

      setTimeout(() => {
        const ticket: Ticket = {
          id: Math.floor(Math.random() * 1000) + 200,
          subject: this.newTicket.subject!,
          description: this.newTicket.description!,
          status: 'Open',
          date: new Date().toISOString().split('T')[0],
          priority:
            (this.newTicket.priority as 'Low' | 'Medium' | 'High') || 'Medium',
        };

        this.tickets.unshift(ticket);
        this.newTicket = { priority: 'Medium' };
        this.isLoading = false;

        // Show success animation
        const successMsg = document.querySelector('.success-message');
        if (successMsg) {
          successMsg.classList.add('show');
          setTimeout(() => successMsg.classList.remove('show'), 3000);
        }
      }, 1000);
    }
  }

  openTicket(ticket: Ticket) {
    this.selectedTicket = ticket;
    document.body.style.overflow = 'hidden';
  }

  closeModal() {
    this.selectedTicket = null;
    document.body.style.overflow = 'auto';
  }

  getStatusIcon(status: string): string {
    switch (status) {
      case 'Open':
        return 'fas fa-clock';
      case 'In Progress':
        return 'fas fa-spinner';
      case 'Resolved':
        return 'fas fa-check-circle';
      default:
        return 'fas fa-question-circle';
    }
  }

  getPriorityIcon(priority: string): string {
    switch (priority) {
      case 'High':
        return 'fas fa-exclamation-triangle';
      case 'Medium':
        return 'fas fa-minus-circle';
      case 'Low':
        return 'fas fa-info-circle';
      default:
        return 'fas fa-minus-circle';
    }
  }

  setFilter(status: string) {
    this.filterStatus = status;
  }

  trackByTicketId(index: number, ticket: Ticket): number {
    return ticket.id;
  }
}
