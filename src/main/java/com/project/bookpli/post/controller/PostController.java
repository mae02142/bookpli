package com.project.bookpli.post.controller;

import com.project.bookpli.common.response.BaseResponse;

import com.project.bookpli.post.dto.PostDTO;
import com.project.bookpli.post.dto.PostRequestDTO;
import com.project.bookpli.post.dto.PostResponseDTO;
import com.project.bookpli.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/post")
@RestController
public class PostController {

    private final PostService postService;


        // 해당 북클럽 전체 게시글 조회
    @GetMapping("/bookclubs")
    public BaseResponse<List<PostResponseDTO>> readPosts(@RequestParam  Long bookclubId){
       try {
         List<PostResponseDTO>list = postService.readClubPosts(bookclubId);
           System.out.println(list);
         return new BaseResponse<>(list);
       }catch (Exception e){
           log.error("컨트롤 : 에러가 발생했습니다");
           e.printStackTrace();
           return new BaseResponse<>();
       }
    }

            // 유저가 등록한 게시글 조회
    @GetMapping("/bookclub/mypost")
    public BaseResponse<List<PostResponseDTO>> readMyPost(@RequestParam Long userId, @RequestParam Long bookClubId){
     try{
         List<PostResponseDTO> myPost = postService.readUserPosts(userId,bookClubId);
         System.out.println(myPost);
         return new BaseResponse<>(myPost);
     }catch (Exception e){
         log.error("컨트롤러 : 오류발생 ");
         e.printStackTrace();
         return new BaseResponse<>();
     }
    }

        // 수정을 위한 데이터 조회
    @GetMapping("/getOne")
    public BaseResponse<PostResponseDTO> readOne (@RequestParam Long postId) {
        try{
            PostResponseDTO post = postService.readOne(postId);
          log.info("게시글을 불러옵니다");
          return new BaseResponse<>(post);
        }catch (Exception e){
            log.error("컨트롤 : 에러 발생");
            return new BaseResponse<>();
        }
    }

    @GetMapping("/comment/readOne")
    public BaseResponse<PostResponseDTO>postForComment (@RequestParam Long postId){
        try {
            PostResponseDTO post = postService.readForComment(postId);
            log.info("게시글을 불러옵니다");
            System.out.println(post);
            return new BaseResponse<>(post);
        }catch (Exception e){
            log.error("컨트롤: 에러발생");
            e.printStackTrace();
            return new BaseResponse<>();
        }
    }

        //게시글 등록
    @PostMapping("/insert")
    public BaseResponse<Boolean> postInsert(@RequestBody PostRequestDTO requestDTO){
        try {
            // 게시글 저장 후 id 반환
             boolean result = postService.createPost(requestDTO);
             return new BaseResponse<>(result);
        }catch (Exception e){
            log.error("컨트롤러 : 등록 중 오류 발생 ");
            e.printStackTrace();
            return new BaseResponse<>(false);
        }
    }

            // 게시글 수정
    @PutMapping("/edit")
    public BaseResponse<Boolean> postEdit(@RequestBody PostRequestDTO postRequestDTO){
        try {
            System.out.println("수정하려는 게시글 : " + postRequestDTO.getPostId());
            boolean result = postService.update(postRequestDTO);
            if (!result) {
                throw new IllegalArgumentException("수정하는데 실패하였습니다");
            }
            log.info("수정 성공");
            return new BaseResponse<>(result);
        }catch (Exception e){
            log.error("에러 발생");
            e.printStackTrace();
            return new BaseResponse<>(false);
        }
    }

    // 게시글 삭제
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> postRemove(@RequestParam Long postId){
        try {
            System.out.println("삭제하려는 게시글 : "+ postId);
            boolean result = postService.delete(postId);
            if(!result){
                throw new IllegalArgumentException("게시글 삭제 실패");
            }
            log.info("삭제 성공");
            return new BaseResponse<>(result);
        }catch (Exception e){
            log.error("컨트롤러 : 에러 발생!");
            e.printStackTrace();
            return new BaseResponse<>(false);
        }
    }
}
