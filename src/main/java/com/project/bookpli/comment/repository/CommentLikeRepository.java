package com.project.bookpli.comment.repository;

import com.project.bookpli.entity.CommentLike;
import com.project.bookpli.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // counting
    @Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.commentId = :commentId ")
    int countByCommentId(@Param("commentId") Long commentId);

    // update and delete
    Optional<CommentLike> findByUserIdAndCommentId(@Param("userId")Long userId, @Param("commentId")Long commentId);

    //checking user
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    boolean deleteByCommentId(Long commentId);  // 댓글 삭제 시 함께 삭제

    void deleteAllByCommentIdIn(List<Long> commentIds);  // 게시글 삭제 시 함께 삭제
}
