import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export type TicketStatus = 'OPEN' | 'PENDING_CUSTOMER' | 'PENDING_AGENT' | 'RESOLVED' | 'CLOSED';
export type TicketPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
export type TicketChannel = 'WEB' | 'EMAIL' | 'PHONE' | 'WHATSAPP';
export type MessageVisibility = 'PUBLIC' | 'INTERNAL';

export interface SupportTicket {
  id?: number;
  ticketNo: string;
  subject: string;
  description?: string;
  status: TicketStatus;
  priority: TicketPriority;
  channel: TicketChannel;
  requesterEmail?: string;
  requesterName?: string;
  assignee?: string;
  firstResponseDueAt?: string;
  resolveDueAt?: string;
  closedAt?: string;
  tags?: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface TicketMessage {
  id?: number;
  author: string;
  body: string;
  visibility: MessageVisibility;
  createdAt?: string;
  updatedAt?: string;
}

export interface CsatSurvey {
  id?: number;
  rating: number;
  comment?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateTicketPayload {
  subject: string;
  description: string;
  requesterEmail?: string;
  requesterName?: string;
  priority?: TicketPriority;
  channel?: TicketChannel;
}

export interface AddMessagePayload {
  ticketNo: string;
  author: string;
  body: string;
  visibility?: MessageVisibility;
}

export interface SubmitCsatPayload {
  ticketNo: string;
  rating: number;
  comment?: string;
}

@Injectable({ providedIn: 'root' })
export class SupportService {
  constructor(private readonly api: ApiService) {}

  openTicket(payload: CreateTicketPayload): Observable<SupportTicket> {
    return this.api.post<SupportTicket>({ key: 'supportTickets', body: payload });
  }

  addMessage(payload: AddMessagePayload): Observable<TicketMessage> {
    return this.api.post<TicketMessage>({ key: 'supportMessage', body: payload });
  }

  submitCsat(payload: SubmitCsatPayload): Observable<CsatSurvey> {
    return this.api.post<CsatSurvey>({ key: 'supportCsat', body: payload });
  }
}
