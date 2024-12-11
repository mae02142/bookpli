package com.project.bookpli.comment.repository;

import com.project.bookpli.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c, u.userNickname, u.profilePath " +
            "FROM Comment c " +
            "JOIN User u ON c.userId = u.userId " +
            "WHERE c.postId = :postId")
    List<Object[]>findByPostId(@Param("postId") Long postId);        // 해당 게시글 댓글 조회

    @Query("SELECT c, u.userNickname, u.profilePath, p.bookClubId " +
            "FROM Comment c " +
            "JOIN User u ON c.userId = u.userId " +
            "JOIN Post p ON c.postId = p.postId " +
            "WHERE c.userId = :userId AND p.bookClubId = :bookClubId")
    List<Object[]>findByUserId(@Param("userId") Long userId , @Param("bookClubId") Long bookClubId);
}
