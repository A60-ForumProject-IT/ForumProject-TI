package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.Comment;
import com.project.models.FilteredCommentsOptions;
import com.project.models.Post;
import com.project.models.User;
import com.project.repositories.contracts.CommentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Comment> getAllCommentsFromPost(Post post, FilteredCommentsOptions filteredCommentsOptions) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("FROM Comment WHERE commentedPost.id = :postId");

            List<String> filtered = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filteredCommentsOptions.getKeyWord().ifPresent(value -> {
                filtered.add("content like :keyWord");
                params.put("keyWord", String.format("%%%s%%", value));
            });

            if (!filtered.isEmpty()) {
                hql.append(" AND ").append(String.join(" AND ", filtered));
            }

            Query<Comment> query = session.createQuery(hql.toString(), Comment.class);
            query.setParameter("postId", post.getPostId());
            query.setProperties(params);
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

    @Override
    public Comment getCommentById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Comment comment = session.get(Comment.class, id);
            if (comment == null) {
                throw new EntityNotFoundException("Comment", id);
            }
            return comment;
        }
    }

    @Override
    public void update(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteComment(Comment comment) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Comment> getAllUserComments(int userId, FilteredCommentsOptions filteredCommentsOptions) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("FROM Comment WHERE userId.id = :userId");

            List<String> filtered = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filteredCommentsOptions.getKeyWord().ifPresent(value -> {
                filtered.add("content LIKE :keyWord");
                params.put("keyWord", String.format("%%%s%%", value));
            });

            if (!filtered.isEmpty()) {
                hql.append(" AND ").append(String.join(" AND ", filtered));
            }

            Query<Comment> query = session.createQuery(hql.toString(), Comment.class);
            query.setParameter("userId", userId);
            query.setProperties(params);

            return query.list();
        }
    }
}
