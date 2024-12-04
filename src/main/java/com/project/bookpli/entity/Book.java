package com.project.bookpli.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Book {
    @Id
    private String bookId;

    private String title;
    private String author;
    private String contents;
    private Date publishDate;
    private String publisher;
    private String thumbnail;
    private String translators;
    private String genre;
}
