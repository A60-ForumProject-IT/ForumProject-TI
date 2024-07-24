package com.project;

import com.project.exceptions.BlockedException;
import com.project.exceptions.DuplicateEntityException;
import com.project.exceptions.EntityNotFoundException;
import com.project.exceptions.UnauthorizedOperationException;
import com.project.helpers.TestHelpers;
import com.project.models.FilteredCommentsOptions;
import com.project.models.Post;
import com.project.models.Tag;
import com.project.models.User;
import com.project.repositories.contracts.TagRepository;
import com.project.services.TagServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTests {

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    TagServiceImpl tagService;


    @Test
    public void createTag_Should_Throw_When_TagCreatorIsBlocked() {
        Tag tagToCreate = TestHelpers.createMockTag();
        User blockedTagCreator = TestHelpers.createMockNoAdminUser();
        blockedTagCreator.setBlocked(true);

        Assertions.assertThrows(BlockedException.class,
                () -> tagService.createTag(tagToCreate, blockedTagCreator));
    }

    @Test
    public void createTag_Should_Pass() {
        Tag tag = TestHelpers.createMockTag();

        Mockito.when(tagRepository.getTagByName(tag.getTag()))
                .thenThrow(EntityNotFoundException.class);

        tagService.createTag(tag, TestHelpers.createMockNoAdminUser());

        Mockito.verify(this.tagRepository, Mockito.times(1)).createTag(tag);
    }

    @Test
    public void createTag_Should_Throw_When_TagAlreadyExists() {
        Tag tag = TestHelpers.createMockTag();
        tag.setTag("AnotherTag");

        Tag tagToCreate = TestHelpers.createMockTag();
        tagToCreate.setTag("AnotherTag");

        Mockito.when(tagRepository.getTagByName(tagToCreate.getTag()))
                .thenReturn(tag);

        Assertions.assertThrows(DuplicateEntityException.class, () ->
                tagService.createTag(tagToCreate, TestHelpers.createMockNoAdminUser()));
    }

    @Test
    public void getAllPostsWithSpecificTag_Should_Pass() {
        FilteredCommentsOptions filteredCommentsOptions = TestHelpers.createCommentFilterOptions();

        tagService.getAllPostsWithSpecificTag(filteredCommentsOptions);

        Mockito.verify(tagRepository, Mockito.times(1))
                .getAllPostsWithSpecificTag(filteredCommentsOptions);
    }

    @Test
    public void deleteTag_Should_Throw_When_UserIsNotAdmin() {
        Tag tag = TestHelpers.createMockTag();
        User nonAdminUser = TestHelpers.createMockNoAdminUser();

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> tagService.deleteTag(tag, nonAdminUser));
    }

    @Test
    public void getTagByName_Should_CallRepository() {
        tagService.getTagByName(Mockito.anyString());

        Mockito.verify(tagRepository, Mockito.times(1))
                .getTagByName(Mockito.anyString());
    }

    @Test
    public void getTagById_Should_CallRepository() {
        tagService.getTagById(Mockito.anyInt());

        Mockito.verify(tagRepository, Mockito.times(1))
                .getTagById(Mockito.anyInt());
    }

    @Test
    public void deleteTag_Should_Pass() {
        User user = TestHelpers.createMockAdminUser();
        Tag tag = TestHelpers.createMockTag();

        tagService.deleteTag(tag, user);

        Mockito.verify(tagRepository, Mockito.times(1))
                .deleteTag(tag);
    }

    @Test
    public void updateTag_Should_CallRepository() {
        User userToUpdate = TestHelpers.createMockAdminUser();
        Tag tagToBeUpdated = TestHelpers.createMockTag();

        tagService.updateTag(tagToBeUpdated, userToUpdate);

        Mockito.verify(tagRepository, Mockito.times(1))
                .updateTag(tagToBeUpdated);
    }
}
