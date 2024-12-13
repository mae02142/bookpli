/*
package com.project.bookpli.post.service;

import com.project.bookpli.entity.Post;
import com.project.bookpli.post.dto.PostImageDTO;
import com.project.bookpli.post.repository.PostImageRepository;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class PostImageService {

    private final PostImageRepository postImageRepository;

    // 문의하기
    public PostImageDTO createInquiry(PostImageDTO request) throws IOException {

        // postImage Entity 변환 및 저장
        Post = Post.builder()
                .userId(request.getUserId())
                .inquiryTitle(request.getInquiryTitle())
                .inquiryContents(request.getInquiryContents())
                .build();
        Inquiry savedInquiry =  inqRepository.save(inquiry);

        List<InquiryImageDTO> imagesToSave = new ArrayList<>();

        // 이미지 저장
        if (request.getImageUrls() != null) {
            for (String imageUrl : request.getImageUrls()) {
                InquiryImage image = InquiryImage.builder()
                        .inquiryId(savedInquiry.getInquiryId())
                        .imagePath(imageUrl)
                        .build();
                imgRepository.save(image);
                imagesToSave.add(InquiryImageDTO.fromEntity(image));
            }
        }
        return InquiryResponseDTO.fromEntity(inquiry, imagesToSave);
    }
}
*/
