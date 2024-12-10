package com.project.bookpli.bookclub.service;

import com.project.bookpli.bookclub.dto.ReviewDTO;
import com.project.bookpli.bookclub.repository.ReviewRepository;
import com.project.bookpli.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService{
    @Autowired
    ReviewRepository repository;


    // 해당 유저가 작성한 리뷰 전체 조회
    @Override
    public  List<ReviewDTO> readByUserId (Long userId) {
        List<Review> list;

        try {
            list= repository.findByUserId(userId);
            if(list.isEmpty()) {
                System.out.println("서비스단 : 리스트가 비어있습니다. userId :" + userId);
            }
            //db 오류
        }catch (DataAccessException data){
            System.out.println("서비스단 : 데이터베이스 오류 발생 : "+ data.getMessage());
            return new ArrayList<>();
        }
        // 그 외 오류
        catch (Exception e){
            System.out.println("서비스단에서 오류 발생 : "+ e.getMessage());
            return new ArrayList<>();
        }
        return ReviewDTO.fromEntityList(list);
    }

    // 해당 도서에 대한 리뷰 전체 조회
    @Override
    public List<ReviewDTO> readByIsbn (String isbn13){
        List<Review> list;
        try {
            list=repository.findByIsbn(isbn13);
            if (list.isEmpty()){
                System.out.println("서비스단 : 리스트가 비어있습니다.  isbn : "+ isbn13);
            }
        }catch (DataAccessException data){
            System.out.println("서비스단 : 데이터베이스 오류 발생 : "+ data.getMessage());
            return new ArrayList<>();
        }
        // 그 외 오류
        catch (Exception e){
            System.out.println("서비스단에서 오류 발생 : "+ e.getMessage());
            return new ArrayList<>();
        }
        return ReviewDTO.fromEntityList(list);
    }

    // 리뷰 등록 & 수정
    @Override
    public ReviewDTO save (ReviewDTO reviewDTO){
       ReviewDTO dto = null;
        try{
           Review entity;

           // 받아온 값에 id 가 존재한다면 수정으로 전환 시키기 위함.
           if(reviewDTO.getReviewId() != null){
              entity = repository.findById(reviewDTO.getReviewId())
                      .orElseThrow(()
                              -> new RuntimeException(" 서비스단 : 리뷰를 가져오는데 실패 하였습니다." + reviewDTO.getReviewId()));
               System.out.println("서비스단 : 리뷰 수정하겠습니다 , reviewId :" + reviewDTO.getReviewId());
           }else {
               // 새로운 리뷰 등록
               entity = Review.builder()
                       .reviewId(null)
                       .userId(null)
                       .reviewContent(null)
                       .isbn13(null)
                       .rating(null)
                       .build();
               System.out.println("서비스단 : 리뷰 등록하겠습니다");
           }
            // 클라이언트로 받아온 값을 엔티티 변환 후 저장
            entity=   repository.save(reviewDTO.toEntity());
            System.out.println("엔티티로 변환 후 저장 성공");
            // 저장된 값을 dto 로 만든 후 다시 클라이언트에게 보내기
            dto = ReviewDTO.fromEntity(entity);
            System.out.println("dto로 변환 후 내보낼게요~~!!!");

        } catch (Exception e){
            System.out.println("서비스단: 리뷰 저장 오류 발생.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return dto;
    }

    // 리뷰 삭제
    @Override
    public boolean remove (Long reviewId){
        try {
            repository.deleteById(reviewId);

            Optional<Review> list = repository.findById(reviewId);

            return list.isEmpty(); // 리뷰가 존재하지 않으면 true 반환
        }catch (Exception e){
            System.out.println("서비스단 : 리뷰 삭제 중 오류 발생  reviewId -->"+ reviewId);
            e.printStackTrace();
            return  false;
        }
    }
}
