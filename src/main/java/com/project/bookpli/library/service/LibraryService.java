package com.project.bookpli.library.service;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.book.repository.BookRepository;
import com.project.bookpli.common.exception.BaseException;
import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.common.response.BaseResponseStatus;
import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.BookLike;
import com.project.bookpli.entity.Library;
import com.project.bookpli.library.dto.BookLikeDTO;
import com.project.bookpli.library.dto.LibraryResponseDTO;
import com.project.bookpli.library.repository.BookLikeRepository;
import com.project.bookpli.library.repository.LibraryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;
    private final BookLikeRepository bookLikeRepository;

    public LibraryService(LibraryRepository libraryRepository, BookRepository bookRepository, BookLikeRepository bookLikeRepository) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
        this.bookLikeRepository = bookLikeRepository;
    }

    public List<LibraryResponseDTO> getMyLibrary(Long userId) {
        return libraryRepository.getMyLibrary(userId);
    }

    @Transactional
    public void deleteMyLibrary(Long userId, Long libraryId) {
        libraryRepository.deleteByLibraryIdAndUserId(userId, libraryId);
    }

    @Transactional
    public LibraryResponseDTO addWishlist(Long userId, String isbn13) {

        // 이미 담은 책인지 확인
        Optional<Library> existingWishBook = libraryRepository.findByUserIdAndBook_Isbn13(userId, isbn13);
        if (existingWishBook.isPresent()) {
            // 중복된 요청이 있을 경우 기존 bookLikeId를 반환
            throw new BaseException(BaseResponseStatus.LIBRARY_ALREADY_EXIST);
        }

        // Book 객체 조회
        Book book = bookRepository.findById(isbn13)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.BOOK_NOT_FOUND));

        // Library 저장
        Library library = Library.builder()
                .userId(userId)
                .book(book)
                .status("wished") // 기본값 설정
                .build();

        Library savedLibrary = libraryRepository.save(library);
        return LibraryResponseDTO.fromEntity(savedLibrary, book);
    }


    public LibraryResponseDTO getMyOneLibrary(Long userId, String isbn13) {
        LibraryResponseDTO libraryResponseDTO = libraryRepository.getMyOneLibrary(userId, isbn13);
        return libraryResponseDTO;
    }



    @Transactional
    public Long addBookLike(Long userId, String isbn13) {
        // 이미 좋아요한 기록이 있는지 확인
        Optional<BookLike> existingLike = bookLikeRepository.findByUserIdAndBook_Isbn13(userId, isbn13);
        if (existingLike.isPresent()) {
            // 중복된 요청이 있을 경우 기존 bookLikeId를 반환
            return existingLike.get().getBookLikeId();
        }

        // BookLike 엔티티 생성 및 저장
        Book book = bookRepository.findById(isbn13)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.BOOK_NOT_FOUND));


        BookLike bookLike = BookLike.builder()
                .userId(userId)
                .book(book)
                .build();

        return bookLikeRepository.save(bookLike).getBookLikeId();
    }

    @Transactional
    public void deleteBookLike(Long bookLikeId) {
        bookLikeRepository.findById(bookLikeId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.BOOK_LIKE_ID_NOT_FOUND));
        bookLikeRepository.deleteById(bookLikeId);
    }

    public List<BookLikeDTO> getBookLikesByUserId(Long userId) {
        bookLikeRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        List<BookLike> bookLikes = bookLikeRepository.findAllByUserId(userId);

        return bookLikes.stream()
                .map(bookLike -> BookLikeDTO.fromEntity(bookLike, bookLike.getBook()))
                .collect(Collectors.toList());
    }

    public Long getBookLike(Long userId, String isbn13) {
        Optional<BookLike> response = bookLikeRepository.findByUserIdAndBook_Isbn13(userId, isbn13);
        return response.map(BookLike::getBookLikeId).orElse(null);
    }

}
