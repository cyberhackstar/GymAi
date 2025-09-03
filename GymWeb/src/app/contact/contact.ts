import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

interface ContactInfo {
  icon: string;
  title: string;
  value: string;
  link?: string;
  linkText?: string;
}

interface SocialLink {
  icon: string;
  name: string;
  url: string;
  color: string;
}

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contact.html',
  styleUrls: ['./contact.css'],
})
export class Contact implements OnInit {
  isFormSubmitting = false;
  formSubmitted = false;

  contactInfo: ContactInfo[] = [
    {
      icon: 'fas fa-map-marker-alt',
      title: 'Visit Us',
      value: '84 Thane Tonk Rd, Jaipur',
      link: 'https://www.google.com/maps/place/Sanganer+Thana+Flyover,+Jaipur,+Rajasthan+302029',
      linkText: 'RJ 302029, India',
    },
    {
      icon: 'fas fa-phone-alt',
      title: 'Call Us',
      value: '+91 77422 61033',
      link: 'tel:+917742261033',
      linkText: 'Available 24/7',
    },
    {
      icon: 'fas fa-envelope',
      title: 'Email Us',
      value: 'gymai@neelahouse.cloud',
      link: 'mailto:gymai@neelahouse.cloud',
      linkText: 'We reply within 24 hours',
    },
    {
      icon: 'fas fa-clock',
      title: 'Business Hours',
      value: 'Mon - Sat: 6:00 AM - 10:00 PM',
      linkText: 'Sunday: 7:00 AM - 9:00 PM',
    },
  ];

  socialLinks: SocialLink[] = [
    {
      icon: 'fab fa-facebook-f',
      name: 'Facebook',
      url: '#',
      color: '#3b5998',
    },
    {
      icon: 'fab fa-twitter',
      name: 'Twitter',
      url: '#',
      color: '#1da1f2',
    },
    {
      icon: 'fab fa-instagram',
      name: 'Instagram',
      url: '#',
      color: '#e4405f',
    },
    {
      icon: 'fab fa-linkedin-in',
      name: 'LinkedIn',
      url: '#',
      color: '#0077b5',
    },
    {
      icon: 'fab fa-youtube',
      name: 'YouTube',
      url: '#',
      color: '#ff0000',
    },
  ];

  stats = [
    { value: '5K+', label: 'Happy Members' },
    { value: '50+', label: 'Trainers' },
    { value: '24/7', label: 'Support' },
    { value: '99%', label: 'Satisfaction' },
  ];

  ngOnInit(): void {
    this.animateStats();
  }

  onSubmit(form: NgForm): void {
    if (form.valid) {
      this.isFormSubmitting = true;

      // Simulate API call
      setTimeout(() => {
        this.isFormSubmitting = false;
        this.formSubmitted = true;
        form.resetForm();

        // Reset success message after 5 seconds
        setTimeout(() => {
          this.formSubmitted = false;
        }, 5000);
      }, 2000);
    }
  }

  private animateStats(): void {
    // Animate stats when component loads
    setTimeout(() => {
      const statElements = document.querySelectorAll('.stat-value');
      statElements.forEach((element, index) => {
        setTimeout(() => {
          element.classList.add('animate');
        }, index * 200);
      });
    }, 500);
  }

  scrollToForm(): void {
    const formElement = document.getElementById('contact-form');
    if (formElement) {
      formElement.scrollIntoView({ behavior: 'smooth' });
    }
  }
}
