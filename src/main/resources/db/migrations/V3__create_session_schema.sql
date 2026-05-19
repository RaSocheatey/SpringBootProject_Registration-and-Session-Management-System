CREATE TABLE sessions (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    session_time DATETIME NOT NULL,
    proposal_abstract TEXT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL,
    conference_id BIGINT NOT NULL,
    chair_id BIGINT NULL,
    FOREIGN KEY (conference_id) REFERENCES conferences(conference_id),
    FOREIGN KEY (chair_id) REFERENCES users(user_id)
);