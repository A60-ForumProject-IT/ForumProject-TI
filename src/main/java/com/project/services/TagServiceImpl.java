package com.project.services;

import com.project.models.Tag;
import com.project.repositories.contracts.TagRepository;
import com.project.services.contracts.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag getTagById(int id) {
        return tagRepository.getTagById(id);
    }

    @Override
    public List<Tag> getAllTags() {
        return List.of();
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
        return tagRepository.getTagByName(name);
    }
}
