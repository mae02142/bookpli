package com.project.bookpli.mypage.controller;

import com.project.bookpli.auth.dto.UserDTO;
import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MypageController {
    private final MypageService mypageService;

    @GetMapping("{userId}")
    public BaseResponse<UserDTO> getUserProfile(@PathVariable Long userId){
        UserDTO response = mypageService.getUserProfile(userId);
        return new BaseResponse<>(response);
    }

    @PatchMapping
    public BaseResponse<Void> patchNickName(@RequestBody Map<String, Object> request){
        mypageService.patchNickName(request);
        return new BaseResponse<>();
    }




}
