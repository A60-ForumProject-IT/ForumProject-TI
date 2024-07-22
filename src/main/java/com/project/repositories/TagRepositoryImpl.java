package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.Comment;
import com.project.models.Role;
import com.project.models.Tag;
import com.project.repositories.contracts.TagRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {
    private SessionFactory sessionFactory;

    @Autowired
    public TagRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Tag getTagById(int id) {
        try(Session session = sessionFactory.openSession()){
            Tag tag = session.get(Tag.class, id);
            if (tag == null){
                throw new EntityNotFoundException("Tag", id);
            }
            return tag;
        }
    }

    @Override
    public List<Tag> getAllTags() {
        try(Session session = sessionFactory.openSession()){
            Query<Tag> query = session.createQuery("from Tag", Tag.class);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Tag");
            }
            return query.list();
        }
    }

    @Override
    public void createTag(Tag tag) {

    }

    @Override
    public void updateTag(Tag tag) {

    }

    @Override
    public void deleteTag(int id) {

    }

    @Override
    public Tag getTagByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Tag> query = session.createQuery("FROM Tag WHERE tag = :name", Tag.class);
            query.setParameter("name", name);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Tag", "name", name);
            }
            return query.list().get(0);
        }
    }
}
