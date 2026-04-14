--liquibase formatted sql

--changeset dev:001-create-events
CREATE TABLE events (
  id                    BIGSERIAL        PRIMARY KEY,
  name                  VARCHAR(255)     NOT NULL,
  starts_at             TIMESTAMPTZ      NOT NULL,
  max_participants      INT              NOT NULL CHECK (max_participants > 0),
  created_at            TIMESTAMPTZ      NOT NULL
);
--rollback DROP TABLE events;

--changeset dev:002-create-registrations
CREATE TABLE registrations (
  id               BIGSERIAL                        PRIMARY KEY,
  event_id         BIGINT                           NOT NULL REFERENCES events(id),
  first_name       VARCHAR(100)                     NOT NULL,
  last_name        VARCHAR(100)                     NOT NULL,
  id_number        VARCHAR(64)                      NOT NULL,
  created_at       TIMESTAMPTZ                      NOT NULL,
  CONSTRAINT       uq_registrations_event_idnum     UNIQUE (event_id, id_number)
);

CREATE INDEX ix_registrations_event ON registrations(event_id);
--rollback DROP TABLE registrations;
