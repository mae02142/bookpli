package com.project.bookpli.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BookLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookLikeId;
    private Long userId;
    private String isbn13;
}
