package com.project.bookpli.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private Long userId;

    @Column(name = "bookclub_id")
    private Long bookClubId;
    private String postContent;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date postDate;


        //날짜 형태 변환
    public String dateFormatted(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(postDate);
    }
}
