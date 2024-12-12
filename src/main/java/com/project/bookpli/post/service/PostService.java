package com.project.bookpli.post.service;

import com.project.bookpli.comment.repository.CommentLikeRepository;
import com.project.bookpli.comment.repository.CommentRepository;
import com.project.bookpli.comment.service.CommentService;
import com.project.bookpli.common.exception.BaseException;
import com.project.bookpli.common.response.BaseResponseStatus;
import com.project.bookpli.entity.Comment;
import com.project.bookpli.entity.Post;
import com.project.bookpli.post.dto.PostDTO;
import com.project.bookpli.post.repository.PostLikeRepository;
import com.project.bookpli.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostLikeRepository postLikeRepository;
    // 해당 북클럽의 전체 게시글 조회
    public List<PostDTO> readClubPosts (Long bookclubId){

        List<Object[]> listAll = postRepository.findByBookClubId(bookclubId);

        if(listAll.isEmpty()){
            System.out.println("리스트가 비어있습니다");
            return new ArrayList<>();
        }
        // Object[]를 PostDTO로 변환
        return listAll.stream()
                .map(row -> {
                    Post post = (Post) row[0];
                    String nickname = (String) row[1];
                    String profilePath = (String) row[2];
                    return PostDTO.fromEntity(post, nickname, profilePath);
                })
                .collect(Collectors.toList());
    }


    // 유저의 해당 북클럽 전체 게시글 조회
    public List<PostDTO> readUserPosts (Long userId, Long bookclubId){

        List<Object[]> listAll = postRepository.findByUserIdAndBookClubId(userId, bookclubId);

        if(listAll.isEmpty()){
            System.out.println("작성한 글이 없습니다");
            return new ArrayList<>();
        }

        return listAll.stream()
                .map(row -> {
                    Post post = (Post) row[0];
                    String nickname = (String) row[1];
                    String profilePath = (String) row[2];
                    return PostDTO.fromEntity(post, nickname, profilePath);
                })
                .collect(Collectors.toList());
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

    // 게시글 등록
    @Transactional
    public boolean save(PostDTO postDTO){

        try {
            if(postDTO == null){
                throw new BaseException(BaseResponseStatus.INVAILD_POST);
            }

            Post post = postDTO.toEntity();
            postRepository.save(post);
            System.out.println("서비스단: 게시글 등록 성공");

            return true;

        }catch (Exception e){
            System.out.println("서비스단: 오류 발생");
            e.printStackTrace();
            return false;
        }
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
