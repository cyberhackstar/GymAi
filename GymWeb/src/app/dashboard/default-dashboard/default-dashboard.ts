import { Component } from '@angular/core';
import { Hero } from '../hero/hero';
import { WhyChooseUs } from '../why-choose-us/why-choose-us';
import { Pricing } from '../../subscription/pricing/pricing';

@Component({
  selector: 'app-default-dashboard',
  imports: [Hero, WhyChooseUs, Pricing],
  templateUrl: './default-dashboard.html',
  styleUrl: './default-dashboard.css',
})
export class DefaultDashboard {}
