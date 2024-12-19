package com.project.bookpli.library.repository;

import com.project.bookpli.entity.BookLike;
import com.project.bookpli.library.dto.BookLikeDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookLikeRepository extends JpaRepository<BookLike, Long> {

    @Query("SELECT new com.project.bookpli.library.dto.BookLikeDTO(" +
            "bl.bookLikeId, bl.userId, bl.isbn13, b.title, b.author, b.cover) " +
            "FROM BookLike bl " +
            "JOIN Book b ON bl.isbn13 = b.isbn13 " +
            "WHERE bl.userId = :userId")
    List<BookLikeDTO> findAllByUserIdWithBookInfo(@Param("userId") Long userId);

    Optional<BookLike> findByUserIdAndIsbn13(Long userId, String isbn13);

    boolean existsByUserIdAndIsbn13(Long userId, String isbn13);
}
