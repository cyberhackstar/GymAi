import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserSummary } from './user-summary';

describe('UserSummary', () => {
  let component: UserSummary;
  let fixture: ComponentFixture<UserSummary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserSummary]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserSummary);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
