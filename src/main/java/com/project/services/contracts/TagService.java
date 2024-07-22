package com.project.services.contracts;

import com.project.models.Tag;

import java.util.List;

public interface TagService {

    Tag getTagById(int id);

    List<Tag> getAllTags();

    void createTag(Tag tag);

    void updateTag(Tag tag);

    void deleteTag(int id);

    Tag getTagByName(String name);
}
