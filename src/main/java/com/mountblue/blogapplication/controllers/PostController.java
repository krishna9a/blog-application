package com.mountblue.blogapplication.controllers;

import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.Tag;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class PostController {

    private final PostService postService;
    private final TagController tagController;

    private final UserController userController;
    @Autowired
    public PostController(PostService postService, TagController tagController, UserController userController) {
        this.postService = postService;
        this.tagController = tagController;
        this.userController = userController;
    }




    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "desc" )
                       String sort, Model model) {
        if(page<0){
            return "redirect:/";
        }
        int numberOfPosts = 6;

        Sort.Direction direction = sort.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, numberOfPosts, Sort.by(direction, "publishedAt"));
        Page<Post> postsPage = postService.getAllPostsPaged(pageable);

        if(postsPage.getTotalPages()!=0&&postsPage.getTotalPages()<=page){
            return "redirect:/";
        }

        List<Post> posts = postsPage.getContent();
        findAllTagsAuthors(model);

        model.addAttribute("user",userController.getUser());
        model.addAttribute("sort",sort);
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("url","/?");
        return "home";
    }

    @GetMapping("/create-post")
    public String showCreatePostForm(Model model) {
        model.addAttribute("post", new Post());
        return "new-post";
    }

    @PostMapping("/save-post")
    public String saveNewPost(@ModelAttribute Post post,@RequestParam("postTags") String postTags) {

        post.setCreatedAt(LocalDateTime.now());
        post.setIsPublished(true);

        String content=post.getContent();
        String excerpt=content.substring(0,Math.min(250,content.length()));
        post.setExcerpt(excerpt);

        User user= userController.getUser();
        post.setAuthor(user.getName());

        List<Post> posts=user.getPosts();
        posts.add(post);
        user.setPosts(posts);

        postService.savePost(post);
        tagController.createAndSaveTags(post,postTags);
        tagController.findAndRemoveUnusedTags();

        return "redirect:/";
    }

    @GetMapping("/post/{id}")
    public String showPost(@PathVariable Integer id,Model model){
        Post post=postService.findPostBYId(id);
        User user=userController.getUser();

        boolean userHasPost=false;
        if(user != null)
        {
           userHasPost=user.getPosts().contains(post);
            System.out.println(userHasPost);
        }

        model.addAttribute("userHasPost",userHasPost);
        model.addAttribute("user",user);
        model.addAttribute("post",post);

        return  "post";
    }

    @GetMapping("/edit-post/{id}")
    public  String showEditPostFrom(@PathVariable Integer id, Model model) {
        Post post=postService.findPostBYId(id);

        Set<Tag> tags=post.getTags();

        String postTags="";
        for(Tag tag:tags){
            postTags += tag.getName()+" , ";
        }

        model.addAttribute("user",userController.getUser());
        model.addAttribute("postTags",postTags);
        model.addAttribute("post", post);

        return "edit-post";
    }
    @PostMapping("/save-edit-post")
    public String saveEditPost(@ModelAttribute Post post,@RequestParam("postTags") String postTags) {

        String content=post.getContent();
        String excerpt=content.substring(0,Math.min(250,content.length()));
        post.setExcerpt(excerpt);

        System.out.println(post.toString());

        tagController.createAndSaveTags(post,postTags);
        postService.savePost(post);
        tagController.findAndRemoveUnusedTags();

        return "redirect:/post/"+post.getId();
    }
    @GetMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Integer id){

        postService.deletePostById(id);
        tagController.findAndRemoveUnusedTags();
        return "redirect:/";
    }
    @GetMapping("/filter")
    public String findFilterPost(
            @RequestParam(name = "tags", required = false) List<Integer> selectedTags,
            @RequestParam(name = "authors", required = false) List<String> selectedAuthors,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "desc")String sort, Model model) {
        if(page<0){
            return "redirect:/";
        }
        int numberOfPosts = 6;

        Sort.Direction direction = sort.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, numberOfPosts, Sort.by(direction, "publishedAt"));
        Page<Post> filteredPosts = postService.findPostsByFilters(selectedTags, selectedAuthors,
                pageable);

        String url="/filter?";
        if(selectedTags!=null){
            url+="tags="+String.join("&tags=", selectedTags.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList()));

        }
        if(selectedAuthors!=null){
            url+="&authors="+String.join("&authors=",selectedAuthors);
        }
        url+="&";

        List<Post> posts=filteredPosts.getContent();
        findAllTagsAuthors(model);
        model.addAttribute("url",url);
        model.addAttribute("user",userController.getUser());
        model.addAttribute("sort",sort);
        model.addAttribute("posts",posts);
        model.addAttribute("currentPage", page);

        return "home";
    }

    @GetMapping("/search")
    public  String findPostsByKeywords(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "desc")String sort,
                                       @RequestParam  String keyword,Model model){
        if(page<0){
            return "redirect:/";
        }
        int numberOfPosts = 6;

        Sort.Direction direction = sort.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, numberOfPosts, Sort.by(direction, "publishedAt"));

        Page<Post>postsPage=postService.findPostsByKeywords(keyword,pageable);

        List<Post> posts=postsPage.getContent();
        findAllTagsAuthors(model);
        model.addAttribute("user",userController.getUser());
        model.addAttribute("keyword",keyword);
        model.addAttribute("sort",sort);
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("url","/search?keyword="+keyword+"&");
        return "home";
    }


    public void findAllTagsAuthors(Model model){
        List<Tag> tags = tagController.findAllTags();
        List<String> authors = userController.findAllAuthorsName();
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
    }
}
