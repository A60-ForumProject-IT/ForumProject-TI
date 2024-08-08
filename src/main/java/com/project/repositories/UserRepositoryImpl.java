package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.FilteredUsersOptions;
import com.project.models.PhoneNumber;
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
    public List<User> getAllUsers(FilteredUsersOptions filteredUsersOptional) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("from User ");
            List<String> filtered = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filteredUsersOptional.getUsername().ifPresent(value -> {
                filtered.add("username like :username ");
                params.put("username", String.format("%%%s%%", value));
            });
            filteredUsersOptional.getFirstName().ifPresent(value -> {
                filtered.add("firstName like :firstName ");
                params.put("firstName", String.format("%%%s%%", value));
            });

            filteredUsersOptional.getEmail().ifPresent(value -> {
                filtered.add("email like :email ");
                params.put("email", String.format("%%%s%%", value));
            });


            if (!filtered.isEmpty()) {
                hql.append(" where ").append(String.join(" AND ", filtered));
            }
            hql.append(generateOrderBy(filteredUsersOptional));
            Query<User> query = session.createQuery(hql.toString(), User.class);
            query.setProperties(params);
            return query.list();
        }

    }

    private String generateOrderBy(FilteredUsersOptions filteredUsersOptional) {
        if (filteredUsersOptional.getSortBy().isEmpty()) {
            return "";
        }
        String orderBy = "";
        switch (filteredUsersOptional.getSortBy().get()) {
            case "username":
                orderBy = "username";
                break;
            case "email":
                orderBy = "email";
                break;
            case "firstName":
                orderBy = "firstName";
                break;
            default:
                return "";
        }
        orderBy=String.format(" ORDER BY %s", orderBy);
        if(filteredUsersOptional.getSortOrder().isPresent() &&
                filteredUsersOptional.getSortOrder().get().equalsIgnoreCase("desc")){
            orderBy = String.format("%s DESC", orderBy);
        }
        return orderBy;
    }

    @Override
    public User getUserById(int id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
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
            List<User> users = query.list();
            if (users.isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return users.get(0);
        }
    }

    @Override
    public User getByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);
            List<User> users = query.list();
            if (users.isEmpty()) {
                throw new EntityNotFoundException("User", "email", email);
            }
            return users.get(0);
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

//    try (Session session = sessionFactory.openSession()) {
//        Query<User> query = session.createQuery("from User where username = :username", User.class);
//        query.setParameter("username", username);
//        List<User> users = query.list();
//        if (users.isEmpty()) {
//            throw new EntityNotFoundException("User", "username", username);
//        }
//        return users.get(0);

    @Override
    public Long countAllUsers() {

        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u) FROM User u", Long.class);
            return query.uniqueResult();
        }
    }

    @Override
    public void userToBeModerator(User userToBeAdmin) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(userToBeAdmin);
            session.getTransaction().commit();
        }
    }


}
