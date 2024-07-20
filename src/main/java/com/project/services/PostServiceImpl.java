package com.project.services;

import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
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
    public Post createPost(Post post) {
        boolean duplicateExists = true;
        try {
            postRepository.getPostByTitle(post.getTitle());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new DuplicateEntityException("Post", "title", post.getTitle());
        }

        return postRepository.createPost(post);
    }

    @Override
    public List<Post> getAllUsersPosts(int userId) {
        return postRepository.getAllUsersPosts(userId);
    }
}
