package com.project.bookpli.bookclub.repository;


import com.project.bookpli.entity.UserBookClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBookClubRepository extends JpaRepository<UserBookClub, Long> {

    //유저가 등록한 북클럽 리스트 조회
    @Query("SELECT ubc FROM UserBookClub ubc " +
            "JOIN FETCH ubc.bookClub bc " +
            "JOIN FETCH bc.book b " +
            "WHERE ubc.userId = :userId")
    List<UserBookClub> myBookClub(@Param("userId") Long userId);

    boolean existsByUserIdAndBookClub_Book_Isbn13(Long userId, String isbn13);

    void deleteByUserClubId(Long userClubId);
}
