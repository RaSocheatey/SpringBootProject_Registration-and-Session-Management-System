CREATE TABLE registrations (
    registration_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    registration_date DATETIME(6) NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (session_id) REFERENCES sessions(session_id)
);