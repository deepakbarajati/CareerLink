package com.careerLink.post_service.controller;

import com.careerLink.post_service.dto.PostCreateRequestDto;
import com.careerLink.post_service.dto.PostDto;
import com.careerLink.post_service.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/core")
public class PostController {

    private final PostService postService;
    @PostMapping()
    public ResponseEntity<PostDto> createPost(@RequestBody PostCreateRequestDto postCreateRequestDto, HttpServletRequest httpServletRequest){
        PostDto createdPost = postService.createPost(postCreateRequestDto,1L);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long postId){
        PostDto postDto=postService.getPostById(postId);
        return ResponseEntity.status(HttpStatus.FOUND).body(postDto);
    }


    @GetMapping("/users/{userId}/allPosts")
    public ResponseEntity<List<PostDto>> getAllPostsOfUser(@PathVariable Long userId){
        List<PostDto> posts=postService.getAllPostsOfUser(userId);
        return ResponseEntity.status(HttpStatus.FOUND).body(posts);
    }
}
