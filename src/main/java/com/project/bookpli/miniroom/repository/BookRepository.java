package com.project.bookpli.miniroom.repository;

import com.project.bookpli.entity.Book;
import com.project.bookpli.miniroom.dto.BookDTO;
import com.project.bookpli.miniroom.dto.BookResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    boolean existsById(String isbn13);

    //내가 읽는중인 책
    @Query("SELECT new com.project.bookpli.miniroom.dto.BookResponseDTO"
            +"(l.libraryId,l.userId,l.status,l.startDate,l.endDate,"
            +"b.isbn13,b.title,b.author,b.description,b.pubdate,b.publisher,b.cover,b.startindex,b.genre) "
            +"FROM Book b JOIN Library l ON b.isbn13 = l.book.isbn13 WHERE l.status = 'reading' AND l.userId = :userId ")
    List<BookResponseDTO> readBookList(@Param("userId") Long userId);


    //내가 담은 책
    @Query("SELECT new com.project.bookpli.miniroom.dto.BookResponseDTO"
        +"(l.libraryId,l.userId,l.status,l.startDate,l.endDate,"
        +"b.isbn13,b.title,b.author,b.description,b.pubdate,b.publisher,b.cover,b.startindex,b.genre) "
            +"FROM Book b JOIN Library l ON b.isbn13 = l.book.isbn13 WHERE l.status = 'wished' AND l.userId = :userId ")
    List<BookResponseDTO> addBookList(@Param("userId") Long userId);

    //내가 도서를 완료한 책
    @Query("SELECT new com.project.bookpli.miniroom.dto.BookResponseDTO"
            +"(l.libraryId,l.userId,l.status,l.startDate,l.endDate,"
            +"b.isbn13,b.title,b.author,b.description,b.pubdate,b.publisher,b.cover,b.startindex,b.genre) "
            +"FROM Book b JOIN Library l ON b.isbn13 = l.book.isbn13 WHERE l.status = 'completed' AND l.userId = :userId ")
    List<BookResponseDTO> finishBookList(@Param("userId") Long userId);

    //내가 좋아요 누른 책
    @Query("SELECT new com.project.bookpli.miniroom.dto.BookResponseDTO"
            +"(l.libraryId,l.userId,l.status,l.startDate,l.endDate,"
            +"b.isbn13,b.title,b.author,b.description,b.pubdate,b.publisher,b.cover,b.startindex,b.genre) "
            +"FROM Book b JOIN Library l ON b.isbn13 = l.book.isbn13 WHERE l.status = 'like' AND l.userId = :userId ")
    List<BookResponseDTO> likeBookList(@Param("userId") Long userId);

    //isbn에 해당하는 도서정보 출력
    @Query("SELECT new com.project.bookpli.miniroom.dto.BookDTO"
            + "(b.isbn13, b.title, b.author, b.description, b.pubdate, b.publisher, b.cover, b.startindex, b.genre) "
            + "FROM Book b WHERE b.isbn13 = :isbn13")
    List<BookDTO> findByIsbn13(@Param("isbn13") String isbn13);

}
