import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { DefaultDashboard } from './default-dashboard';

describe('DefaultDashboard', () => {
  let component: DefaultDashboard;
  let fixture: ComponentFixture<DefaultDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter([])],
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
