package com.mountblue.blogapplication.restapicontrollers;

import com.mountblue.blogapplication.configurations.LoginUser;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.Tag;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.services.PostService;
import com.mountblue.blogapplication.services.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/posts")
public class RestPostController {

    private final PostService postService;
    private  final TagService tagService;
    private final LoginUser loginUser;


    @Autowired
    public RestPostController(PostService postService, RestTagController restTagController, RestUserController restUserController, TagService tagService, LoginUser loginUser) {
        this.postService = postService;
        this.tagService = tagService;
        this.loginUser = loginUser;
    }

    @GetMapping("/")
    public ResponseEntity<List<Post>> getPosts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "desc") String sort) {
        int numberOfPosts = 6;

        Sort.Direction direction = sort.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, numberOfPosts, Sort.by(direction, "publishedAt"));
        Page<Post> postsPage = postService.getAllPostsPaged(pageable);

        if (postsPage.getTotalPages() != 0 && postsPage.getTotalPages() <= page) {
            return null;
        }

        return  new ResponseEntity<List<Post>>(postsPage.getContent(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable Integer id) {
        return postService.findPostById(id);
    }

    @PostMapping("/create")
    public String createPost(@RequestBody Post post, @RequestParam("postTags") String postTags) {
        post.setCreatedAt(LocalDateTime.now());
        post.setIsPublished(true);

        String content = post.getContent();
        String excerpt = content.substring(0, Math.min(250, content.length()));
        post.setExcerpt(excerpt);

        User user = loginUser.getDetails();
        post.setAuthor(user.getName());

        List<Post> posts = user.getPosts();
        posts.add(post);
        user.setPosts(posts);

        postService.savePost(post);
        createTags(post, postTags);
        tagService.removeUnusedTags();

        return "Post created successfully";
    }

    @PutMapping("/update/{id}")
    public String updatePost(@PathVariable Integer id, @RequestBody Post updatedPost,
                             @RequestParam("postTags") String postTags) {
        Post post = postService.findPostById(id);
        User currentUser= loginUser.getDetails();
        if(currentUser.getRole().equals("ADMIN")||currentUser.getPosts().contains(post)) {
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());

            String content = post.getContent();
            String excerpt = content.substring(0, Math.min(250, content.length()));
            post.setExcerpt(excerpt);

            createTags(post, postTags);
            postService.savePost(post);
            tagService.removeUnusedTags();

            return "Post updated successfully";
        }
        return "Unauthorized";
    }

    @DeleteMapping("/delete/{id}")
    public String deletePost(@PathVariable Integer id) {
        User currentUser= loginUser.getDetails();
        Post post=postService.findPostById(id);
        if(currentUser.getRole().equals("ADMIN")||currentUser.getPosts().contains(post)) {
            postService.deletePostById(id);
            tagService.removeUnusedTags();
            return "Post deleted successfully";
        }
        return "Unauthorized";
    }
    private void createTags( Post post, String postTags) {
        String[] tagList = postTags.split(",");

        Set<Tag> tags = post.getTags();
        for (String tagName : tagList) {
            tagName = tagName.trim();
            if (!tagName.isEmpty()) {
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
        }
        post.setTags(tags);
    }
}
