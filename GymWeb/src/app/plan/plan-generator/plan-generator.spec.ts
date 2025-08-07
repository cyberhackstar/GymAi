import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanGenerator } from './plan-generator';

describe('PlanGenerator', () => {
  let component: PlanGenerator;
  let fixture: ComponentFixture<PlanGenerator>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanGenerator]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlanGenerator);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
