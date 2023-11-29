package com.mountblue.blogapplication.controllers;

import com.mountblue.blogapplication.entities.Comment;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.services.CommentService;
import com.mountblue.blogapplication.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class CommentController {

    private final CommentService commentService;
    private  final PostService postService;
    @Autowired
    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping("/add-comment/{postId}")
    public  String addNewComment(@PathVariable Integer postId, @ModelAttribute Comment comment){
        Post post=postService.findPostBYId(postId);

        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        List<Comment> comments=post.getComments();
        comments.add(comment);
        post.setComments(comments);
        comment.setPost(post);
        commentService.saveComment(comment);

        return "redirect:/post/"+postId;
    }

    @GetMapping("/edit-comment/{id}")
    public  String showEditCommentPage(@PathVariable Integer id, Model model){
        Comment comment=commentService.findCommentById(id);
        model.addAttribute("post",comment.getPost());
        model.addAttribute("commentId",id);
        return "editcomment";
    }

    @PostMapping("/update-comment")
    public String saveUpdateComment( Integer id,String content){
       Comment comment=commentService.findCommentById(id);
       comment.setCommentContent(content);
       commentService.saveComment(comment);
       return "redirect:/post/"+comment.getPost().getId();
    }

    @GetMapping("/delete-comment/{id}")
    public String deleteComment(@PathVariable Integer id){
        Comment comment=commentService.findCommentById(id);
        commentService.deleteCommentById(id);
        return "redirect:/post/"+comment.getPost().getId();
    }
}
