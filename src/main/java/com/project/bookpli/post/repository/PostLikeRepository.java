package com.project.bookpli.post.repository;

import com.project.bookpli.entity.PostLike;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.postId = :postId ")
    int countByPostId(@Param("postId") Long postId); // 좋아요 수 체크

    Optional<PostLike>findByUserIdAndPostId(@Param("userId")Long userId, @Param("postId")Long postId);

    Boolean existsByPostIdAndUserId(Long postId, Long userId); // 하트 눌렀는지 체크해서 기본값 변경해주기

    void deleteAllByPostId(Long postId);  // 게시글 삭제 시 사용

}
