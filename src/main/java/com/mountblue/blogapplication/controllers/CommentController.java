package com.mountblue.blogapplication.controllers;

import com.mountblue.blogapplication.configurations.LoginUser;
import com.mountblue.blogapplication.entities.Comment;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.User;
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
    private static final String ADMIN_ROLE = "ADMIN";
    private final CommentService commentService;
    private  final PostService postService;

    private  final LoginUser loginUser;


    @Autowired
    public CommentController(CommentService commentService, PostService postService, LoginUser loginUser) {
        this.commentService = commentService;
        this.postService = postService;
        this.loginUser = loginUser;
    }

    @PostMapping("/add-comment/{postId}")
    public  String addComment(@PathVariable Integer postId, @ModelAttribute Comment comment){
        Post post=postService.findPostById(postId);

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
    public  String editComment(@PathVariable Integer id, Model model){
        Comment comment=commentService.findCommentById(id);

        Post post = comment.getPost();
        User currentUser= loginUser.getDetails();

        if(currentUser.getRole().equals(ADMIN_ROLE)||currentUser.getPosts().contains(post)) {
            model.addAttribute("post", comment.getPost());
            model.addAttribute("commentId", id);
            return "edit-comment";
        }

        return "redirect:/post/"+post.getId();
    }

    @PostMapping("/update-comment")
    public String updateComment(Integer id,String content){
       Comment comment=commentService.findCommentById(id);
       comment.setCommentContent(content);
       commentService.saveComment(comment);
       return "redirect:/post/"+comment.getPost().getId();
    }

    @GetMapping("/delete-comment/{id}")
    public String deleteComment(@PathVariable Integer id) {

        Comment comment = commentService.findCommentById(id);
        Post post = comment.getPost();
        User currentUser = loginUser.getDetails();
        if (currentUser.getRole().equals(ADMIN_ROLE) || currentUser.getPosts().contains(post)) {
            commentService.deleteCommentById(id);

        }
        return "redirect:/post/" + post.getId();
    }
}
