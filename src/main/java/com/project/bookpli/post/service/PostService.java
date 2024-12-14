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
import com.project.bookpli.post.dto.PostRequestDTO;
import com.project.bookpli.post.dto.PostResponseDTO;
import com.project.bookpli.post.repository.PostImageRepository;
import com.project.bookpli.post.repository.PostLikeRepository;
import com.project.bookpli.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
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
    public List<PostResponseDTO> readClubPosts (Long bookclubId){

        List<Object[]> listAll = postRepository.findByBookClubId(bookclubId);

        if(listAll.isEmpty()){
            System.out.println("리스트가 비어있습니다");
            return new ArrayList<>();
        }

        List<PostImage> imgList = postImageRepository.findImageUrlByBookclubId(bookclubId);
        if(imgList.isEmpty()){
            System.out.println("등록된 이미지가 없습니다.");
            return new ArrayList<>();
        }

        // Object[]를 PostDTO로 변환
        List<PostDTO> posts =  listAll.stream()
                .map(row -> {
                    Post post = (Post) row[0];
                    String nickname = (String) row[1];
                    String profilePath = (String) row[2];
                    return PostDTO.fromEntity(post, nickname, profilePath);
                })
                .collect(Collectors.toList());

        // post id 기준으로 이미지 매칭
        Map<Long, List<String>> imageMap = imgList.stream()
                .collect(Collectors.groupingBy(
                        PostImage::getPostId,
                        Collectors.mapping(PostImage::getImageUrl,Collectors.toList())
                ));

        // 이미지랑 post 병합..
        List<PostResponseDTO> response = posts.stream()
                .map(post -> {
                    List<String>images = imageMap.getOrDefault(post.getPostId(), new ArrayList<>());
                    return  PostResponseDTO.fromEntity(post,images);
                })
                .collect(Collectors.toList());

        return response;
    }


    // 유저의 해당 북클럽 전체 게시글 조회
    public List<PostResponseDTO> readUserPosts (Long userId, Long bookclubId){

        // post 조회
        List<Object[]> listAll = postRepository.findByUserIdAndBookClubId(userId, bookclubId);

        if(listAll.isEmpty()){
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
        List<PostImage> imageList = postImageRepository.findByUserIdAndBookClubId(userId,bookclubId);

        if(imageList.isEmpty()){
            System.out.println("등록한 이미지가 존재하지 않습니다.");
            return new ArrayList<>();
        }

        // post id 로 이미지 매칭
        Map<Long, List<String>> imgMap  = imageList.stream()
                .collect(Collectors.groupingBy(
                        PostImage::getPostId,
                        Collectors.mapping(PostImage::getImageUrl,Collectors.toList())
                ));

        // 이미지랑 게시글 병합
        List<PostResponseDTO> response = posts.stream()
                .map(post -> {
                    List<String>imgUrl = imgMap.getOrDefault(post.getPostId(),new ArrayList<>());
                    return PostResponseDTO.fromEntity(post, imgUrl);
                }).collect(Collectors.toList());

        return response;
    }

    // 수정할 때 특정 게시글 조회 하기 위함
    public PostDTO readOne(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow();
        PostDTO postOne = PostDTO.fromEntityForOne(post);
        if(postOne == null){
            throw new BaseException(BaseResponseStatus.POST_NOT_FOUND);
        }
        return postOne;
    }

    // 댓글 조회에서 사용될 게시글 조회
    public List<PostDTO> readForComment(Long postId){
        List<Object[]> enPost = postRepository.findByPost(postId);
        if(enPost==null){
            throw new BaseException(BaseResponseStatus.POST_NOT_FOUND);
        }
       List< PostDTO> post = enPost.stream().map(row ->{
            Post en = (Post) row[0];
            String userNickname = (String) row[1];
            String profilePath = (String) row[2];
            return PostDTO.fromEntity(en,userNickname,profilePath);
        }).collect(Collectors.toList());
        return post;
    }

    // 게시글 저장
    @Transactional
    public Boolean createPost(PostRequestDTO requestDTO) {
        try {
            Post post = Post.builder()
                    .userId(requestDTO.getUserId())
                    .postContent(requestDTO.getPostContent())
                    .bookClubId(requestDTO.getBookClubId())
                    .build();

            postRepository.save(post);  // 게시글 저장...

            Long postId = post.getPostId(); // 저장 후 해당 postId 얻기

            if (requestDTO.getImageUrl() != null) {
                for (String imageUrl : requestDTO.getImageUrl()) {
                    PostImage image = PostImage.builder()
                            .postId(postId)
                            .imageUrl(imageUrl)
                            .build();
                    postImageRepository.save(image);
                }
                return true;
            }
        }catch (Exception e){
            System.out.println("등록 중 오류 발생"+ e.getMessage());
        }
        return false;
    }


    //게시글 수정
    @Transactional
    public boolean update (PostDTO postDTO){
        try {
            Post post = postDTO.toEntity();
            if(post ==null){
                throw new BaseException(BaseResponseStatus.INVAILD_POST);
            }
            postRepository.save(post);
            return true;
        }catch (Exception e){
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

            // 4. post 삭제
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
