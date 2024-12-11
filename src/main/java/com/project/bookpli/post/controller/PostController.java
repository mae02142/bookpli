package com.project.bookpli.post.controller;

import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.post.dto.PostDTO;
import com.project.bookpli.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/post")
@RestController
public class PostController {

    private final PostService postService;

        // 해당 북클럽 전체 게시글 조회
    @GetMapping("/bookclubs")
    public BaseResponse<List<PostDTO>> readPosts(@RequestParam  Long bookclubId){
       try {
         List<PostDTO>list = postService.readClubPosts(bookclubId);
         if(list.isEmpty()){
             throw new NoSuchElementException("북클럽에 게시글이 존재하지 않습니다");
         }
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
    public BaseResponse<List<PostDTO>> readMyPost(@RequestParam Long userId, @RequestParam Long bookClubId){
     try{
         System.out.println("도착1");
         List<PostDTO> myPost = postService.readUserPosts(userId,bookClubId);
         System.out.println("도착2");
         if(myPost.isEmpty()){
             throw new NoSuchElementException("등록된 게시글이 없습니다");
         }
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
    public BaseResponse<PostDTO> readOne (@RequestParam Long postId) {
        try{
          PostDTO post = postService.readOne(postId);
          log.info("게시글을 불러옵니다");
          return new BaseResponse<>(post);
        }catch (Exception e){
            log.error("컨트롤 : 에러 발생");
            return new BaseResponse<>(null);
        }
    }


        //게시글 등록
    @PostMapping("/insert")
    public BaseResponse<Boolean> postInsert(@RequestBody PostDTO postDTO){
        try {
            System.out.println(postDTO.getBookClubId());
             boolean result = postService.save(postDTO);
             if(!result){
                return new BaseResponse<>(false);
             }
            return new BaseResponse<>(result);
        }catch (Exception e){
            log.error("컨트롤러 : 등록 중 오류 발생 ");
            e.printStackTrace();
            return new BaseResponse<>(false);
        }
    }

    @PutMapping("/edit")
    public BaseResponse<Boolean> postEdit(@RequestBody PostDTO postDTO){
        try {
            System.out.println("수정하려는 게시글 : " + postDTO.getPostId());
            boolean result = postService.update(postDTO);
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
