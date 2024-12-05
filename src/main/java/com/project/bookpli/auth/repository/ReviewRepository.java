package com.project.bookpli.auth.repository;

import com.project.bookpli.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository< Review, Long > {

    List<Review> findByUserId (long userId);
}
