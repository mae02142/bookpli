package com.project.bookpli.comment.controller;

import com.project.bookpli.comment.dto.CommentDTO;
import com.project.bookpli.comment.service.CommentService;
import com.project.bookpli.common.exception.BaseException;
import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.common.response.BaseResponseStatus;
import com.project.bookpli.entity.Comment;
import com.project.bookpli.entity.User;
import com.project.bookpli.mypage.dto.UserDTO;
import com.project.bookpli.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping ("/api/comment")
@RestController
public class CommentController {

    private final CommentService commentService;
    private final MypageService mypageService;

    // 유저 정보 가져오기
    @GetMapping("/info/{spotifyId}")
    public BaseResponse<UserDTO> getInfoForComment(@PathVariable String spotifyId){
        User info = mypageService.findBySpotifyId(spotifyId)
                .orElseThrow(()-> new BaseException(BaseResponseStatus.USER_NOT_FOUND));
        UserDTO userInfo = UserDTO.fromEntity(info);
        return new BaseResponse<>(userInfo);
    }

    // 게시글 댓글 조회
    @GetMapping("/post/{postId}")
    public BaseResponse<List<CommentDTO>> readAllByPost(@PathVariable Long postId){
        List<CommentDTO> comments = commentService.readCommentByPost(postId);
        return new BaseResponse<>(comments);
    }

    // 유저 전체 댓글 조회
    @GetMapping("/user")
    public BaseResponse<List<CommentDTO>> readAllByUser(@RequestParam Long userId, @RequestParam Long bookClubId){
        List<CommentDTO> comments = commentService.readCommentByUser(userId, bookClubId);
        return new BaseResponse<>(comments);
    }

    // 댓글 등록
    @PostMapping("/insert")
    public BaseResponse<Boolean> registerComment (@RequestBody CommentDTO commentDTO){
        try {
          boolean response= commentService.insert(commentDTO);
            log.info("댓글 수정 성공");
            return new BaseResponse<>(response);
        }catch (Exception e){
            log.error("등록 중 에러 발생");
            e.printStackTrace();
            return new BaseResponse<>(false);
        }
    }

    // 댓글 수정
    @PutMapping("/edit")
    public BaseResponse<Boolean> editComment (@RequestBody CommentDTO commentDTO){
        try{
            boolean response = commentService.update(commentDTO);
            log.info("댓글 수정 성공");
            return new BaseResponse<>(response);
        }catch (Exception e){
            log.error("등록 중 에러 발생");
            e.printStackTrace();
            return new BaseResponse<>(false);
        }
    }

    // 댓글 삭제
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteComment(@RequestParam Long commentId){
        try{
            boolean response = commentService.delete(commentId);
            log.info("삭제 성공");
            return new BaseResponse<>(response);
        } catch (Exception e) {
            log.error("삭제 중 오류 발생");
            e.printStackTrace();
            return new BaseResponse<>(false);
        }
    }
}
