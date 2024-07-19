CREATE DATABASE forum;
USE forum;

CREATE TABLE roles (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       name ENUM('user', 'moderator', 'admin') NOT NULL
);

CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       role_id INT,
                       phone_number VARCHAR(20) UNIQUE,
                       is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
                       FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE posts (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       user_id INT,
                       title VARCHAR(64) NOT NULL,
                       content TEXT NOT NULL,
                       post_time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       likes INT DEFAULT 0,
                       dislikes INT DEFAULT 0,
                       FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE comments (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          creator INT,
                          content_type VARCHAR(500) NOT NULL,
                          comment_time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          likes INT DEFAULT 0,
                          dislikes INT DEFAULT 0,
                          FOREIGN KEY (creator) REFERENCES users(id)
);

CREATE TABLE tags (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL
);

CREATE TABLE posts_comments (
                                post_id INT,
                                comment_id INT,
                                PRIMARY KEY (post_id, comment_id),
                                FOREIGN KEY (post_id) REFERENCES posts(id),
                                FOREIGN KEY (comment_id) REFERENCES comments(id)
);

CREATE TABLE tags_posts (
                            tag_id INT,
                            post_id INT,
                            PRIMARY KEY (tag_id, post_id),
                            FOREIGN KEY (tag_id) REFERENCES tags(id),
                            FOREIGN KEY (post_id) REFERENCES posts(id)
);

CREATE TABLE replies (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         user_id INT,
                         content TEXT NOT NULL,
                         reply_time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         likes INT DEFAULT 0,
                         dislikes INT DEFAULT 0,
                         FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE replies_comments (
                                  comment_id INT,
                                  reply_id INT,
                                  PRIMARY KEY (comment_id, reply_id),
                                  FOREIGN KEY (comment_id) REFERENCES comments(id),
                                  FOREIGN KEY (reply_id) REFERENCES replies(id)
);

