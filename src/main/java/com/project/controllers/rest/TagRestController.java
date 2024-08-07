package com.project.controllers.rest;

import com.project.exceptions.AuthenticationException;
import com.project.exceptions.BlockedException;
import com.project.exceptions.EntityNotFoundException;
import com.project.helpers.AuthenticationHelper;
import com.project.helpers.MapperHelper;
import com.project.models.FilteredCommentsOptions;
import com.project.models.Post;
import com.project.models.Tag;
import com.project.models.User;
import com.project.models.dtos.TagDto;
import com.project.services.contracts.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/forum")
public class TagRestController {
    private final TagService tagService;
    private AuthenticationHelper authenticationHelper;
    private MapperHelper mapperHelper;

    @Autowired
    public TagRestController(TagService tagService, AuthenticationHelper authenticationHelper, MapperHelper mapperHelper) {
        this.tagService = tagService;
        this.authenticationHelper = authenticationHelper;
        this.mapperHelper = mapperHelper;
    }

    @GetMapping("/tags")
    public List<Post> getAllPostsWithSpecificTag(@RequestHeader HttpHeaders headers, @RequestParam(required = false) String keyWord) {
        try {
            authenticationHelper.tryGetUser(headers);
            FilteredCommentsOptions filteredCommentsOptions = new FilteredCommentsOptions(keyWord);
            return tagService.getAllPostsWithSpecificTag(filteredCommentsOptions);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/tags/{id}")
    public Tag getTagById(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            authenticationHelper.tryGetUser(headers);
            return tagService.getTagById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/tags")
    public void createTag(@Valid @RequestBody TagDto tagDto, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Tag tag = mapperHelper.fromTagDto(tagDto);
            tagService.createTag(tag, user);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/tags/{tagId}")
    public void updateTag(@Valid @RequestBody TagDto tagDto, @PathVariable int tagId, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Tag tag = mapperHelper.fromTagDtoToUpdate(tagDto, tagId);
            tagService.updateTag(tag, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/tags/{id}")
    public void deleteTag(@PathVariable int id, @RequestHeader HttpHeaders headers) {
        try {
            User user = authenticationHelper.tryGetUser(headers);
            Tag tag = tagService.getTagById(id);
            tagService.deleteTag(tag, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (BlockedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }


}
