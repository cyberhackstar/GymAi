import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupportTicketUser } from './support-ticket-user';

describe('SupportTicketUser', () => {
  let component: SupportTicketUser;
  let fixture: ComponentFixture<SupportTicketUser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupportTicketUser]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupportTicketUser);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
