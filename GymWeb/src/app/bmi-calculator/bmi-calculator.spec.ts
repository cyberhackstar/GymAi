import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BmiCalculator } from './bmi-calculator';

describe('BmiCalculator', () => {
  let component: BmiCalculator;
  let fixture: ComponentFixture<BmiCalculator>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BmiCalculator]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BmiCalculator);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
