-- Task Manager Database Schema

-- Drop tables if they exist
DROP TABLE IF EXISTS work_item_labels;
DROP TABLE IF EXISTS labels;
DROP TABLE IF EXISTS work_items;
DROP TABLE IF EXISTS users;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Work items table
CREATE TABLE work_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    deadline TIMESTAMP NOT NULL,
    priority INT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_status (user_id, status),
    INDEX idx_deadline (deadline),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Labels table
CREATE TABLE labels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7) NOT NULL,
    icon VARCHAR(10),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    UNIQUE KEY unique_user_label (user_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Work item labels junction table
CREATE TABLE work_item_labels (
    work_item_id BIGINT NOT NULL,
    label_id BIGINT NOT NULL,
    PRIMARY KEY (work_item_id, label_id),
    FOREIGN KEY (work_item_id) REFERENCES work_items(id) ON DELETE CASCADE,
    FOREIGN KEY (label_id) REFERENCES labels(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert demo user (username: demo, password: password)
-- BCrypt hash for 'password' with strength 10
INSERT INTO users (username, email, password, first_name, last_name, enabled)
VALUES ('demo', 'demo@taskmanager.com', '$2a$10$8F5KwJxLzQqE.8E3h3X0.eLVqJ5ZLqJ5ZLqJ5ZLqJ5ZLqJ5ZLqJ5a', 'Demo', 'User', TRUE);

-- Insert some demo labels for the demo user
INSERT INTO labels (name, color, icon, user_id)
VALUES
    ('Urgent', '#EF4444', '🔥', 1),
    ('Work', '#3B82F6', '💼', 1),
    ('Personal', '#10B981', '👤', 1),
    ('Bug', '#F59E0B', '🐛', 1),
    ('Feature', '#8B5CF6', '✨', 1);

-- Insert some demo work items
INSERT INTO work_items (title, description, status, deadline, priority, user_id)
VALUES
    ('Complete project proposal', 'Write and submit the Q4 project proposal with budget estimations', 'TODO', DATE_ADD(NOW(), INTERVAL 2 DAY), 1, 1),
    ('Review pull requests', 'Review and merge pending pull requests from the team', 'IN_PROGRESS', DATE_ADD(NOW(), INTERVAL 1 DAY), 2, 1),
    ('Update documentation', 'Update API documentation with new endpoints', 'TODO', DATE_ADD(NOW(), INTERVAL 5 DAY), 0, 1),
    ('Fix login bug', 'Investigate and fix the OAuth login timeout issue', 'TODO', DATE_ADD(NOW(), INTERVAL -1 DAY), 3, 1),
    ('Team meeting preparation', 'Prepare slides and agenda for weekly team sync', 'COMPLETE', DATE_ADD(NOW(), INTERVAL -2 DAY), 1, 1),
    ('Code refactoring', 'Refactor user authentication module for better maintainability', 'IN_PROGRESS', DATE_ADD(NOW(), INTERVAL 7 DAY), 1, 1),
    ('Database migration', 'Plan and execute database migration to PostgreSQL', 'TODO', DATE_ADD(NOW(), INTERVAL 14 DAY), 2, 1);

-- Associate labels with work items
INSERT INTO work_item_labels (work_item_id, label_id)
VALUES
    (1, 2), -- Complete project proposal - Work
    (1, 1), -- Complete project proposal - Urgent
    (2, 2), -- Review pull requests - Work
    (3, 2), -- Update documentation - Work
    (4, 4), -- Fix login bug - Bug
    (4, 1), -- Fix login bug - Urgent
    (5, 2), -- Team meeting preparation - Work
    (6, 5), -- Code refactoring - Feature
    (7, 2), -- Database migration - Work
    (7, 1); -- Database migration - Urgent