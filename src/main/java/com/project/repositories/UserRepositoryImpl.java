package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.FilteredUsersOptional;
import com.project.models.User;
import com.project.repositories.contracts.UserRepository;
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
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<User> getAllUsers(FilteredUsersOptional filteredUsersOptional) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("from User ");
            List<String> filtered = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filteredUsersOptional.getUsername().ifPresent(value -> {
                filtered.add("username like :username ");
                params.put("username", String.format("%%%s%%", value));
            });

            filteredUsersOptional.getEmail().ifPresent(value -> {
                filtered.add("email like :email ");
                params.put("email", String.format("%%%s%%", value));
            });

            filteredUsersOptional.getFirstName().ifPresent(value -> {
                filtered.add("firstName like :firstName ");
                params.put("firstName", String.format("%%%s%%", value));
            });

            if (!filtered.isEmpty()) {
                hql.append(" where ").append(String.join(" AND ", filtered));
            }

            Query <User> query = session.createQuery(hql.toString(), User.class);
            query.setProperties(params);
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

    @Override
    public User getByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);
            List<User> beers = query.list();
            if (beers.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return beers.get(0);
        }
    }

    @Override
    public void blockUser(User user) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void unblocked(User userToUnblock) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(userToUnblock);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteUser(User user) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(user);
            session.getTransaction().commit();
        }
    }
}
