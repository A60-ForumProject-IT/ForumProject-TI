package com.project.helpers;

import com.project.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.time.LocalDateTime;

public class TestHelpers {
    public static User createMockAdminUser() {
        Set<Role> roles = new TreeSet<>();
        roles.add(createMockRoleAdmin());
        roles.add(createMockRoleStaffMember());
        roles.add(createMockRoleMember());

        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("Mock_Admin_User@gmail.com");
        mockUser.setPassword("MnOpQrStUvWxYzAbCdEf");
        mockUser.setFirstName("Mock Admin");
        mockUser.setLastName("User");
        mockUser.setUsername("Mock_Admin_User");
        mockUser.setBlocked(false);
        mockUser.setRole(createAdminRole());
        return mockUser;
    }

    public static Role createAdminRole() {
        Role mockRole = new Role();
        mockRole.setRoleId(3);
        mockRole.setName("admin");
        return mockRole;
    }

    public static Role createUserRole() {
        Role mockRole = new Role();
        mockRole.setRoleId(1);
        mockRole.setName("user");
        return mockRole;
    }

    public static User createMockNoAdminUser() {
        Set<Role> roles = new TreeSet<>(Set.of(createMockRoleMember()));
        User mockUser = new User();
        mockUser.setId(2);
        mockUser.setEmail("Mock_NoAdmin_User@gmail.com");
        mockUser.setPassword("MnOpQrStUvWxYzAbCdEf");
        mockUser.setFirstName("Mock NoAdmin");
        mockUser.setLastName("User");
        mockUser.setUsername("Mock_NoAdmin_User");
        mockUser.setBlocked(false);
        mockUser.setRole(createUserRole());
        return mockUser;
    }

    public static Tag createMockTag() {
        Tag mockTag = new Tag();
        mockTag.setId(33);
        mockTag.setTag("#luckyNumber");
        mockTag.setPostTags(null);
        return mockTag;
    }

    public static Post createMockPost1() {
        List<Comment> relatedComments = new ArrayList<>();
        Set<Tag> relatedTags = new TreeSet<>();
        relatedTags.add(createMockTag());

        Set<User> usersWhoLikedThePost = new TreeSet<>();
        usersWhoLikedThePost.add(createMockAdminUser());

        Set<User> usersWhoDislikedThePost = new TreeSet<>();
        User userWhoDisliked = createMockNoAdminUser();
        userWhoDisliked.setId(3);
        usersWhoDislikedThePost.add(userWhoDisliked);

        Post mockPost = new Post();

        mockPost.setPostId(1);
        mockPost.setLikes(1);
        mockPost.setDislikes(1);
        mockPost.setTitle("Mock post title.");
        mockPost.setContent("Mock post random content.");
        mockPost.setCreatedOn(LocalDateTime.of(2024, 1, 28, 0, 0, 0));
        mockPost.setPostedBy(createMockNoAdminUser());
        mockPost.setComments(relatedComments);
        mockPost.setPostTags(relatedTags);
        mockPost.setUsersWhoLikedPost(usersWhoLikedThePost);
        mockPost.setUsersWhoDislikedPost(usersWhoDislikedThePost);

        return mockPost;
    }

    public static Post createMockPost2() {
        List<Comment> relatedComments = new ArrayList<>();
        Set<Tag> relatedTags = new TreeSet<>();
        relatedTags.add(createMockTag());

        Set<User> usersWhoLikedThePost = new TreeSet<>();
        usersWhoLikedThePost.add(createMockAdminUser());

        Set<User> usersWhoDislikedThePost = new TreeSet<>();
        User userWhoDisliked = createMockNoAdminUser();
        userWhoDisliked.setId(3);
        usersWhoDislikedThePost.add(userWhoDisliked);

        Post mockPost = new Post();

        mockPost.setPostId(1);
        mockPost.setLikes(1);
        mockPost.setDislikes(1);
        mockPost.setTitle("Mock post2 title.");
        mockPost.setContent("Mock post2 random content.");
        mockPost.setCreatedOn(LocalDateTime.of(2024, 1, 28, 0, 0, 0));
        mockPost.setPostedBy(createMockNoAdminUser());
        mockPost.setComments(relatedComments);
        mockPost.setPostTags(relatedTags);
        mockPost.setUsersWhoLikedPost(usersWhoLikedThePost);
        mockPost.setUsersWhoDislikedPost(usersWhoDislikedThePost);

        return mockPost;
    }

    public static Role createMockRoleAdmin() {
        Role mockRole = new Role();
        mockRole.setRoleId(1);
        mockRole.setName("ADMIN");
        return mockRole;
    }

    public static Role createMockRoleStaffMember() {
        Role mockRole = new Role();
        mockRole.setRoleId(3);
        mockRole.setName("STAFF_MEMBER");
        return mockRole;
    }

    public static Role createMockRoleMember() {
        Role mockRole = new Role();
        mockRole.setRoleId(2);
        mockRole.setName("MEMBER");
        return mockRole;
    }

    public static Comment createMockComment1() {
        Comment mockComment = new Comment();
        Set<User> usersWhoLikedTheComment = new TreeSet<>();
        Set<User> usersWhoDislikedTheComment = new TreeSet<>();

        mockComment.setCommentId(1);
        mockComment.setContent("Mock comment random content.");
        mockComment.setCreatedOn(LocalDateTime.of(2024, 1, 28, 0, 0, 0));
        mockComment.setUserId(createMockNoAdminUser());

        return mockComment;
    }

    public static FilteredPostsOptions createPostFilterOptions() {
        return new FilteredPostsOptions(
                0,
                0,
                0,
                0,
                "Test title",
                "Test content",
                "2024-01-31 00:00:00",
                "2024-01-31 00:00:00",
                "Test User",
                null,
                null
        );
    }

    public static FilteredCommentsOptions createCommentFilterOptions() {
        return new FilteredCommentsOptions(
                "Content"
        );
    }

    public static FilteredUsersOptions createUserFilterOptions(){
        return new FilteredUsersOptions(
                "username",
                "email",
                "firstName"
        );
    }
}
