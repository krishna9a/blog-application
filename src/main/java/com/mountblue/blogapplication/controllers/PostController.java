package com.mountblue.blogapplication.controllers;

import com.mountblue.blogapplication.configurations.LoginUser;
import com.mountblue.blogapplication.entities.Post;
import com.mountblue.blogapplication.entities.Tag;
import com.mountblue.blogapplication.entities.User;
import com.mountblue.blogapplication.services.PostService;
import com.mountblue.blogapplication.services.TagService;
import com.mountblue.blogapplication.services.UserService;
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
    private final TagService tagService;
    private final UserService userService;
    private final LoginUser loginUser;


    @Autowired
    public PostController(PostService postService, TagService tagService, UserService userService,
                          LoginUser loginUser) {
        this.postService = postService;
        this.tagService = tagService;
        this.userService = userService;
        this.loginUser = loginUser;
    }



    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "desc" )
                       String sort, Model model) {
        if(page<0){
            return "redirect:/";
        }
        int pageLimit = 6;

        Sort.Direction direction = sort.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageLimit, Sort.by(direction, "publishedAt"));
        Page<Post> pagePosts = postService.getAllPostsPaged(pageable);

        if(pagePosts.getTotalPages()!=0 && pagePosts.getTotalPages()<=page){
            return "redirect:/";
        }

        List<Post> posts = pagePosts.getContent();
        allTagsAuthors(model);
        model.addAttribute("user", loginUser.getDetails());
        model.addAttribute("sort",sort);
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("url","/?");
        return "home";
    }

    @GetMapping("/create-post")
    public String createPost(Model model) {
        if(loginUser.getDetails()!=null) {
            model.addAttribute("post", new Post());
            return "new-post";
        }
        return "redirect:/";
    }

    @PostMapping("/save-post")
    public String saveNewPost(@ModelAttribute Post post,@RequestParam("postTags") String postTags) {
        post.setCreatedAt(LocalDateTime.now());
        post.setIsPublished(true);
        String content=post.getContent();
        String excerpt=content.substring(0,Math.min(250,content.length()));
        post.setExcerpt(excerpt);

        User user= loginUser.getDetails();
        post.setAuthor(user.getName());

        List<Post> posts=user.getPosts();
        posts.add(post);
        user.setPosts(posts);

        postService.savePost(post);
        createTags(post,postTags);
        tagService.removeUnusedTags();

        return "redirect:/";
    }

    @GetMapping("/post/{id}")
    public String showPost(@PathVariable Integer id,Model model){
        Post post=postService.findPostBYId(id);
        User user= loginUser.getDetails();
        boolean hasPost=false;

        if(user != null)
        {
           hasPost=user.getPosts().contains(post);
        }

        model.addAttribute("hasPost",hasPost);
        model.addAttribute("user",user);
        model.addAttribute("post",post);

        return  "post";
    }

    @GetMapping("/edit-post/{id}")
    public  String editPost(@PathVariable Integer id, Model model) {
        Post post=postService.findPostBYId(id);
        Set<Tag> tags=post.getTags();
        String postTags="";

        for(Tag tag:tags){
            postTags += tag.getName()+" , ";
        }

        model.addAttribute("user", loginUser.getDetails());
        model.addAttribute("postTags",postTags);
        model.addAttribute("post", post);

        return "edit-post";
    }
    @PostMapping("/save-edit-post")
    public String saveEditPost(@ModelAttribute Post post,@RequestParam("postTags") String postTags) {
        String content=post.getContent();
        String excerpt=content.substring(0,Math.min(250,content.length()));
        post.setExcerpt(excerpt);

        createTags(post,postTags);
        postService.savePost(post);
        tagService.removeUnusedTags();

        return "redirect:/post/"+post.getId();
    }
    @GetMapping("/delete-post/{id}")
    public String deletePost(@PathVariable Integer id){
        postService.deletePostById(id);
        tagService.removeUnusedTags();

        return "redirect:/";
    }
    @GetMapping("/filter")
    public String filterPosts(
            @RequestParam(name = "tags", required = false) List<Integer> selectedTags,
            @RequestParam(name = "authors", required = false) List<String> selectedAuthors,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "desc")String sort, Model model) {
        if(page<0){
            return "redirect:/";
        }
        int pageLimit = 6;

        Sort.Direction direction = sort.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageLimit, Sort.by(direction, "publishedAt"));
        Page<Post> filterPosts = postService.findPostsByFilters(selectedTags, selectedAuthors,
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

        List<Post> posts = filterPosts.getContent();
        allTagsAuthors(model);
        model.addAttribute("url",url);
        model.addAttribute("user", loginUser.getDetails());
        model.addAttribute("posts",posts);
        model.addAttribute("currentPage", page);

        return "home";
    }

    @GetMapping("/search")
    public  String searchPosts(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "desc")String sort,
                                       @RequestParam  String keyword,Model model){
        if(page<0){
            return "redirect:/";
        }
        int pageLimit = 6;

        Sort.Direction direction = sort.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, pageLimit, Sort.by(direction, "publishedAt"));

        Page<Post> pagePosts = postService.findPostsByKeywords(keyword,pageable);

        List<Post> posts = pagePosts.getContent();
        allTagsAuthors(model);
        model.addAttribute("user", loginUser.getDetails());
        model.addAttribute("keyword",keyword);
        model.addAttribute("sort",sort);
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        model.addAttribute("url","/search?keyword="+keyword+"&");

        return "home";
    }


    public void allTagsAuthors(Model model){
        List<Tag> tags = tagService.findAllTags();;
        List<String> authors = userService.findAllNames();;
        model.addAttribute("authors", authors);
        model.addAttribute("tags", tags);
    }

    public void createTags( Post post, String postTags) {
        String[] tagList = postTags.split(",");

        Set<Tag> tags = post.getTags();
        for (String tagName : tagList) {
            tagName = tagName.trim();
            if (tagName.isEmpty()) {
                continue;
            }
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
        post.setTags(tags);
    }
}
