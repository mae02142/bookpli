package com.project.bookpli.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserBookClub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userClubId;
    private Long userId;
    @ManyToOne
    private BookClub bookClub;
}
