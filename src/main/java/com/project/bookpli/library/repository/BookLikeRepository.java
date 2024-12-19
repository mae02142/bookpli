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

    List<BookLike> findAllByUserId(Long userId);

    Optional<BookLike> findByUserIdAndBook_Isbn13(Long userId, String isbn13);

    boolean existsByUserIdAndBook_Isbn13(Long userId, String isbn13);
}
