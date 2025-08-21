import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Inventary } from './inventary';

describe('Inventary', () => {
  let component: Inventary;
  let fixture: ComponentFixture<Inventary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Inventary]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Inventary);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
