package com.project.bookpli.review.repository;

import com.project.bookpli.entity.Review;
import com.project.bookpli.review.dto.ReviewDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository< Review, Long > {

    @Query("SELECT r, b.cover, b.title FROM Review r " +
            "JOIN User u ON r.userId = u.userId " +
            "JOIN Book b ON r.isbn13 = b.isbn13 " +
            "WHERE r.userId = :userId")
    List<Object[]> findByUserId(@Param("userId") long userId);


//    @Query("SELECT r, u,userNickname , u.profilePath FROM Review r " +
//            "JOIN User u ON r.userId = u.userId " +
//            "JOIN Book b ON r.isbn13 = b.isbn13 " +
//            "WHERE b.isbn13 = :isbn13")
//    List<ReviewDTO> findByIsbn(@Param("isbn13") String  isbn13);
    //도서 리뷰 조회
    @Query("SELECT new com.project.bookpli.review.dto.ReviewDTO( " +
            "r.reviewId, r.userId, r.isbn13, r.reviewContent, r.rating, " +
            "u.userNickname, u.profilePath) " +
            "FROM Review r " +
            "JOIN User u ON r.userId = u.userId " +
            "JOIN Book b ON r.isbn13 = b.isbn13 " +
            "WHERE b.isbn13 = :isbn13")
    List<ReviewDTO> findByIsbn(@Param("isbn13") String isbn13);

}
