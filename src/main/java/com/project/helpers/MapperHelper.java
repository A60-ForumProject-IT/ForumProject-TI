package com.project.helpers;

import com.project.models.Post;
import com.project.models.User;
import com.project.models.dtos.PostDto;
import com.project.models.dtos.RegistrationDto;
import com.project.models.dtos.UserDto;
import com.project.repositories.contracts.UserRepository;
import com.project.services.contracts.PostService;
import com.project.services.contracts.RoleService;
import com.project.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MapperHelper {
    private UserService userService;
    private RoleService roleService;
    private PostService postService;
    private UserRepository userRepository;

    @Autowired
    public MapperHelper(UserService userService, RoleService roleService,
                        PostService postService, UserRepository userRepository) {
        this.userService = userService;
        this.roleService = roleService;
        this.postService = postService;
        this.userRepository = userRepository;
    }

    public User updateUserFromDto(UserDto userDto, int id) {
        User user = userRepository.getUserById( id);
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
        post.setCreatedOn(LocalDateTime.now());
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
        user.setRole(roleService.getRoleById(1));
        return user;
    }
}
