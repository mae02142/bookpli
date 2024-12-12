package com.project.bookpli.comment.service;

import com.project.bookpli.comment.dto.CommentDTO;
import com.project.bookpli.comment.repository.CommentLikeRepository;
import com.project.bookpli.comment.repository.CommentRepository;
import com.project.bookpli.common.exception.BaseException;
import com.project.bookpli.common.response.BaseResponseStatus;
import com.project.bookpli.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

            // 게시글에 대한 전체 댓글 조회
    public List<CommentDTO> readCommentByPost(Long postId){
        List<Object[]> list = commentRepository.findByPost(postId);

        if(list.isEmpty()){
            log.info("게시글에 대한 댓글이 존재하지 않습니다");
            return new ArrayList<>();
        }

        List<CommentDTO> comments = list.stream()
                .map(row ->{
                    Comment comment = (Comment) row[0];
                    String userNickname = (String) row[1];
                    String profilePath = (String) row[2];
                    return CommentDTO.fromEntity(comment,userNickname,profilePath);
                }).collect(Collectors.toList());

            return comments;
    }


                // 유저가 등록한 댓글 전체 조회
    public List<CommentDTO> readCommentByUser (Long userId, Long bookclubId){
        List<Object[]> list = commentRepository.findByUserId(userId, bookclubId);

        if(list.isEmpty()){
            log.info("게시글에 대한 댓글이 존재하지 않습니다");
            return new ArrayList<>();
        }

        List<CommentDTO> comments = list.stream()
                .map(row -> {
                    Comment comment = (Comment) row[0];
                    String userNickname = (String) row[1];
                    String profilePath = (String) row[2];
                    Long bookClubId = (Long) row[3];
                    return CommentDTO.fromEntityBookClub(comment,userNickname,profilePath, bookClubId);
                }).collect(Collectors.toList());

            return comments;
    }

            // 댓글 등록
    public boolean insert (CommentDTO commentDTO){

        if(commentDTO == null) {
            log.error("잘못된 입력이 있습니다.");
            throw new BaseException(BaseResponseStatus.INVAILD_COMMENT);
        }
        Comment comment = commentDTO.toEntity();
        commentRepository.save(comment);

        return true;
    }

            //  댓글 수정
    public boolean update (CommentDTO commentDTO){

        if(commentDTO == null){
            log.error("잘못된 입력이 있습니다.");
            throw new BaseException(BaseResponseStatus.INVAILD_COMMENT);
        }

        Comment comment = commentDTO.toEntity();
        commentRepository.save(comment);

        return true;
    }

            // 댓글 삭제
    public boolean delete (Long commentId){
        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.COMMENT_NOT_FOUND));

        if(existing != null){
            log.info("댓글을 삭제하겠습니다.");
            boolean removeLike = commentLikeRepository.deleteByCommentId(commentId);
            if(removeLike) {
                commentRepository.delete(existing);
            }
        }
        return true;
    }
}
