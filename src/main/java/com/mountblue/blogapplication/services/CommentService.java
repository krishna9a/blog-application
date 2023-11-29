package com.mountblue.blogapplication.services;

import com.mountblue.blogapplication.entities.Comment;
import com.mountblue.blogapplication.repositories.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {
    private  final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void saveComment(Comment comment){
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public Comment findCommentById(Integer id) {
        return  commentRepository.findById(id).orElse(new Comment());
    }

    public void deleteCommentById(Integer id) {
        commentRepository.deleteById(id);
    }
}
