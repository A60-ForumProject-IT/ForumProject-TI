CREATE DATABASE IF NOT EXISTS forum;
USE forum;

CREATE TABLE roles (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(20) NOT NULL
);

CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       first_name VARCHAR(50),
                       last_name VARCHAR(50),
                       role_id INT,
                       phone_number VARCHAR(20) UNIQUE,
                       is_blocked BOOLEAN DEFAULT FALSE,
                       FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE posts (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       user_id INT,
                       title VARCHAR(64) NOT NULL,
                       content TEXT NOT NULL,
                       post_time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       likes INT DEFAULT 0,
                       dislikes INT DEFAULT 0,
                       FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE comments (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          creator INT,
                          content_type VARCHAR(500) NOT NULL,
                          comment_time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          post_id INT,
                          FOREIGN KEY (creator) REFERENCES users(id),
                          FOREIGN KEY (post_id) REFERENCES posts(id)
);

CREATE TABLE tags (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(50) NOT NULL
);

CREATE TABLE tags_posts (
                            tag_id INT,
                            post_id INT,
                            PRIMARY KEY (tag_id, post_id),
                            FOREIGN KEY (tag_id) REFERENCES tags(id),
                            FOREIGN KEY (post_id) REFERENCES posts(id)
);

CREATE TABLE posts_users_likes (
                                   post_id INT,
                                   user_id INT,
                                   PRIMARY KEY (post_id, user_id),
                                   FOREIGN KEY (post_id) REFERENCES posts(id),
                                   FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE posts_users_dislikes (
                                      post_id INT,
                                      user_id INT,
                                      PRIMARY KEY (post_id, user_id),
                                      FOREIGN KEY (post_id) REFERENCES posts(id),
                                      FOREIGN KEY (user_id) REFERENCES users(id)
);