package com.mountblue.blogapplication.restapicontrollers;

import com.mountblue.blogapplication.configurations.LoginUser;
import com.mountblue.blogapplication.entities.Comment;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.services.CommentService;
import com.mountblue.blogapplication.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class RestCommentController {

    private final CommentService commentService;
    private final PostService postService;
    private final LoginUser loginUser;

    @Autowired
    public RestCommentController(CommentService commentService, PostService postService, LoginUser loginUser) {
        this.commentService = commentService;
        this.postService = postService;
        this.loginUser = loginUser;
    }

    @PostMapping("/add/{postId}")
    public String addNewComment(@PathVariable Integer postId, @RequestBody Comment comment) {
        Post post = postService.findPostBYId(postId);

        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        List<Comment> comments = post.getComments();
        comments.add(comment);
        post.setComments(comments);
        comment.setPost(post);
        commentService.saveComment(comment);

        return "Comment added successfully";
    }

    @GetMapping("/edit/{id}")
    public Comment showEditCommentPage(@PathVariable Integer id) {
        return commentService.findCommentById(id);
    }

    @PutMapping("/update/{id}")
    public String saveUpdateComment(@PathVariable Integer id, @RequestBody Comment updatedComment) {

        Comment comment = commentService.findCommentById(id);

        Post post = comment.getPost();
        User currentUser= loginUser.getDetails();

        if(currentUser.getRole().equals("ADMIN")||currentUser.getPosts().contains(post)) {
            comment.setCommentContent(updatedComment.getCommentContent());
            commentService.saveComment(comment);
            return "Comment updated successfully";
        }
        return "Unauthorized";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteComment(@PathVariable Integer id) {
        Comment comment = commentService.findCommentById(id);

        Post post = comment.getPost();
        User currentUser= loginUser.getDetails();

        if(currentUser.getRole().equals("ADMIN")||currentUser.getPosts().contains(post)) {
            commentService.deleteCommentById(id);
            return "Comment deleted successfully";
        }

        return "Unauthorized";
    }
}
