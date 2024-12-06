package com.project.bookpli.mypage.repository;

import com.project.bookpli.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySpotifyId(String spotifyUserId);

    Optional<User> findByUserNickname (String nickName);
}
