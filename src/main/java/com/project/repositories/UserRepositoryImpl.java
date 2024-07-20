package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.User;
import com.project.repositories.contracts.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User", User.class);
            if(query.list().isEmpty()){
                throw new IllegalArgumentException("There are no users");
            }
            return query.list();
        }

    }

    @Override
    public User getUserById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if(user == null){
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public void update(User user) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void create(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }
    }
}
