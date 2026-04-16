export type EventItem = {
  id: number;
  name: string;
  startsAt: string; // ISO datetime
  maxParticipants: number;
  registrationsCount: number;
};

export type CreateEventInput = {
  name: string;
  startsAt: string;
  maxParticipants: number;
};

export type RegisterInput = {
  firstName: string;
  lastName: string;
  idNumber: string;
};
