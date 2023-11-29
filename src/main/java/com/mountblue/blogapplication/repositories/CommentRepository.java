package com.mountblue.blogapplication.repositories;

import com.mountblue.blogapplication.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Integer> {
}
