package com.project.repositories.contracts;

import com.project.models.Tag;

import java.util.List;

public interface TagRepository {

    Tag getTagById(int id);

    List<Tag> getAllTags();

    void createTag(Tag tag);

    void updateTag(Tag tag);

    void deleteTag(int id);

    Tag getTagByName(String name);
}
