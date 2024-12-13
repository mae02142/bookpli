package com.project.bookpli.library.controller;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.library.dto.LibraryResponseDTO;
import com.project.bookpli.library.service.LibraryService;
import com.project.bookpli.mypage.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @DeleteMapping
    public BaseResponse<Void> deleteMyLibrary(@RequestBody Map<String, Long> request){
        libraryService.deleteMyLibrary(request.get("userId"), request.get("libraryId"));
        return new BaseResponse<>();
    }

    @PostMapping("{userId}")
    public BaseResponse<Long> addWishlist(@PathVariable Long userId, @RequestBody BookDTO request){
        Long response = libraryService.addWishlist(userId, request);
        return new BaseResponse<>(response);
    }
}
