package com.project.helpers;

import com.project.models.Post;
import com.project.models.User;
import com.project.models.dtos.PostDto;
import com.project.models.dtos.UserDto;
import com.project.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapperHelper {
    private UserService userService;

    @Autowired
    public MapperHelper(UserService userService) {
        this.userService = userService;
    }

    public User updateUserFromDto(UserDto userDto, int id) {
        User user = userService.getUserById(id);
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        return user;
    }

    public Post fromPostDto(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());

        return post;
    }
}
