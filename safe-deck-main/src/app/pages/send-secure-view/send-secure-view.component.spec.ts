import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SendSecureViewComponent } from './send-secure-view.component';

describe('SendSecureViewComponent', () => {
  let component: SendSecureViewComponent;
  let fixture: ComponentFixture<SendSecureViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SendSecureViewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SendSecureViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
