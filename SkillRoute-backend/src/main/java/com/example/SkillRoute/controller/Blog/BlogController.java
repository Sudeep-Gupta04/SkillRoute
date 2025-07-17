package com.example.SkillRoute.controller.Blog;

import com.example.SkillRoute.dto.Blog.PostDetails.PostDetailResponse;
import com.example.SkillRoute.dto.CreatePost.PostResponse;
import com.example.SkillRoute.service.Blog.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/blog")

public class BlogController {

    @Autowired
    private PostService postService;

    // 1. Get a single post with full details
    @GetMapping("sinlgePost/{postId}")
    public ResponseEntity<PostDetailResponse> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDetails(postId));
    }

    //2. . Search posts by title (optional)
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllOrSearchPosts(@RequestParam(required = false) String search) {
        List<PostResponse> response = postService.getAllOrSearchPosts(search);
        return ResponseEntity.ok(response);
    }



    //3. Create a post with optional image upload
    @PostMapping("/create/{userId}")
    public ResponseEntity<PostResponse> createPostWithImage(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @PathVariable("userId") long userId
    ) throws IOException {
        PostResponse response = postService.createPostWithImage(userId, title, content, imageFile);
        return ResponseEntity.ok(response);
    }

    //4. Get AllPost of a user
    @GetMapping("post/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(@PathVariable Long userId) {
        List<PostResponse> posts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(posts);
    }

}
