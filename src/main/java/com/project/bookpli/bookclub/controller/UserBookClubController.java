package com.project.bookpli.bookclub.controller;

import com.project.bookpli.bookclub.dto.UserBookClubDTO;
import com.project.bookpli.bookclub.service.BookClubService;
import com.project.bookpli.bookclub.service.UserBookClubService;
import com.project.bookpli.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/userbookclub")
@RestController
public class UserBookClubController {

    private final UserBookClubService userBookClubService;
    private final BookClubService bookClubService;


    // 유저의 북클럽 추가
    @Transactional
    @PostMapping("/add/bookclub")
    public BaseResponse<Boolean> addBookClub(@RequestParam Long userId, @RequestParam String isbn13){
        boolean result;
        try {
            result =userBookClubService.addBookClub(userId, isbn13);
            if(result) {
                log.info("북클럽 추가 성공!!");
            }else {
                log.info("이미 추가된 북클럽입니다.");
            }
            return new BaseResponse<>(result);

        }catch (Exception e){

            log.error("C :북클럽 추가 중 오류 발생");
            e.printStackTrace();
            result = false;
            return new BaseResponse<>(result);

        }
    }

    // 유저가 추가한 북클럽 조회
    @GetMapping("/mybookclubs")
    public BaseResponse<List<UserBookClubDTO>> myclubs (@RequestParam Long userId) {
        try {
            List<UserBookClubDTO> list = userBookClubService.readMyBookClub(userId);
            System.out.println(list);
            return new BaseResponse<>(list);
        }catch (Exception e){
            log.error("C :북클럽 리스트 불러오는 중 오류 발생");
            e.printStackTrace();
            return new BaseResponse<>();
        }
    }

    // 북클럽 리스트에서 삭제
    @DeleteMapping("/remove/myclub")
    public BaseResponse<Void>deleteMyclub (@RequestParam Long userClubId) {
        try {
            System.out.println("C : 삭제할 북클럽 id -> "+ userClubId);
            userBookClubService.deleteClub(userClubId);
            return new BaseResponse<>();
        }catch (Exception e){
            log.error("C :북클럽 삭제 중 오류 발생");
            e.printStackTrace();
            return new BaseResponse<>();
        }
    }

}
