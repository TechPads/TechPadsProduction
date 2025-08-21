import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Dashbdoard } from './dashbdoard';

describe('Dashbdoard', () => {
  let component: Dashbdoard;
  let fixture: ComponentFixture<Dashbdoard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Dashbdoard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Dashbdoard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
