import { CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';

interface Star {
  state: 'full' | 'half' | 'empty';
}

@Component({
  selector: 'app-stars',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, NgClass],
  templateUrl: './rating-stars.component.html',
  styleUrls: ['./rating-stars.component.css']
})
export class RatingStarsComponent {
  private readonly maxStars = 5;

  private _value = 0;
  private _reviews?: number;

  stars: Star[] = Array.from({ length: this.maxStars }, () => ({ state: 'empty' as const }));

  @Input()
  set value(rating: number) {
    this._value = Math.max(0, Math.min(this.maxStars, rating ?? 0));
    this.computeStars();
  }
  get value(): number {
    return this._value;
  }

  @Input()
  set reviews(count: number | undefined) {
    this._reviews = count;
  }
  get reviews(): number | undefined {
    return this._reviews;
  }

  private computeStars(): void {
    const fullStars = Math.floor(this._value);
    const hasHalf = this._value - fullStars >= 0.25 && this._value - fullStars < 0.75;
    const stars: Star[] = [];

    for (let i = 1; i <= this.maxStars; i++) {
      if (i <= fullStars) {
        stars.push({ state: 'full' });
      } else if (hasHalf && i === fullStars + 1) {
        stars.push({ state: 'half' });
      } else {
        stars.push({ state: 'empty' });
      }
    }

    this.stars = stars;
  }
}
