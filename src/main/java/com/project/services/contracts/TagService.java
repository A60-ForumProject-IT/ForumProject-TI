package com.project.services.contracts;

import com.project.models.FilteredCommentsOptions;
import com.project.models.Post;
import com.project.models.Tag;
import com.project.models.User;

import java.util.List;

public interface TagService {

    Tag getTagById(int id);

    List<Post> getAllPostsWithSpecificTag(FilteredCommentsOptions filteredCommentsOptions);

    void createTag(Tag tag, User user);

    void updateTag(Tag tag, User user);

    void deleteTag(Tag tag, User user);

    Tag getTagByName(String name);

    List<Tag> getAllTags();
}
