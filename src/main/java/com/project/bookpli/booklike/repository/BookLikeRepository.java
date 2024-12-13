package com.project.bookpli.booklike.repository;

import com.project.bookpli.entity.BookLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookLikeRepository extends JpaRepository<BookLike, Long> {

    boolean existsByUserIdAndIsbn13(Long userId, String isbn13);

    Optional<BookLike> findByUserIdAndIsbn13(Long userId, String isbn13);
}
