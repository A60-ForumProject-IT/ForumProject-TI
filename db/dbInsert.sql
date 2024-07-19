INSERT INTO roles (name) VALUES
                             ('user'),
                             ('moderator'),
                             ('admin');


INSERT INTO users (username, password, email, first_name, last_name, role_id, phone_number, is_blocked) VALUES
                                                                                                            ('gamer1', 'password1', 'gamer1@example.com', 'John', 'Doe', 1, NULL, FALSE),
                                                                                                            ('gamer2', 'password2', 'gamer2@example.com', 'Jane', 'Smith', 2, '0987654321', FALSE),
                                                                                                            ('gamer3', 'password3', 'gamer3@example.com', 'Alice', 'Johnson', 1, NULL, FALSE),
                                                                                                            ('gamer4', 'password4', 'gamer4@example.com', 'Bob', 'Brown', 3, '2222222222', FALSE),
                                                                                                            ('gamer5', 'password5', 'gamer5@example.com', 'Charlie', 'Davis', 2, '3333333333', FALSE),
                                                                                                            ('gamer6', 'password6', 'gamer6@example.com', 'David', 'Wilson', 1, NULL, FALSE),
                                                                                                            ('gamer7', 'password7', 'gamer7@example.com', 'Eve', 'Taylor', 2, '5555555555', FALSE),
                                                                                                            ('gamer8', 'password8', 'gamer8@example.com', 'Frank', 'Anderson', 1, NULL, TRUE),
                                                                                                            ('gamer9', 'password9', 'gamer9@example.com', 'Grace', 'Thomas', 3, '7777777777', FALSE),
                                                                                                            ('gamer10', 'password10', 'gamer10@example.com', 'Hank', 'Moore', 1, NULL, FALSE);

INSERT INTO posts (user_id, title, content, post_time_created, likes, dislikes) VALUES
                                                                                    (1, 'Best RPG Games of 2024', 'Let\'s discuss the best RPG games released this year...', '2024-01-01 10:00:00', 10, 2),
                                                                                    (2, 'FPS Games Recommendations', 'Looking for some good first-person shooters to play...', '2024-02-15 12:30:00', 8, 1),
                                                                                    (3, 'MMORPGs Worth Playing', 'Which MMORPGs do you think are worth playing in 2024?', '2024-03-10 14:45:00', 15, 3),
                                                                                    (4, 'Gaming Laptops vs Desktops', 'What are the pros and cons of gaming laptops vs desktops?', '2024-04-22 09:20:00', 5, 0),
                                                                                    (5, 'Top Indie Games', 'Share your favorite indie games!', '2024-05-05 16:00:00', 12, 4),
                                                                                    (6, 'Game Development Tips', 'Tips and resources for aspiring game developers.', '2024-06-18 18:10:00', 20, 5),
                                                                                    (7, 'VR Gaming Experience', 'How is your experience with VR games?', '2024-07-11 11:00:00', 7, 1),
                                                                                    (8, 'Upcoming Game Releases', 'What upcoming game releases are you most excited about?', '2024-08-23 20:30:00', 9, 2),
                                                                                    (9, 'Best Gaming Headsets', 'Recommendations for the best gaming headsets.', '2024-09-14 13:25:00', 6, 3),
                                                                                    (10, 'Esports Tournaments', 'Upcoming esports tournaments and predictions.', '2024-10-29 21:40:00', 14, 6);

INSERT INTO comments (creator, content_type, comment_time_created, post_id) VALUES
                                                                                (1, 'I totally agree with your list of RPGs!', '2024-01-02 11:00:00', 1),
                                                                                (2, 'You should try the new shooter released last month.', '2024-02-16 13:30:00', 2),
                                                                                (3, 'I think MMORPG X is the best right now.', '2024-03-11 15:45:00', 3),
                                                                                (4, 'Laptops are more portable, but desktops have better performance.', '2024-04-23 10:20:00', 4),
                                                                                (5, 'Check out Indie Game Y, it\'s amazing!', '2024-05-06 17:00:00', 5),
                                                                                (6, 'For game development, focus on learning Unity or Unreal Engine.', '2024-06-19 19:10:00', 6),
                                                                                (7, 'VR games are getting better every year.', '2024-07-12 12:00:00', 7),
                                                                                (8, 'I can\'t wait for the release of Game Z.', '2024-08-24 21:30:00', 8),
                                                                                (9, 'I recommend Headset A for the best sound quality.', '2024-09-15 14:25:00', 9),
                                                                                (10, 'Team Alpha is going to win the next tournament!', '2024-10-30 22:40:00', 10);