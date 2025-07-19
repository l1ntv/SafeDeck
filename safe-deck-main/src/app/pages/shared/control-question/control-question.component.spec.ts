import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ControlQuestionComponent } from './control-question.component';

describe('ControlQuestionComponent', () => {
  let component: ControlQuestionComponent;
  let fixture: ComponentFixture<ControlQuestionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ControlQuestionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ControlQuestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
