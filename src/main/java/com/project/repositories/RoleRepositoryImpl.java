package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.Role;
import com.project.repositories.contracts.RoleRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public RoleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Role> getAllRoles() {
        try(Session session = sessionFactory.openSession()){
            Query<Role> query = session.createQuery("from Role", Role.class);
            return query.list();
        }
    }

    @Override
    public Role getRoleById(int id) {
        try(Session session = sessionFactory.openSession()){
            Role role = session.get(Role.class, id);
            if (role == null){
                throw new EntityNotFoundException("Role", id);
            }
            return role;
        }
    }
}
