import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SendSecureComponent } from './send-secure.component';

describe('SendSecureComponent', () => {
  let component: SendSecureComponent;
  let fixture: ComponentFixture<SendSecureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SendSecureComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SendSecureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
