package com.project.helpers;

import com.project.models.*;
import com.project.models.dtos.*;
import com.project.repositories.contracts.CommentRepository;
import com.project.repositories.contracts.UserRepository;
import com.project.services.TagServiceImpl;
import com.project.services.contracts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class MapperHelper {
    public static final int USER = 1;
    private final TagService tagService;
    private UserService userService;
    private RoleService roleService;
    private PostService postService;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private AvatarService avatarService;

    @Autowired
    public MapperHelper(UserService userService, RoleService roleService,
                        PostService postService, UserRepository userRepository,
                        CommentRepository commentRepository, TagService tagService, AvatarService avatarService) {
        this.userService = userService;
        this.roleService = roleService;
        this.postService = postService;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.tagService = tagService;
        this.avatarService = avatarService;
    }

    public User updateUserFromDto(UserDto userDto, int id) {
        User user = userRepository.getUserById(id);
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        return user;
    }

    public Post fromPostDto(PostDto postDto, User user) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setPostedBy(user);
        post.setCreatedOn(LocalDate.now());
        return post;
    }

    public Post fromPostDtoToUpdate(PostDto postDto, int id) {
        Post post = postService.getPostById(id);
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        return post;
    }

    public User createUserFromRegistrationDto(RegistrationDto registrationDto) {
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(registrationDto.getPassword());
        user.setUsername(registrationDto.getUsername());
        user.setRole(roleService.getRoleById(USER));
        user.setBlocked(false);
        return user;
    }

//    public User registerFromDto(RegistrationDto dto){
//        User user = new User();
//        user.setUsername(dto.getUsername());
//        user.setPassword(dto.getPassword());
//        user.setFirstName(dto.getFirstName());
//        user.setLastName(dto.getLastName());
//        user.setEmail(dto.getEmail());
//        user.setAdmin(false);
//        return user;
//    }

    public Comment createCommentForPostFromCommentDto(CommentDto commentDto, User user, Post post) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUserId(user);
        comment.setCommentedPost(post);
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }

    public Comment fromCommentDtoToUpdate(CommentDto commentDto, int id) {
        Comment comment = commentRepository.getCommentById(id);
        comment.setContent(commentDto.getContent());
        comment.setCreatedOn(LocalDateTime.now());
        return comment;
    }

    public Tag fromTagDto(TagDto tagDto) {
        Tag tag = new Tag();
        tag.setTag(tagDto.getTagName());
        return tag;
    }

    public Tag fromTagDtoToUpdate(TagDto tagDto, int id) {
        Tag tag = tagService.getTagById(id);
        tag.setTag(tagDto.getTagName());
        return tag;
    }

    public PhoneNumber getFromPhoneDto(PhoneNumberDto phoneNumberDto) {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setPhoneNumber(phoneNumberDto.getPhoneNumber());
        return phoneNumber;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent(comment.getContent());
        return commentDto;
    }
}
