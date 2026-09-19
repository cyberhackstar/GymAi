import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { DietPlanComponent } from './diet-plan-component';

describe('DietPlanComponent', () => {
  let component: DietPlanComponent;
  let fixture: ComponentFixture<DietPlanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter([])],
      imports: [DietPlanComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DietPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
