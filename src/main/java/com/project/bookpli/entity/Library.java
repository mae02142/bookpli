package com.project.bookpli.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long libraryId;

    //FK
    private Long userId;
    //FK
    private String bookId;
    private String status;
    private Date startDate;
    private Date endDate;
}
