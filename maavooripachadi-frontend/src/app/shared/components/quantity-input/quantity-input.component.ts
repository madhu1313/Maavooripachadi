import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-qty',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quantity-input.component.html',
  styleUrls: ['./quantity-input.component.css']
})
export class QuantityInputComponent {
  @Input() value = 1;
  @Input() min = 1;
  @Input() max = 12;
  @Input() compact = false;

  @Output() valueChange = new EventEmitter<number>();

  inc(): void {
    const next = Math.min(this.max, this.value + 1);
    this.valueChange.emit(next);
  }

  dec(): void {
    const next = Math.max(this.min, this.value - 1);
    this.valueChange.emit(next);
  }
}
