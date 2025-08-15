import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupportTicketAdmin } from './support-ticket-admin';

describe('SupportTicketAdmin', () => {
  let component: SupportTicketAdmin;
  let fixture: ComponentFixture<SupportTicketAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SupportTicketAdmin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupportTicketAdmin);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
