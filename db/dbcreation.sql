create table avatars
(
    id         int auto_increment
        primary key,
    avatar_url varchar(255) not null
);

create table roles
(
    id   int auto_increment
        primary key,
    name varchar(20) not null
);

create table tags
(
    id   int auto_increment
        primary key,
    name varchar(50) not null
);

create table users
(
    id         int auto_increment
        primary key,
    username   varchar(50)          not null,
    password   varchar(255)         not null,
    email      varchar(100)         not null,
    first_name varchar(50)          null,
    last_name  varchar(50)          null,
    role_id    int                  null,
    is_blocked tinyint(1) default 0 null,
    constraint email
        unique (email),
    constraint username
        unique (username),
    constraint users_ibfk_1
        foreign key (role_id) references roles (id)
);

create table phone_numbers
(
    id           int auto_increment
        primary key,
    phone_number varchar(20) not null,
    user_id      int         not null,
    constraint phone_number_unique
        unique (phone_number),
    constraint phone_numbers_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade
);

create index user_id
    on phone_numbers (user_id);

create table posts
(
    id                int auto_increment
        primary key,
    user_id           int                                   null,
    title             varchar(64)                           not null,
    content           text                                  not null,
    post_time_created timestamp default current_timestamp() null,
    likes             int       default 0                   null,
    dislikes          int       default 0                   null,
    constraint posts_ibfk_1
        foreign key (user_id) references users (id)
);

create table comments
(
    id                   int auto_increment
        primary key,
    creator              int                                   null,
    content_type         varchar(500)                          not null,
    comment_time_created timestamp default current_timestamp() null,
    post_id              int                                   null,
    constraint comments_ibfk_1
        foreign key (creator) references users (id),
    constraint comments_ibfk_2
        foreign key (post_id) references posts (id)
);

create index creator
    on comments (creator);

create index post_id
    on comments (post_id);

create index user_id
    on posts (user_id);

create table posts_users_dislikes
(
    post_id int not null,
    user_id int not null,
    primary key (post_id, user_id),
    constraint posts_users_dislikes_ibfk_1
        foreign key (post_id) references posts (id)
            on delete cascade,
    constraint posts_users_dislikes_ibfk_2
        foreign key (user_id) references users (id)
);

create index user_id
    on posts_users_dislikes (user_id);

create table posts_users_likes
(
    post_id int not null,
    user_id int not null,
    primary key (post_id, user_id),
    constraint posts_users_likes_ibfk_1
        foreign key (post_id) references posts (id)
            on delete cascade,
    constraint posts_users_likes_ibfk_2
        foreign key (user_id) references users (id)
);

create index user_id
    on posts_users_likes (user_id);

create table tags_posts
(
    tag_id  int not null,
    post_id int not null,
    primary key (tag_id, post_id),
    constraint tags_posts_ibfk_1
        foreign key (tag_id) references tags (id),
    constraint tags_posts_ibfk_2
        foreign key (post_id) references posts (id)
);

create index post_id
    on tags_posts (post_id);

create index role_id
    on users (role_id);

create table users_avatars
(
    user_id   int not null,
    avatar_id int not null,
    primary key (user_id, avatar_id),
    constraint users_avatars_ibfk_1
        foreign key (user_id) references users (id)
            on delete cascade,
    constraint users_avatars_ibfk_2
        foreign key (avatar_id) references avatars (id)
            on delete cascade
);

create index avatar_id
    on users_avatars (avatar_id);

