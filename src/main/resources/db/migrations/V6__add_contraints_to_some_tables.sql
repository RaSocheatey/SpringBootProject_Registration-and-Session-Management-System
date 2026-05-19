-- 2. Alter Session table to add optional room_id
ALTER TABLE sessions
ADD COLUMN room_id BIGINT NULL,
ADD CONSTRAINT fk_session_room
    FOREIGN KEY (room_id)
    REFERENCES rooms(room_id);

-- ensure conference dates are valid
ALTER TABLE conferences
ADD CONSTRAINT chk_conference_date
    CHECK (end_date >= start_date);

-- ensure unique registration per user per session
ALTER TABLE registrations
ADD CONSTRAINT uq_user_session
    UNIQUE (user_id, session_id);