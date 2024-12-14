package com.project.bookpli.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "postimage")
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;
    private Long postId;
    private String imageUrl;

    public PostImage(Long postId, String imageUrl){
        this.postId = postId;
        this.imageUrl = imageUrl;
    }

}
