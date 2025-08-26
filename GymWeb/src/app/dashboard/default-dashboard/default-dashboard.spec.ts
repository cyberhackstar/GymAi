import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DefaultDashboard } from './default-dashboard';

describe('DefaultDashboard', () => {
  let component: DefaultDashboard;
  let fixture: ComponentFixture<DefaultDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DefaultDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DefaultDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
