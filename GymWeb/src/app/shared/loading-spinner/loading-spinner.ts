import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

interface MotivationQuote {
  text: string;
  author: string;
}

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loading-spinner.html',
  styleUrls: ['./loading-spinner.css'],
})
export class LoadingSpinner implements OnInit, OnDestroy {
  @Input() exercise: 'shoulder-press' | 'bench-press' | 'deadlift' | 'squat' =
    'shoulder-press';
  @Input() message: string = 'Loading your workout...';
  @Input() showReps: boolean = true;
  @Input() showQuotes: boolean = true;
  @Input() size: 'small' | 'medium' | 'large' = 'medium';

  currentRep: number = 0;
  maxReps: number = 12;
  currentQuoteIndex: number = 0;

  private repInterval?: any;
  private quoteInterval?: any;

  motivationQuotes: MotivationQuote[] = [
    {
      text: "The only bad workout is the one that didn't happen.",
      author: 'Unknown',
    },
    {
      text: 'Push yourself because no one else is going to do it for you.',
      author: 'Unknown',
    },
    {
      text: "Your body can do it. It's your mind you need to convince.",
      author: 'Unknown',
    },
    { text: "Don't wish for it. Work for it.", author: 'Unknown' },
    {
      text: 'The pain you feel today will be the strength you feel tomorrow.',
      author: 'Unknown',
    },
    { text: "Success isn't given. It's earned in the gym.", author: 'Unknown' },
    { text: 'Train like a beast, look like a beauty.', author: 'Unknown' },
    { text: 'Sweat is fat crying.', author: 'Unknown' },
    { text: 'Champions train, losers complain.', author: 'Unknown' },
    { text: 'No pain, no gain. Shut up and train.', author: 'Unknown' },
    {
      text: "You don't get what you wish for. You get what you work for.",
      author: 'Unknown',
    },
    { text: 'The gym is not a place for excuses.', author: 'Unknown' },
  ];

  get currentQuote(): MotivationQuote {
    return this.motivationQuotes[this.currentQuoteIndex];
  }

  get exerciseIcon(): string {
    switch (this.exercise) {
      case 'shoulder-press':
        return 'shoulder-press';
      case 'bench-press':
        return 'bench-press';
      case 'deadlift':
        return 'deadlift';
      case 'squat':
        return 'squat';
      default:
        return 'shoulder-press';
    }
  }

  get exerciseName(): string {
    switch (this.exercise) {
      case 'shoulder-press':
        return 'Shoulder Press';
      case 'bench-press':
        return 'Bench Press';
      case 'deadlift':
        return 'Deadlift';
      case 'squat':
        return 'Squat';
      default:
        return 'Shoulder Press';
    }
  }

  ngOnInit(): void {
    this.startRepCounter();
    if (this.showQuotes) {
      this.startQuoteRotation();
    }
  }

  ngOnDestroy(): void {
    if (this.repInterval) {
      clearInterval(this.repInterval);
    }
    if (this.quoteInterval) {
      clearInterval(this.quoteInterval);
    }
  }

  private startRepCounter(): void {
    this.repInterval = setInterval(() => {
      this.currentRep++;
      if (this.currentRep > this.maxReps) {
        this.currentRep = 1;
      }
    }, 2000); // Each rep takes 2 seconds (1 second up, 1 second down)
  }

  private startQuoteRotation(): void {
    this.quoteInterval = setInterval(() => {
      this.currentQuoteIndex =
        (this.currentQuoteIndex + 1) % this.motivationQuotes.length;
    }, 4000); // Change quote every 4 seconds
  }
}
