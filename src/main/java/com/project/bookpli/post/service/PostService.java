package com.project.bookpli.post.service;

import com.project.bookpli.comment.repository.CommentLikeRepository;
import com.project.bookpli.comment.repository.CommentRepository;
import com.project.bookpli.comment.service.CommentService;
import com.project.bookpli.common.exception.BaseException;
import com.project.bookpli.common.response.BaseResponseStatus;
import com.project.bookpli.entity.Comment;
import com.project.bookpli.entity.Post;
import com.project.bookpli.entity.PostImage;
import com.project.bookpli.post.dto.PostDTO;
import com.project.bookpli.post.dto.PostImageDTO;
import com.project.bookpli.post.dto.PostRequestDTO;
import com.project.bookpli.post.dto.PostResponseDTO;
import com.project.bookpli.post.repository.PostImageRepository;
import com.project.bookpli.post.repository.PostLikeRepository;
import com.project.bookpli.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.cache.spi.support.CollectionReadOnlyAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostImageRepository postImageRepository;


    // 해당 북클럽의 전체 게시글 조회
    public List<PostResponseDTO> readClubPosts(Long bookclubId) {

        List<Object[]> listAll = postRepository.findByBookClubId(bookclubId);

        if (listAll.isEmpty()) {
            System.out.println("리스트가 비어있습니다");
            return new ArrayList<>();
        }

        List<PostImage> imgList = postImageRepository.findImageUrlByBookclubId(bookclubId);

        // Object[]를 PostDTO로 변환
        List<PostDTO> posts = listAll.stream()
                .map(row -> {
                    Post post = (Post) row[0];
                    String nickname = (String) row[1];
                    String profilePath = (String) row[2];
                    return PostDTO.fromEntity(post, nickname, profilePath);
                })
                .collect(Collectors.toList());

        // 이미지랑 포스트 병합
        List<PostResponseDTO> response = posts.stream()
                .map(post -> PostResponseDTO.fromEntity(post, imgList)) // imgList 전체 전달
                .collect(Collectors.toList());

        return response;
    }


    // 유저의 해당 북클럽 전체 게시글 조회
    public List<PostResponseDTO> readUserPosts(Long userId, Long bookclubId) {

        // post 조회
        List<Object[]> listAll = postRepository.findByUserIdAndBookClubId(userId, bookclubId);

        if (listAll.isEmpty()) {
            System.out.println("작성한 글이 없습니다");
            return new ArrayList<>();
        }

        List<PostDTO> posts = listAll.stream()
                .map(row -> {
                    Post post = (Post) row[0];
                    String nickname = (String) row[1];
                    String profilePath = (String) row[2];
                    return PostDTO.fromEntity(post, nickname, profilePath);
                })
                .collect(Collectors.toList());

        // image 조회
        List<PostImage> imageList = postImageRepository.findByUserIdAndBookClubId(userId, bookclubId);

        // 이미지랑 게시글 병합
        List<PostResponseDTO> response = posts.stream()
                .map(post -> PostResponseDTO.fromEntity(post, imageList))
                .collect(Collectors.toList());

        return response;
    }

    // 수정할 때 특정 게시글 조회 하기 위함
    public PostResponseDTO readOne(Long postId) {
        Post entity = postRepository.findById(postId)
                .orElseThrow();

        PostDTO post = PostDTO.fromEntityForOne(entity);

        List<PostImage> list = postImageRepository.findByPostId(postId);
        if (list.isEmpty()) {
            list = new ArrayList<>();
        }

        PostResponseDTO postOne = PostResponseDTO.fromEntityOne(post, list);

        if (postOne == null) {
            throw new BaseException(BaseResponseStatus.POST_NOT_FOUND);
        }
        ;

        return postOne;
    }

    // 댓글 조회에서 사용될 게시글 조회
    public PostResponseDTO readForComment(Long postId) {
        // 게시글 조회

        List<Object[]> enPost = postRepository.findByPost(postId);

        // 단일 객체로 처리
        Object[] row = enPost.get(0); // 첫 번째 결과만 가져옴
        Post en = (Post) row[0];
        String userNickname = (String) row[1];
        String profilePath = (String) row[2];

        // PostDTO 객체 생성 및 반환
        PostDTO post = PostDTO.fromEntity(en, userNickname, profilePath);


        // 이미지 조회
        List<PostImage> imageList = postImageRepository.findByPostId(postId);


        PostResponseDTO response = PostResponseDTO.fromEntity(post, imageList);
        System.out.println("response" + response);
        return response;
    }


    // 게시글 저장
    @Transactional
    public Boolean createPost(PostRequestDTO requestDTO) {
        try {
            // 게시글 저장
            Post post = Post.builder()
                    .userId(requestDTO.getUserId())
                    .postContent(requestDTO.getPostContent())
                    .bookClubId(requestDTO.getBookClubId())
                    .build();

            postRepository.save(post);  // 게시글 저장...

            Long postId = post.getPostId(); // 저장 후 해당 postId 얻기

            // 이미지 URL이 있을 경우 처리
            if (requestDTO.getImageUrl() != null && !requestDTO.getImageUrl().isEmpty()) {
                for (PostImageDTO imageDTO : requestDTO.getImageUrl()) {
                    // 이미지 URL을 처리하면서, PostImage 객체로 변환하여 저장
                    PostImage image = PostImage.builder()
                            .postId(postId)
                            .imageUrl(imageDTO.getImageUrl()) // imageUrl 사용
                            .build();
                    postImageRepository.save(image);
                }
                return true;
            }
        } catch (Exception e) {
            System.out.println("등록 중 오류 발생" + e.getMessage());
        }
        return false;
    }



    @Transactional
    public boolean update(PostRequestDTO postRequestDTO) {
        try {
            // 게시글 업데이트
            Post post = Post.builder()
                    .postId(postRequestDTO.getPostId())
                    .userId(postRequestDTO.getUserId())
                    .postContent(postRequestDTO.getPostContent())
                    .bookClubId(postRequestDTO.getBookClubId())
                    .postDate(postRequestDTO.getPostDate())
                    .build();
            postRepository.save(post);

            Long postId = post.getPostId();

            // 기존 이미지 목록 조회
            List<PostImage> existing = postImageRepository.findByPostId(postId);

            // 요청된 이미지 목록을 엔티티로 변환
            List<PostImage> request = postRequestDTO.toEntity();

            // 기존 이미지와 요청 이미지 비교 후 삭제할 이미지 찾기
            List<Long> deleteImgId = postRequestDTO.getImageUrl().stream()
                    .filter(img -> img.getImageId() != null)  // null이 아닌 이미지 ID만
                    .map(PostImageDTO::getImageId)
                    .collect(Collectors.toList());

            List<PostImage> deleteImg = existing.stream()
                    .filter(existingimg -> !deleteImgId.contains(existingimg.getImageId()))
                    .collect(Collectors.toList());

            // 요청에 없는 이미지 삭제
            if (!deleteImg.isEmpty()) {
                postImageRepository.deleteAll(deleteImg);
            }

            // 새 이미지 저장
            List<PostImage> newImgs = request.stream()
                    .filter(newImg -> newImg.getImageId() == null) // id 없는 새로운 이미지
                    .collect(Collectors.toList());

            if (!newImgs.isEmpty()) {
                postImageRepository.saveAll(newImgs);
            }

            // 기존 이미지 업데이트 (imageUrl 수정 시)
            request.stream()
                    .filter(updateImg -> updateImg.getImageId() != null) // id 있는 기존 이미지
                    .forEach(updateImg -> {
                        existing.stream()
                                .filter(existingimg -> existingimg.getImageId().equals(updateImg.getImageId()))
                                .findFirst()
                                .ifPresent(existingimg -> {
                                    // 기존 이미지에서만 수정된 URL이 있을 경우에만 업데이트
                                    if (!existingimg.getImageUrl().equals(updateImg.getImageUrl())) {
                                        PostImage updatedImage = PostImage.builder()
                                                .imageId(existingimg.getImageId())
                                                .postId(existingimg.getPostId())
                                                .imageUrl(updateImg.getImageUrl()) // 수정된 imageUrl
                                                .build();
                                        postImageRepository.save(updatedImage);
                                    }
                                });
                    });

            return true;
        } catch (Exception e) {
            System.out.println("서비스단 : 오류발생");
            e.printStackTrace();
            return false;
        }
    }



    // 게시글 삭제
    @Transactional
    public boolean delete (Long postId){
        try {
            Optional<Post> list = postRepository.findById(postId);
            if(list.isEmpty()){
               throw new BaseException(BaseResponseStatus.INVAILD_POST);
            }

            // 1. comment like 삭제
            List<Comment>commentsId =commentRepository.findByPostId(postId);
            if(!commentsId.isEmpty()){
               List<Long> cmId=  commentsId.stream().map(Comment :: getCommentId)
                                .collect(Collectors.toList());

                commentLikeRepository.deleteAllByCommentIdIn(cmId);
            }
            // 2. comment 삭제
            commentRepository.deleteAllByPostId(postId);

            // 3. post like 삭제
            postLikeRepository.deleteAllByPostId(postId);

            // 4. postImage 삭제
            postImageRepository.deleteAllByPostId(postId);

            // 5. post 삭제
            postRepository.deleteById(postId);

            Optional<Post> check = postRepository.findById(postId);
            return check.isEmpty();
        }catch (Exception e){
            System.out.println("서비스단: 오류 발생");
            e.printStackTrace();
            return false;
        }
    }
}
