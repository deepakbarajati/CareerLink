package com.careerLink.post_service.service;

import com.careerLink.post_service.entity.PostLike;
import com.careerLink.post_service.exception.BadRequestException;
import com.careerLink.post_service.exception.ResourceNotFoundException;
import com.careerLink.post_service.repository.PostLikeRepository;
import com.careerLink.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    public void likePost(Long postId,Long userId){
        log.info("Attempting to like the post with id: {}",postId);
        boolean exists =postRepository.existsById(postId);

        if(!exists) throw new ResourceNotFoundException("Post not found with id: "+postId);

        boolean alreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId,postId);

        if(alreadyLiked) throw new BadRequestException("Cannot like the same post again");

       PostLike postLike=PostLike.builder()
               .postId(postId)
               .userId(userId)
               .build();

        postLikeRepository.save(postLike);
        log.info("Post with id: {} liked successfully",postId);

    }

    public void unlikePosts(Long postId, Long userId) {
        log.info("Attempting to unlike the post with id: {}",postId);
        boolean exists =postRepository.existsById(postId);

        if(!exists) throw new ResourceNotFoundException("Post not found with id: "+postId);

        boolean alreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId,postId);

        if(!alreadyLiked) throw new BadRequestException("Cannot unlike the  post which is not liked.");


        postLikeRepository.deleteByUserIdAndPostId(userId,postId);
        log.info("Post with id: {} unliked successfully",postId);

    }
}
