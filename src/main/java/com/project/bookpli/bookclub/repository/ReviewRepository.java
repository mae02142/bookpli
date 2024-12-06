package com.project.bookpli.bookclub.repository;

import com.project.bookpli.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository< Review, Long > {

    @Query("SELECT r FROM Review r JOIN User u ON r.userId = u.userId WHERE u.userId = :userId")
    List<Review> findByUserId(@Param("userId") long userId);

    @Query("SELECT r FROM Review r JOIN Book b ON r.isbn13 = b.isbn13 WHERE b.isbn13 = :isbn13")
    List<Review> findByIsbn(@Param("isbn13") String isbn13);
}
