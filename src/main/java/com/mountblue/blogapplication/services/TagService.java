package com.mountblue.blogapplication.services;

import com.mountblue.blogapplication.entities.Tag;
import com.mountblue.blogapplication.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public void saveTag(Tag tag) {
        tagRepository.save(tag);
    }


    public Tag findTagByName(String tagName) {
        return tagRepository.findByName(tagName);
    }

    public void removeUnusedTags() {
        List<Tag> tags= tagRepository.findAll();
        for(Tag tag:tags){
            if (tag.getPosts() == null || tag.getPosts().isEmpty()){
                tagRepository.delete(tag);
            }
        }
    }

    public List<Tag> findAllTags() {
        return tagRepository.findAll();

    }


}
