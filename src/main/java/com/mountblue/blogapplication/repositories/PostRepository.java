package com.mountblue.blogapplication.repositories;

import com.mountblue.blogapplication.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {

    @Query("SELECT DISTINCT p.author FROM Post p")
    List<String> findAllAuthors();


    @Query("SELECT p FROM Post p " +
            "WHERE ( EXISTS (SELECT t FROM p.tags t WHERE t.id IN :selectedTags)) " +
            "OR ( p.author IN :selectedAuthors) ")
    Page<Post> findByTagIdsAndAuthors(
            @Param("selectedTags") List<Integer> selectedTags,
            @Param("selectedAuthors") List<String> selectedAuthors,
            Pageable pageable
    );




    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t WHERE p.author " +
            "LIKE %:keyword% OR p.content LIKE %:keyword% OR t.name LIKE %:keyword%")
    Page<Post> findPostsByKeywords(String keyword, Pageable pageable);

}
