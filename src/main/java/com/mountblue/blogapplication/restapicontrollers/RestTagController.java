package com.mountblue.blogapplication.restapicontrollers;

import com.mountblue.blogapplication.entities.Tag;
import com.mountblue.blogapplication.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class RestTagController {

    private final TagService tagService;

    @Autowired
    public RestTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/all")
    public List<Tag> getAllTags() {
        return tagService.findAllTags();
    }




}
