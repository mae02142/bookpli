package com.project.bookpli.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long libraryId;

    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn13")
    private Book book;
    private String status;
    private Date startDate;
    private Date endDate;

    @Builder

    public Library(Long libraryId, Long userId, Book book, String status, Date startDate, Date endDate) {
        this.libraryId = libraryId;
        this.userId = userId;
        this.book = book;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
