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
    private String isbn13;

    private String title;
    private String author;
    private String description;
    private Date pubdate;
    private String publisher;
    private String cover;
    private Integer startIndex;
    private String genre;
}
