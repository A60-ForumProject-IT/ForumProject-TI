package com.project.repositories;

import com.project.models.Post;
import com.project.repositories.contracts.PostRepository;
import jakarta.persistence.EntityNotFoundException;
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
    public List<Post> getAllUsersPosts(int userId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query<Post> query = session.createQuery("from Post where postedBy = :user_id", Post.class);
            query.setParameter("user_id", userId);

            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Post");
            }
            return query.list();
        }
    }
}
