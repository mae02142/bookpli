package com.project.bookpli.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "Bookclub")
public class BookClub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookclub_id")
    private Long bookClubId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn13")
    private Book book;


}
