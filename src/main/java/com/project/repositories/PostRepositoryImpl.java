package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.Post;
import com.project.models.User;
import com.project.models.dtos.PostDtoTopComments;
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
    public List<PostDtoTopComments> getMostLikedPosts() {
        try(Session session = sessionFactory.openSession()) {
            String hql = "SELECT NEW com.project.models.dtos.PostDtoTopComments(p.title, p.content, p.createdOn, p.likes, p.dislikes, COUNT(c)) " +
                    "FROM Post p JOIN p.comments c GROUP BY p.title, p.content, p.createdOn, p.likes, p.dislikes " +
                    "ORDER BY p.likes DESC LIMIT 10";
            Query<PostDtoTopComments> query = session.createQuery(hql, PostDtoTopComments.class);

            return query.list();
        }
    }

    @Override
    public List<PostDtoTopComments> getMostCommentedPosts() {
        try(Session session = sessionFactory.openSession()) {
            String hql = "SELECT NEW com.project.models.dtos.PostDtoTopComments(p.title, p.content, p.createdOn, p.likes, p.dislikes, COUNT(c)) " +
                    "FROM Post p JOIN p.comments c GROUP BY p.title, p.content, p.createdOn, p.likes, p.dislikes " +
                    "ORDER BY COUNT(c) DESC LIMIT 10";
            Query<PostDtoTopComments> query = session.createQuery(hql, PostDtoTopComments.class);

            return query.list();
        }
    }

    @Override
    public Post createPost(Post post) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
        }
        return post;
    }

    @Override
    public void updatePost(Post post) {

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

    @Override
    public Post getPostByTitle(String title) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("FROM Post WHERE title = :title", Post.class);
            query.setParameter("title", title);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Post", "title", title);
            }
            return query.list().get(0);
        }
    }
}
