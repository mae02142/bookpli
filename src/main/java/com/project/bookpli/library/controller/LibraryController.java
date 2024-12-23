package com.project.bookpli.library.controller;

import com.project.bookpli.auth.service.CustomPrincipal;
import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.library.dto.BookLikeDTO;
import com.project.bookpli.library.dto.LibraryResponseDTO;
import com.project.bookpli.library.service.LibraryService;
import com.project.bookpli.miniroom.dto.LibraryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/library")
public class LibraryController {
    private final LibraryService libraryService;

    /* 담기 기능 */
    @GetMapping
    public BaseResponse<List<LibraryResponseDTO>> getMyLibrary(@AuthenticationPrincipal CustomPrincipal principal){
        List<LibraryResponseDTO> response = libraryService.getMyLibrary(principal.getUserId());
        return new BaseResponse<>(response);
    }

    @GetMapping("{userId}/{isbn13}")
    public BaseResponse<LibraryResponseDTO> getMyOneLibrary(@PathVariable Long userId, @PathVariable String isbn13){
        LibraryResponseDTO response = libraryService.getMyOneLibrary(userId, isbn13);
        return new BaseResponse<>(response);
    }

    @PostMapping("{isbn13}")
    public BaseResponse<LibraryResponseDTO> addWishlist(@PathVariable String isbn13, @AuthenticationPrincipal CustomPrincipal principal){
        LibraryResponseDTO response = libraryService.addWishlist(principal.getUserId(), isbn13);
        return new BaseResponse<>(response);
    }

    @DeleteMapping("{libraryId}")
    public BaseResponse<Void> deleteMyLibrary(@PathVariable Long libraryId, @AuthenticationPrincipal CustomPrincipal principal){
        System.out.println("오 신기해!!!! : " + principal.getUserId());
        libraryService.deleteMyLibrary(principal.getUserId(), libraryId);
        return new BaseResponse<>();
    }

    /* 좋아요 기능 */
    @GetMapping("/book/{isbn13}")
    public BaseResponse<Long> getBookLike(@PathVariable String isbn13, @AuthenticationPrincipal CustomPrincipal principal){
        Long response = libraryService.getBookLike(principal.getUserId(), isbn13);
        return new BaseResponse<>(response);
    }

    @GetMapping("/book-like")
    public BaseResponse<List<BookLikeDTO>> getBookLikesByUserId(@AuthenticationPrincipal CustomPrincipal principal){
        List<BookLikeDTO> response = libraryService.getBookLikesByUserId(principal.getUserId());
        return new BaseResponse<>(response);
    }

    @PostMapping("/book-like/{isbn13}")
    public BaseResponse<Long> addBookLike(@PathVariable String isbn13, @AuthenticationPrincipal CustomPrincipal principal){
        Long response = libraryService.addBookLike(principal.getUserId(), isbn13);
        return new BaseResponse<>(response);
    }

    @DeleteMapping("/book-like/{bookLikeId}")
    public BaseResponse<Void> deleteBookLike(@PathVariable Long bookLikeId){
        libraryService.deleteBookLike(bookLikeId);
        return new BaseResponse<>();
    }
}
