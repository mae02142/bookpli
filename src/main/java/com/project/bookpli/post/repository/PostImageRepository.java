package com.project.bookpli.post.repository;

import com.project.bookpli.entity.Post;
import com.project.bookpli.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // 북클럽 전체 게시글 조회
    @Query("SELECT pi FROM PostImage pi " +
            "JOIN Post p ON pi.postId = p.postId  " +
            "WHERE p.bookClubId = :bookClubId")
    List<PostImage> findImageUrlByBookclubId(@Param("bookClubId") Long bookClubId);

    @Query("SELECT pi FROM PostImage pi " +
            "JOIN Post p ON pi.postId = p.postId  " +
            "JOIN User u ON p.userId = u.userId " +
            "WHERE p.bookClubId = :bookClubId AND p.userId = :userId ")
    List<PostImage> findByUserIdAndBookClubId(@Param("userId")Long userId, @Param("bookClubId")Long bookClubId);


    List<PostImage> findByPostId(@Param("postId")Long postId);

    void deleteAllByPostId(@Param("postId")Long postId);
}
