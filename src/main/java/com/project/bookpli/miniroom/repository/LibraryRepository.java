package com.project.bookpli.miniroom.repository;

import com.project.bookpli.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {
    
    //독서중인 책
    @Query("SELECT b from Book b where b.isbn13 IN (SELECT l.book.isbn13 FROM Library l WHERE l.status= :status)")
    List<Library> readingBookList(@Param("status") String status);

    //가장 많이 읽은 달
    @Query("SELECT MONTH(endDate) AS month, COUNT(l) AS bookCnt FROM Library l WHERE l.status = 'completed' GROUP BY MONTH(l.endDate) ORDER BY bookCnt DESC ")
    String[] mostReadMonth(); //DTO사용해야할까? 아닐까? 어떻게 하나~ 우리 만남은 ~ 빙글빙글~ 돌! 고!

    //1년동안 읽은 도서수
    @Query("SELECT YEAR(l.endDate) AS year, COUNT(l.libraryId) AS bookCnt FROM Library l WHERE l.status = 'completed' GROUP BY YEAR(l.endDate) ORDER BY year ASC")
    String bookCntOrderByYear();

}
