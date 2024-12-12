package com.project.bookpli.comment.service;

import com.project.bookpli.comment.dto.CommentLikeDTO;
import com.project.bookpli.comment.repository.CommentLikeRepository;
import com.project.bookpli.entity.CommentLike;
import com.project.bookpli.entity.PostLike;
import com.project.bookpli.post.dto.PostLikeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;

    // 좋아요 수 조회
    public int likeCount (Long commentId) {

        int counting = commentLikeRepository.countByCommentId(commentId);

        return counting;
    }

    // 좋아요 && 취소 기능
    public boolean commentLike (CommentLikeDTO commentLikeDTO) {

        Long userId = commentLikeDTO.getUserId();
        Long commentId = commentLikeDTO.getCommentId();

        Optional<CommentLike> counting = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);
        if (counting.isPresent()) {
            commentLikeRepository.delete(counting.get());
            return false;
        } else {
            CommentLike newLike = commentLikeDTO.toEntity();
            commentLikeRepository.save(newLike);
            return true;
        }
    }

    // 해당 댓글에 좋아요 되어있는지 체크
    public boolean checkingLike ( Long commentId, Long userId) {
        boolean response = false;
        if (userId != null && commentId != null) {
            response = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
        }
        return response;
    }
}
