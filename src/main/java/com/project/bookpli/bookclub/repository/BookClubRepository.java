package com.project.bookpli.bookclub.repository;

import com.project.bookpli.entity.BookClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookClubRepository extends JpaRepository<BookClub, Long> {

    // 북적북적 검색창
    @Query("SELECT bc FROM BookClub bc JOIN FETCH bc.book b WHERE b.title LIKE CONCAT('%', :key, '%') ")
    List<BookClub>findBookClubByName(@Param("key") String key);

    //북클럽 찾기
    @Query("SELECT bc FROM BookClub bc JOIN FETCH bc.book b where b.isbn13 = :isbn13")
    BookClub findBookClubByIsbn(@Param("isbn13") String isbn13);

}
