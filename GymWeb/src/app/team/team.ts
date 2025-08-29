import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

interface TeamMember {
  id: number;
  name: string;
  position: string;
  image: string;
  bio: string;
  specialties: string[];
  social: {
    instagram: string;
    whatsapp: string;
    email: string;
  };
}

@Component({
  selector: 'app-team',
  imports: [CommonModule, FormsModule],
  templateUrl: './team.html',
  styleUrls: ['./team.css'],
})
export class Team {
  teamMembers: TeamMember[] = [
    {
      id: 1,
      name: 'Alex Carter',
      position: 'Strength & Conditioning Coach',
      image:
        'https://images.pexels.com/photos/1552242/pexels-photo-1552242.jpeg?auto=compress&cs=tinysrgb&w=600&h=600&fit=crop',
      bio: 'Expert in strength training and conditioning with 10+ years of experience transforming clients.',
      specialties: ['Strength Training', 'Bodybuilding', 'Athlete Coaching'],
      social: {
        instagram: 'https://instagram.com/',
        whatsapp:
          'https://wa.me/7742261033?text=Hi%20Alex%2C%20I%27m%20interested%20in%20training',
        email: 'mailto:gymai@neelahouse.cloud',
      },
    },
    {
      id: 2,
      name: 'Sophie Reynolds',
      position: 'Yoga & Flexibility Trainer',
      image:
        'https://images.pexels.com/photos/3768913/pexels-photo-3768913.jpeg?auto=compress&cs=tinysrgb&w=600&h=600&fit=crop',
      bio: 'Specialist in yoga and flexibility routines, making fitness more holistic and mindful.',
      specialties: ['Yoga', 'Flexibility', 'Mindfulness'],
      social: {
        instagram: 'https://instagram.com/',
        whatsapp:
          'https://wa.me/7742261033?text=Hi%20Sophie%2C%20I%27m%20interested%20in%20yoga%20sessions',
        email: 'mailto:gymai@neelahouse.cloud',
      },
    },
    {
      id: 3,
      name: 'Daniel Brooks',
      position: 'CrossFit & Functional Coach',
      image:
        'https://images.pexels.com/photos/4753893/pexels-photo-4753893.jpeg?auto=compress&cs=tinysrgb&w=600&h=600&fit=crop',
      bio: 'Dedicated to CrossFit and functional training, building strength and endurance for everyday life.',
      specialties: ['CrossFit', 'HIIT', 'Endurance Training'],
      social: {
        instagram: 'https://instagram.com/',
        whatsapp:
          'https://wa.me/7742261033?text=Hi%20Daniel%2C%20I%27m%20interested%20in%20CrossFit',
        email: 'mailto:gymai@neelahouse.cloud',
      },
    },
    {
      id: 4,
      name: 'Emily Johnson',
      position: 'Nutrition & Wellness Coach',
      image:
        'https://images.pexels.com/photos/3759658/pexels-photo-3759658.jpeg?auto=compress&cs=tinysrgb&w=600&h=600&fit=crop',
      bio: 'Passionate about balanced diets and lifestyle changes for long-term health improvements.',
      specialties: ['Nutrition', 'Diet Planning', 'Holistic Health'],
      social: {
        instagram: 'https://instagram.com/',
        whatsapp:
          'https://wa.me/7742261033?text=Hi%20Emily%2C%20I%27m%20interested%20in%20nutrition%20plans',
        email: 'mailto:gymai@neelahouse.cloud',
      },
    },
    {
      id: 5,
      name: 'Michael Smith',
      position: 'Body Transformation Specialist',
      image:
        'https://images.pexels.com/photos/3757375/pexels-photo-3757375.jpeg?auto=compress&cs=tinysrgb&w=600&h=600&fit=crop',
      bio: 'Known for guiding clients through fat loss and muscle gain transformations effectively.',
      specialties: ['Bodybuilding', 'Fat Loss', 'Muscle Gain'],
      social: {
        instagram: 'https://instagram.com/',
        whatsapp:
          'https://wa.me/7742261033?text=Hi%20Michael%2C%20I%27m%20interested%20in%20body%20transformation',
        email: 'mailto:gymai@neelahouse.cloud',
      },
    },
    {
      id: 6,
      name: 'Olivia Brown',
      position: 'Pilates & Core Strength Coach',
      image:
        'https://images.pexels.com/photos/1701194/pexels-photo-1701194.jpeg?auto=compress&cs=tinysrgb&w=600&h=600&fit=crop',
      bio: 'Helping clients build strong cores and improve posture through personalized Pilates sessions.',
      specialties: ['Pilates', 'Core Training', 'Posture Correction'],
      social: {
        instagram: 'https://instagram.com/',
        whatsapp:
          'https://wa.me/7742261033?text=Hi%20Olivia%2C%20I%27m%20interested%20in%20Pilates',
        email: 'mailto:gymai@neelahouse.cloud',
      },
    },
  ];

  getIconForSocial(platform: string): string {
    const icons: { [key: string]: string } = {
      instagram: 'camera_alt',
      whatsapp: 'chat',
      email: 'email',
    };
    return icons[platform] || 'link';
  }

  handleTeamMemberClick(member: TeamMember): void {
    console.log(`Clicked on ${member.name} - ${member.position}`);
  }

  handleViewPositionsClick(): void {
    console.log('View Open Positions clicked');
    window.open('#careers', '_blank');
  }

  handleSendResumeClick(): void {
    console.log('Send Resume clicked');
    window.location.href =
      'mailto:gymai@neelahouse.cloud?subject=Resume Submission';
  }
}
