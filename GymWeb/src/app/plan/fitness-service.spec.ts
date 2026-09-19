import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { FitnessService } from './fitness-service';

describe('FitnessService', () => {
  let service: FitnessService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter([])],
    });
    service = TestBed.inject(FitnessService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
