package com.project.bookpli.library.controller;

import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.library.dto.LibraryResponseDTO;
import com.project.bookpli.library.service.LibraryService;
import com.project.bookpli.mypage.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/library")
public class LibraryController {
    private final LibraryService libraryService;

    @GetMapping("{userId}")
    public BaseResponse<List<LibraryResponseDTO>> getMyLibrary(@PathVariable Long userId){
        List<LibraryResponseDTO> response = libraryService.getMyLibrary(userId);
        return new BaseResponse<>(response);
    }
}
