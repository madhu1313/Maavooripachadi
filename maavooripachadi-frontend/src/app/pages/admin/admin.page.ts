import { AsyncPipe, DatePipe, DecimalPipe, NgFor, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';

interface AdminStats {
  checkoutStarts: number;
  paymentSuccess: number;
  webhooksOk: number;
}

interface AuditLogEntry {
  id?: number;
  actor: string;
  action: string;
  message: string;
  createdAt?: string;
}

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [NgIf, NgFor, AsyncPipe, DecimalPipe, DatePipe, RouterLink, ReactiveFormsModule],
  templateUrl: './admin.page.html',
  styleUrls: ['./admin.page.css']
})
export class AdminPage {
  private readonly api = inject(ApiService);
  private readonly auth = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  statusMessage = '';
  loadingStats = false;

  stats$: Observable<AdminStats | null> = of(null);
  auditLogs$: Observable<AuditLogEntry[]> = of([]);

  noteForm = this.fb.group({
    message: ['', [Validators.required, Validators.maxLength(240)]],
  });

  constructor() {
    this.loadStats();
    this.loadAuditLogs();
  }

  loadStats(): void {
    this.loadingStats = true;
    this.stats$ = this.api.get<AdminStats>({ key: 'adminStats' }).pipe(
      tap(() => (this.loadingStats = false)),
      catchError(() => {
        this.statusMessage = 'Unable to load dashboard metrics.';
        this.loadingStats = false;
        return of(null);
      })
    );
  }

  loadAuditLogs(): void {
    this.auditLogs$ = this.api.get<AuditLogEntry[]>(this.api.url('adminAuditLogs')).pipe(
      catchError(() => of([]))
    );
  }

  submitNote(): void {
    if (this.noteForm.invalid) {
      return;
    }
    const message = this.noteForm.value.message ?? '';
    this.api.post(this.api.url('adminNote', undefined, { message }), undefined).pipe(
      tap(() => {
        this.statusMessage = 'Note recorded successfully.';
        this.noteForm.reset();
      }),
      catchError(() => {
        this.statusMessage = 'Unable to record note right now.';
        return of(undefined);
      })
    ).subscribe();
  }
}
