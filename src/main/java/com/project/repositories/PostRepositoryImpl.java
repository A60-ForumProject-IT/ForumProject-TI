package com.project.repositories;

import com.project.exceptions.EntityNotFoundException;
import com.project.models.FilteredPostsOptions;
import com.project.models.Post;
import com.project.models.dtos.PostDtoTop;
import com.project.repositories.contracts.PostRepository;
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
public class PostRepositoryImpl implements PostRepository {
    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Post> getAllPosts(FilteredPostsOptions filteredPostsOptions, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            List<Post> posts = filterPostOptions(filteredPostsOptions, session);
            int start = (page - 1) * size;
            return posts.subList(start, Math.min(start + size, posts.size()));
        }
    }

    @Override
    public Post getPostById(int postId) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, postId);
            if (post == null) {
                throw new EntityNotFoundException("Post", postId);
            }
            return post;
        }
    }

    @Override
    public List<PostDtoTop> getMostLikedPosts() {
        try(Session session = sessionFactory.openSession()) {
            String hql = """
            SELECT NEW com.project.models.dtos.PostDtoTop(p.title, p.content, p.createdOn, p.likes, p.dislikes, CAST(COUNT(c) AS Long)) \
            FROM Post p JOIN p.comments c GROUP BY p.title, p.content, p.createdOn, p.likes, p.dislikes \
            ORDER BY p.likes DESC LIMIT 10""";
            Query<PostDtoTop> query = session.createQuery(hql, PostDtoTop.class);

            return query.list();
        }
    }

    @Override
    public List<PostDtoTop> getMostCommentedPosts() {
        try(Session session = sessionFactory.openSession()) {
            String hql = """
                    SELECT NEW com.project.models.dtos.PostDtoTop(p.title, p.content, p.createdOn, p.likes, p.dislikes, CAST(COUNT(c) AS Long)) \
                    FROM Post p JOIN p.comments c GROUP BY p.title, p.content, p.createdOn, p.likes, p.dislikes \
                    ORDER BY COUNT(c) DESC LIMIT 10""";
            Query<PostDtoTop> query = session.createQuery(hql, PostDtoTop.class);

            return query.list();
        }
    }

    @Override
    public List<PostDtoTop> getMostRecentPosts() {
        try (Session session = sessionFactory.openSession()) {
            String hql = """
                    SELECT NEW com.project.models.dtos.PostDtoTop(p.title, p.content, p.createdOn, p.likes, p.dislikes, CAST(COUNT(c) AS Long)) \
                    FROM Post p JOIN p.comments c GROUP BY p.title, p.content, p.createdOn, p.likes, p.dislikes \
                    ORDER BY p.createdOn DESC LIMIT 10""";
            Query<PostDtoTop> query = session.createQuery(hql, PostDtoTop.class);
            return query.list();
        }
    }

    @Override
    public int getTotalPostsCount() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COUNT(p) FROM Post p";
            Query<Long> query = session.createQuery(hql, Long.class);
            return Math.toIntExact(query.uniqueResult());
        }
    }

    @Override
    public void addTagToPost(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteTagFromPost(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Post> getMostRecentPostsMvc() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Post p ORDER BY p.createdOn DESC";
            Query<Post> query = session.createQuery(hql, Post.class);
            query.setMaxResults(10);
            return query.list();
        }
    }

    @Override
    public List<Post> getMostLikedPostsMvc() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT p " +
                    "FROM Post p " +
                    "LEFT JOIN p.usersWhoLikedPost users " +
                    "GROUP BY p " +
                    "ORDER BY COUNT(users) DESC";

            Query<Post> query = session.createQuery(hql, Post.class);
            query.setMaxResults(10);
            return query.list();
        }
    }

    @Override
    public List<Post> getMostCommentedPostsMvc() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT c.commentedPost FROM Comment c GROUP BY c.commentedPost ORDER BY COUNT(c.id) DESC";
            Query<Post> query = session.createQuery(hql, Post.class);
            query.setMaxResults(10);
            return query.list();
        }
    }

    @Override
    public Post createPost(Post post) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
        }
        return post;
    }

    @Override
    public void updatePost(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Post existingPost = session.get(Post.class, post.getPostId());

            if (existingPost != null) {
                existingPost.setTitle(post.getTitle());
                existingPost.setContent(post.getContent());
                existingPost.setLikes(post.getLikes());
                existingPost.setDislikes(post.getDislikes());
                existingPost.setUsersWhoLikedPost(post.getUsersWhoLikedPost());
                existingPost.setUsersWhoDislikedPost(post.getUsersWhoDislikedPost());

                session.update(existingPost);
            } else {
                session.saveOrUpdate(post);
            }

            session.getTransaction().commit();
        }
    }

    @Override
    public void deletePost(Post post) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Post> getAllUsersPosts(int userId, FilteredPostsOptions postFilterOptions) {
        try (Session session = sessionFactory.openSession()) {
            return filterPostOptions(userId, postFilterOptions, session);
        }
    }

    @Override
    public Post getPostByTitle(String title) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("FROM Post WHERE title = :title", Post.class);
            query.setParameter("title", title);
            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("Post", "title", title);
            }
            return query.list().get(0);
        }
    }

    private String generateOrderBy(FilteredPostsOptions filteredPostsOptions) {
        if (filteredPostsOptions.getSortBy().isEmpty()) {
            return "";
        }
        String orderBy = "";
        switch (filteredPostsOptions.getSortBy().get()) {
            case "likes":
                orderBy = "likes";
                break;
            case "dislikes":
                orderBy = "dislikes";
                break;
            case "title":
                orderBy = "title";
                break;
            case "content":
                orderBy = "content";
                break;
            case "createdOn":
                orderBy = "createdOn";
                break;
            case "createdBy":
                orderBy = "createdBy.userName";
                break;
            default:
                return "";
        }

        orderBy = String.format(" order by %s", orderBy);

        if (filteredPostsOptions.getSortOrder().isPresent() && filteredPostsOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }
        return orderBy;
    }

    private List<Post> filterPostOptions(FilteredPostsOptions filteredPostsOptions, Session session) {
        List<String> filters = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder queryString = new StringBuilder("FROM Post AS post ");


        filteredPostsOptions.getMinLikes().ifPresent(value -> {
            filters.add(" post.likes >=: minLikes ");
            parameters.put("minLikes", value);
        });

        filteredPostsOptions.getMinDislikes().ifPresent(value -> {
            filters.add(" post.dislikes >=: minDislikes ");
            parameters.put("minDislikes", value);
        });

        filteredPostsOptions.getMaxLikes().ifPresent(value -> {
            filters.add(" post.likes <=: maxLikes ");
            parameters.put("maxLikes", value);
        });

        filteredPostsOptions.getMaxDislikes().ifPresent(value -> {
            filters.add(" post.dislikes <=: maxDislikes ");
            parameters.put("maxDislikes", value);
        });

        filteredPostsOptions.getTitle().ifPresent(value -> {
            filters.add(" post.title like :title ");
            parameters.put("title", String.format("%%%s%%", value));
        });

        filteredPostsOptions.getTagName().ifPresent(value -> {
            queryString.append("JOIN post.postTags tags ");
            filters.add("tags.tag LIKE :tagName");
            parameters.put("tagName", "%" + value + "%");
        });

        filteredPostsOptions.getContent().ifPresent(value -> {
            filters.add(" post.content like :content ");
            parameters.put("content", String.format("%%%s%%", value));
        });

        filteredPostsOptions.getCreatedBefore().ifPresent(value -> {
            filters.add(" post.createdOn < :createdBefore ");
            parameters.put("createdBefore", value);
        });

        filteredPostsOptions.getCreatedAfter().ifPresent(value -> {
            filters.add(" post.createdOn > :createdAfter ");
            parameters.put("createdAfter", value);
        });

        filteredPostsOptions.getPostedBy().ifPresent(value -> {
            filters.add(" post.postedBy.username like :postedBy ");
            parameters.put("postedBy", String.format("%%%s%%", value));
        });

        if (!filters.isEmpty()) {
            queryString.append(" WHERE ").append(String.join(" AND ", filters));
        }

        queryString.append(generateOrderBy(filteredPostsOptions));
        Query<Post> query = session.createQuery(queryString.toString(), Post.class);
        query.setProperties(parameters);
        return query.list();
    }

    private List<Post> filterPostOptions(int userId, FilteredPostsOptions postFilterOptions, Session session) {
        List<String> filters = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        StringBuilder queryString = new StringBuilder("FROM Post AS post ");

        filters.add(" post.postedBy.id = :id ");
        parameters.put("id", userId);

        postFilterOptions.getMinLikes().ifPresent(value -> {
            filters.add(" post.likes >=: minLikes ");
            parameters.put("minLikes", value);
        });

        postFilterOptions.getMinDislikes().ifPresent(value -> {
            filters.add(" post.dislikes >=: minDislikes ");
            parameters.put("minDislikes", value);
        });

        postFilterOptions.getMaxLikes().ifPresent(value -> {
            filters.add(" post.likes <=: maxLikes ");
            parameters.put("maxLikes", value);
        });

        postFilterOptions.getMaxDislikes().ifPresent(value -> {
            filters.add(" post.dislikes <=: maxDislikes ");
            parameters.put("maxDislikes", value);
        });

        postFilterOptions.getTitle().ifPresent(value -> {
            filters.add(" post.title like :title ");
            parameters.put("title", String.format("%%%s%%", value));
        });

        postFilterOptions.getContent().ifPresent(value -> {
            filters.add(" post.content like :content ");
            parameters.put("content", String.format("%%%s%%", value));
        });

        postFilterOptions.getCreatedBefore().ifPresent(value -> {
            filters.add(" post.createdOn < :createdBefore ");
            parameters.put("createdBefore", value.atStartOfDay());
        });

        postFilterOptions.getCreatedAfter().ifPresent(value -> {
            filters.add(" post.createdOn > :createdAfter ");
            parameters.put("createdAfter", value.atStartOfDay());
        });

        if (!filters.isEmpty()) {
            queryString.append(" WHERE ").append(String.join(" AND ", filters));
        }

        queryString.append(generateOrderBy(postFilterOptions));
        Query<Post> query = session.createQuery(queryString.toString(), Post.class);
        query.setProperties(parameters);
        return query.list();
    }

    @Override
    public int getFilteredPostsCount(FilteredPostsOptions filteredPostsOptions) {
        try (Session session = sessionFactory.openSession()) {
            List<String> filters = new ArrayList<>();
            Map<String, Object> parameters = new HashMap<>();
            StringBuilder queryString = new StringBuilder("SELECT COUNT(post) FROM Post AS post ");

            if (filteredPostsOptions.getTagName().isPresent()) {
                queryString.append("JOIN post.postTags tags ");
                filters.add("tags.tag LIKE :tagName");
                parameters.put("tagName", "%" + filteredPostsOptions.getTagName().get() + "%");
            }

            filteredPostsOptions.getMinLikes().ifPresent(value -> {
                filters.add(" post.likes >= :minLikes ");
                parameters.put("minLikes", value);
            });

            filteredPostsOptions.getMinDislikes().ifPresent(value -> {
                filters.add(" post.dislikes >= :minDislikes ");
                parameters.put("minDislikes", value);
            });

            filteredPostsOptions.getMaxLikes().ifPresent(value -> {
                filters.add(" post.likes <= :maxLikes ");
                parameters.put("maxLikes", value);
            });

            filteredPostsOptions.getMaxDislikes().ifPresent(value -> {
                filters.add(" post.dislikes <= :maxDislikes ");
                parameters.put("maxDislikes", value);
            });

            filteredPostsOptions.getTitle().ifPresent(value -> {
                filters.add(" post.title LIKE :title ");
                parameters.put("title", "%" + value + "%");
            });

            filteredPostsOptions.getContent().ifPresent(value -> {
                filters.add(" post.content LIKE :content ");
                parameters.put("content", "%" + value + "%");
            });

            filteredPostsOptions.getCreatedBefore().ifPresent(value -> {
                filters.add(" post.createdOn < :createdBefore ");
                parameters.put("createdBefore", value);
            });

            filteredPostsOptions.getCreatedAfter().ifPresent(value -> {
                filters.add(" post.createdOn > :createdAfter ");
                parameters.put("createdAfter", value);
            });

            filteredPostsOptions.getPostedBy().ifPresent(value -> {
                filters.add(" post.postedBy.username LIKE :postedBy ");
                parameters.put("postedBy", "%" + value + "%");
            });

            if (!filters.isEmpty()) {
                queryString.append(" WHERE ").append(String.join(" AND ", filters));
            }

            Query<Long> query = session.createQuery(queryString.toString(), Long.class);
            query.setProperties(parameters);

            return Math.toIntExact(query.uniqueResult());
        }
    }
}
