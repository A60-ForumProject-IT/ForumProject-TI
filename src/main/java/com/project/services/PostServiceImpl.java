package com.project.services;

import com.project.models.Post;
import com.project.repositories.contracts.PostRepository;
import com.project.services.contracts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    private PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getAllUsersPosts(int userId) {
        return postRepository.getAllUsersPosts(userId);
    }
}
