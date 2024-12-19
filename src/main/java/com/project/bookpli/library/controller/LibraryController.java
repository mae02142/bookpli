package com.project.bookpli.library.controller;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.library.dto.BookLikeDTO;
import com.project.bookpli.library.dto.LibraryResponseDTO;
import com.project.bookpli.library.service.LibraryService;
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

    @GetMapping("{userId}/book/{isbn13}")
    public BaseResponse<Long> getBookLike(@PathVariable Long userId, @PathVariable String isbn13){
        Long response = libraryService.getBookLike(userId, isbn13);
        return new BaseResponse<>(response);
    }

    @PostMapping("{userId}/book")
    public BaseResponse<Long> addBookLike(@PathVariable Long userId, @RequestBody BookDTO book){
        Long response = libraryService.addBookLike(userId, book);
        return new BaseResponse<>(response);
    }

    @DeleteMapping("/book-like/{bookLikeId}")
    public BaseResponse<Void> deleteBookLike(@PathVariable Long bookLikeId){
        libraryService.deleteBookLike(bookLikeId);
        return new BaseResponse<>();
    }

    @GetMapping("/book-like/{userId}")
    public BaseResponse<List<BookLikeDTO>> getUserBookLike(@PathVariable Long userId){
        List<BookLikeDTO> response = libraryService.getUserBookLike(userId);
        return new BaseResponse<>(response);
    }
}
