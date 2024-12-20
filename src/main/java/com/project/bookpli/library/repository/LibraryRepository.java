package com.project.bookpli.library.repository;

import com.project.bookpli.entity.Library;
import com.project.bookpli.library.dto.LibraryResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    //독서 목표 수정
    //독서 목표설정 status update
    @Transactional
    @Modifying
    @Query("update Library l set l.status='reading', l.startDate= :startDate, l.endDate= :endDate where l.status='wished' AND l.book.isbn13 = :isbn13")
    int setReadGoal(@Param("isbn13") String isbn13, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    //독서 상태 해제로 변경
    @Transactional
    @Modifying
    @Query("update Library l set l.status='dropped', l.startDate=null, l.endDate=null where l.status='reading' AND l.book.isbn13 = :isbn13")
    int changeStatus(@Param("isbn13") String isbn13, @Param("status") String status);


    //도서완독 처리
    @Transactional
    @Modifying
    @Query("update Library l set l.status='completed' where l.status='reading' and l.book.isbn13 = :isbn13")
    int completeBook(@Param("isbn13") String isbn13, @Param("status") String status );

    @Query("SELECT new com.project.bookpli.library.dto.LibraryResponseDTO(l.libraryId, l.userId, l.status, l.startDate, l.endDate, b.isbn13, b.title, b.author, b.cover, b.startindex) " +
            "FROM Library l " +
            "JOIN l.book b " +
            "WHERE l.userId = :userId")
    List<LibraryResponseDTO> getMyLibrary(Long userId);


    @Transactional
    @Modifying
    @Query("DELETE FROM Library l WHERE l.userId = :userId AND l.libraryId = :libraryId")
    void deleteByLibraryIdAndUserId(@Param("userId") Long userId, @Param("libraryId") Long libraryId);

    //도서 실패처리
    @Transactional
    @Modifying
    @Query("update Library l set l.status='failed' where l.status='reading' and l.book.isbn13= :isbn13")
    int changeFail (@Param("isbn13") String isbn13);


}
