package com.project.repositories.contracts;

import com.project.models.FilteredCommentsOptions;
import com.project.models.Post;
import com.project.models.Tag;

import java.util.List;

public interface TagRepository {

    Tag getTagById(int id);

    List<Post> getAllPostsWithSpecificTag(FilteredCommentsOptions filteredCommentsOptions);

    void createTag(Tag tag);

    void updateTag(Tag tag);

    void deleteTag(Tag tag);

    Tag getTagByName(String name);

}
