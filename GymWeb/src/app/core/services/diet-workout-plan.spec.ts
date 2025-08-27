import { TestBed } from '@angular/core/testing';

import { DietWorkoutPlan } from './diet-workout-plan';

describe('DietWorkoutPlan', () => {
  let service: DietWorkoutPlan;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DietWorkoutPlan);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
