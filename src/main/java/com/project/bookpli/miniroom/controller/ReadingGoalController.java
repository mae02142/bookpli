package com.project.bookpli.miniroom.controller;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.book.repository.BookRepository;
import com.project.bookpli.common.exception.BaseException;
import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.common.response.BaseResponseStatus;
import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.Library;
import com.project.bookpli.library.dto.LibraryResponseDTO;
import com.project.bookpli.library.repository.LibraryRepository;
import com.project.bookpli.miniroom.dto.LibraryDTO;
import com.project.bookpli.miniroom.service.BookApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/goal")
public class ReadingGoalController {

    @Autowired
    private BookRepository bookrep;

    @Autowired
    private LibraryRepository libraryrep;

    @Autowired
    private BookApiService service;


    @PutMapping("/register/{isbn13}")
    public ResponseEntity<String> bookGoal(@PathVariable String isbn13,
                                           @RequestBody LibraryDTO libraryDTO) {

        System.out.println("요청 데이터: " + libraryDTO);
        System.out.println("PathVariable isbn13: " + isbn13);

        // 요청 데이터 검증
        if (!isbn13.equals(libraryDTO.getIsbn13())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ISBN 번호가 일치하지 않습니다.");
        }

        try {
            // ISBN 존재 여부 확인
            Optional<Library> existingLibrary = libraryrep.findByUserIdAndBook_Isbn13(libraryDTO.getUser_id(), isbn13);

            // 존재하지 않으면 새로 추가
            if (existingLibrary.isEmpty()) {
                // Book 엔티티 조회 또는 생성
                Book book = bookrep.findById(isbn13)
                        .orElseThrow(() -> new BaseException(BaseResponseStatus.BOOK_NOT_FOUND));

                // Library 엔티티 생성
                Library library = Library.builder()
                        .userId(libraryDTO.getUser_id())
                        .book(book)
                        .status("wished") // 기본 상태
                        .build();
                libraryrep.save(library);


            }

            // 독서 목표 업데이트 실행
            int update = libraryrep.setReadGoal(
                    isbn13,
                    libraryDTO.getStartDate(),
                    libraryDTO.getEndDate(),
                    libraryDTO.getStatus()
            );

            if (update > 0) {
                return ResponseEntity.ok("독서목표가 설정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("독서 목표 설정 실패: 조건에 맞는 데이터가 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러 발생: " + e.getMessage());
        }
    }

    //독서 목표 해제
    @DeleteMapping("/{isbn13}")
    public ResponseEntity<String> dropReading(@PathVariable String isbn13,
                                              @RequestParam String status) {
        int stopRead = libraryrep.changeStatus(isbn13, status);

        if (stopRead > 0) {
            return ResponseEntity.ok("독서목표가 해제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("독서 목표 해제 실패");
        }
    }

    //독서 기간 수정
    @PutMapping("/reset/{isbn13}")
    public ResponseEntity<Integer> resetDate(@PathVariable String isbn13,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        int setPeriod = libraryrep.updateDate(isbn13,startDate,endDate);
        return ResponseEntity.ok(setPeriod);
    }

}
