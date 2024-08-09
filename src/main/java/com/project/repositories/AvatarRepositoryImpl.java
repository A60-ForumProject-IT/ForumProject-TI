package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.Avatar;
import com.project.models.Role;
import com.project.repositories.contracts.AvatarRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AvatarRepositoryImpl implements AvatarRepository {
    private SessionFactory sessionFactory;

    @Autowired
    public AvatarRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Avatar saveAvatar(Avatar avatar) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(avatar);
            session.getTransaction().commit();
            return avatar;
        }
    }

    @Override
    public Avatar getAvatarById(int id) {
        try(Session session = sessionFactory.openSession()){
            Avatar avatar = session.get(Avatar.class, id);
            if (avatar == null){
                throw new EntityNotFoundException("Avatar", id);
            }
            return avatar;
        }
    }

    @Override
    public void deleteAvatar(Avatar avatar) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(avatar);
            session.getTransaction().commit();
        }
    }
}
