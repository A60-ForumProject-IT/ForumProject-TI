package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.Post;
import com.project.models.User;
import com.project.repositories.contracts.PostRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepository {
    private SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public List<Post> getAllPosts() {
        try (Session session = sessionFactory.openSession()) {
            //TODO:
            return null;
        }
    }

    @Override
    public Post getPostById(int postId) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, postId);
            if (post == null) {
                throw new EntityNotFoundException("Post", postId);
            }
            return post;
        }
    }

    @Override
    public List<Post> getMostLikedPosts() {
        try(Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("FROM Post ORDER BY createdOn DESC LIMIT 10");
            return query.list();
        }
    }

    @Override
    public List<Post> getMostCommentedPosts() {
        try(Session session = sessionFactory.openSession()) {
            String hql = "SELECT p FROM Post p JOIN p.comments c GROUP BY p.id ORDER BY COUNT(c.id) DESC";
            Query<Post> query = session.createQuery(hql, Post.class);
            query.setMaxResults(5);
            return query.getResultList();
        }
    }

    @Override
    public List<Post> getAllUsersPosts(int userId) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, userId);
            Query<Post> query = session.createQuery("FROM Post WHERE postedBy = :user", Post.class);
            query.setParameter("user", user);
            List<Post> posts = query.list();
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException(userId);
            }
            return posts;
        }
    }
}
