package com.mountblue.blogapplication.controllers;

import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.Tag;
import com.mountblue.blogapplication.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Set;

@Controller
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    public void createAndSaveTags(Post post, String postTags) {

        String[] tagList = postTags.split(",");

        Set<Tag> tags = post.getTags();
        for (String tagName : tagList) {
            tagName = tagName.trim();
            if (tagName.isEmpty()) {
                continue;
            }
            Tag tag = tagService.findTagByName(tagName);
            if (tag == null) {
                tag = new Tag(tagName);
            }

            List<Post> posts = tag.getPosts();
            posts.add(post);
            tag.setPosts(posts);
            tags.add(tag);

            tagService.saveTag(tag);
        }
        post.setTags(tags);
    }

    public void findAndRemoveUnusedTags() {
        tagService.removeUnusedTags();
    }

    public List<Tag> findAllTags() {
        return tagService.findAllTags();
    }

}
