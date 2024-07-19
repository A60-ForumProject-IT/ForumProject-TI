INSERT INTO roles (name) VALUES
('user'),
('moderator'),
('admin');

INSERT INTO users (username, password, email, first_name, last_name, role_id, phone_number, is_blocked) VALUES
('johndoe', 'password123', 'johndoe@example.com', 'John', 'Doe', 1, '1234567890', FALSE),
('janedoe', 'password456', 'janedoe@example.com', 'Jane', 'Doe', 2, '0987654321', FALSE),
('adminuser', 'adminpassword', 'admin@example.com', 'Admin', 'User', 3, NULL, FALSE),
('alice', 'alicepassword', 'alice@example.com', 'Alice', 'Wonderland', 1, '1112223333', FALSE),
('bob', 'bobpassword', 'bob@example.com', 'Bob', 'Builder', 1, '4445556666', FALSE),
('charlie', 'charliepassword', 'charlie@example.com', 'Charlie', 'Brown', 2, '7778889999', TRUE),
('david', 'davidpassword', 'david@example.com', 'David', 'Smith', 3, '1212121212', FALSE),
('eva', 'evapassword', 'eva@example.com', 'Eva', 'Green', 1, '1313131313', FALSE);

INSERT INTO posts (user_id, title, content, post_time_created, likes, dislikes) VALUES
(1, 'First Post', 'This is the content of the first post.', NOW(), 10, 2),
(2, 'Second Post', 'This is the content of the second post.', NOW(), 20, 5),
(3, 'Admin Post', 'This is the content of an admin post.', NOW(), 30, 1),
(4, 'Alice\'s Adventures', 'Content of Alice\'s post.', NOW(), 15, 3),
(5, 'Bob the Builder', 'Content of Bob\'s post.', NOW(), 25, 2),
(6, 'Charlie Brown', 'Content of Charlie\'s post.', NOW(), 5, 10),
(7, 'David\'s Diary', 'Content of David\'s post.', NOW(), 8, 1),
(8, 'Eva\'s Experience', 'Content of Eva\'s post.', NOW(), 12, 4);

INSERT INTO comments (creator, content_type, comment_time_created, likes, dislikes) VALUES
(1, 'Comment by John on the first post.', NOW(), 5, 0),
(2, 'Comment by Jane on the second post.', NOW(), 3, 1),
(3, 'Admin comment on the third post.', NOW(), 7, 2),
(4, 'Comment by Alice on her own post.', NOW(), 6, 0),
(5, 'Comment by Bob on his own post.', NOW(), 4, 1),
(6, 'Comment by Charlie on his own post.', NOW(), 2, 3),
(7, 'Comment by David on his own post.', NOW(), 5, 1),
(8, 'Comment by Eva on her own post.', NOW(), 8, 2);

INSERT INTO posts_comments (post_id, comment_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5),
(6, 6),
(7, 7),
(8, 8);