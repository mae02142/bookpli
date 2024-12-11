package com.project.bookpli.post.service;

import com.project.bookpli.entity.Post;
import com.project.bookpli.entity.PostLike;
import com.project.bookpli.post.dto.PostLikeDTO;
import com.project.bookpli.post.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;

    // 좋아요 수 조회
    public int likeCount (Long postId) {

            int counting = postLikeRepository.countByPostId(postId);

            return counting;

    }

    // 좋아요 && 취소 기능
    public boolean postLike (PostLikeDTO postLikeDTO) {

        Long userId = postLikeDTO.getUserId();
        Long postId = postLikeDTO.getPostId();

        Optional<PostLike> counting = postLikeRepository.findByUserIdAndPostId(userId, postId);
        if (counting.isPresent()) {
            postLikeRepository.delete(counting.get());
            return false;
        } else {
            PostLike newLike = postLikeDTO.toEntity();
            postLikeRepository.save(newLike);
            return true;
        }
    }
}
