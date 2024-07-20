package com.project.services;

import com.project.models.Post;
import com.project.models.dtos.PostDtoTopComments;
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
    public List<Post> getAllPosts() {
        return postRepository.getAllPosts();
    }

    @Override
    public Post getPostById(int postId) {
        return postRepository.getPostById(postId);
    }

    @Override
    public List<PostDtoTopComments> getMostLikedPosts() {
        return postRepository.getMostLikedPosts();
    }

    @Override
    public List<PostDtoTopComments> getMostCommentedPosts() {
        return postRepository.getMostCommentedPosts();
    }

    @Override
    public List<Post> getAllUsersPosts(int userId) {
        return postRepository.getAllUsersPosts(userId);
    }
}
