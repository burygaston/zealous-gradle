-- Insert demo user (username: demo, password: password)
-- BCrypt hash for 'password' with strength 10
INSERT INTO users (username, email, password, first_name, last_name, enabled, created_at, updated_at)
VALUES ('demo', 'demo@taskmanager.com', '$2a$10$8F5KwJxLzQqE.8E3h3X0.eLVqJ5ZLqJ5ZLqJ5ZLqJ5ZLqJ5ZLqJ5a', 'Demo', 'User', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert some demo labels for the demo user
INSERT INTO labels (name, color, icon, user_id, created_at)
VALUES
    ('Urgent', '#EF4444', '🔥', 1, CURRENT_TIMESTAMP),
    ('Work', '#3B82F6', '💼', 1, CURRENT_TIMESTAMP),
    ('Personal', '#10B981', '👤', 1, CURRENT_TIMESTAMP),
    ('Bug', '#F59E0B', '🐛', 1, CURRENT_TIMESTAMP),
    ('Feature', '#8B5CF6', '✨', 1, CURRENT_TIMESTAMP);

-- Insert some demo work items
INSERT INTO work_items (title, description, status, deadline, priority, user_id, created_at, updated_at)
VALUES
    ('Complete project proposal', 'Write and submit the Q4 project proposal with budget estimations', 'TODO', DATEADD('DAY', 2, CURRENT_TIMESTAMP), 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Review pull requests', 'Review and merge pending pull requests from the team', 'IN_PROGRESS', DATEADD('DAY', 1, CURRENT_TIMESTAMP), 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Update documentation', 'Update API documentation with new endpoints', 'TODO', DATEADD('DAY', 5, CURRENT_TIMESTAMP), 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Fix login bug', 'Investigate and fix the OAuth login timeout issue', 'TODO', DATEADD('DAY', -1, CURRENT_TIMESTAMP), 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Team meeting preparation', 'Prepare slides and agenda for weekly team sync', 'COMPLETE', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Code refactoring', 'Refactor user authentication module for better maintainability', 'IN_PROGRESS', DATEADD('DAY', 7, CURRENT_TIMESTAMP), 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Database migration', 'Plan and execute database migration to PostgreSQL', 'TODO', DATEADD('DAY', 14, CURRENT_TIMESTAMP), 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
