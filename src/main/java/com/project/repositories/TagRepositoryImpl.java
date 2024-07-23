package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.FilteredCommentsOptions;
import com.project.models.Post;
import com.project.models.Tag;
import com.project.repositories.contracts.TagRepository;
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
    public List<Post> getAllPostsWithSpecificTag(FilteredCommentsOptions filteredCommentsOptions) {
        try(Session session = sessionFactory.openSession()){
            StringBuilder hql = new StringBuilder("SELECT p FROM Post p " +
                    "JOIN p.postTags t ");

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filteredCommentsOptions.getKewWord().ifPresent(value -> {
                filters.add("t.tag like :keyWord");
                params.put("keyWord", String.format("%%%s%%", value));
            });

            if (!filters.isEmpty()) {
                hql.append(" WHERE ").append(String.join(" AND ", filters));
            }

            Query<Post> query = session.createQuery(hql.toString(), Post.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public void createTag(Tag tag) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(tag);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateTag(Tag tag) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.merge(tag);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteTag(Tag tag) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(tag);
            session.getTransaction().commit();
        }
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
