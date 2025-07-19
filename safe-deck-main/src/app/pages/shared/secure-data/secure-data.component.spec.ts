import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SecureDataComponent } from './secure-data.component';

describe('SecureDataComponent', () => {
  let component: SecureDataComponent;
  let fixture: ComponentFixture<SecureDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SecureDataComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SecureDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
