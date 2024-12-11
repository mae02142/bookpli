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
    int countByPostId(@Param("postId") Long postId);

    Optional<PostLike>findByUserIdAndPostId(@Param("userId")Long userId, @Param("postId")Long postId);
}
