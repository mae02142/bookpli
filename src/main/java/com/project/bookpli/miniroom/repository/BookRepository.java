package com.project.bookpli.miniroom.repository;

import com.project.bookpli.entity.Book;
import com.project.bookpli.miniroom.dto.BookResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    //내가 좋아요 누른 책
//    @Query("SELECT b FROM Book b JOIN BookLike bl ON b.isbn13 = bl.isbn13 WHERE bl.userId = :userId")
//    List<Book> likeBookList(@Param("userId") Long userId);

    //내가 좋아요 누른 책2
    @Query("SELECT new com.project.bookpli.miniroom.dto.BookResponseDTO"
            +"(l.libraryId,l.userId,l.status,l.startDate,l.endDate,"
            +"b.isbn13,b.title,b.author,b.description,b.pubdate,b.publisher,b.cover,b.startindex,b.genre) "
            +"FROM Book b JOIN Library l ON b.isbn13 = l.book.isbn13 WHERE l.status = 'reading' AND l.userId = :userId ")
    List<BookResponseDTO> likeBookList(@Param("userId") Long userId);

//    //내가 담은 책
//    @Query("SELECT b FROM Book b JOIN Library l ON b.isbn13 = l.book.isbn13 WHERE l.status = 'wished' and l.userId= :userId")

//    List<Book> readingBookList(@Param("userId") Long userId);
    //내가 담은 책2
    @Query("SELECT new com.project.bookpli.miniroom.dto.BookResponseDTO"
        +"(l.libraryId,l.userId,l.status,l.startDate,l.endDate,"
        +"b.isbn13,b.title,b.author,b.description,b.pubdate,b.publisher,b.cover,b.startindex,b.genre) "
            +"FROM Book b JOIN Library l ON b.isbn13 = l.book.isbn13 WHERE l.status = 'wished' AND l.userId = :userId ")
    List<BookResponseDTO> testBookList(@Param("userId") Long userId);
}
