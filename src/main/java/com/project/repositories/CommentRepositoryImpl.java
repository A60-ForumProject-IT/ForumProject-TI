package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.Comment;
import com.project.models.Post;
import com.project.repositories.contracts.CommentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Comment> getAllCommentsFromPost(int id) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Comment WHERE commentedPost.id = :postId";
            Query<Comment> query = session.createQuery(hql, Comment.class);
            query.setParameter("postId", id);
            return query.list();
        }
    }

    @Override
    public Comment getCommentByContent(String content) {
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery("FROM Comment WHERE content = :content", Comment.class);
            query.setParameter("content", content);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Comment", "content", content);
            }
            return query.list().get(0);
        }
    }

    @Override
    public void createComment(Comment comment) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(comment);
            session.getTransaction().commit();
        }
    }
}
