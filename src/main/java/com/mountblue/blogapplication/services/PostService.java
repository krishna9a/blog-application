package com.mountblue.blogapplication.services;

import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class PostService {

    private final PostRepository postRepository;
    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void savePost(Post post) {
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }


    public Post findPostById(Integer id) {
        return postRepository.findById(id).orElse(new Post());
    }

    public void deletePostById(Integer id) {
        postRepository.deleteById(id);
    }

    public Page<Post> getAllPostsPaged(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Page<Post> findPostsByFilters(
            List<Integer> selectedTags,
            List<String> selectedAuthors,
            Pageable pageable) {

        return postRepository.findByTagIdsAndAuthors(selectedTags, selectedAuthors,
               pageable);
    }

    public Page<Post> findPostsByKeywords(String keyword, Pageable pageable) {
        return postRepository.findPostsByKeywords(keyword,pageable);
    }
}
