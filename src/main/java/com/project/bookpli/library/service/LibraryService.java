package com.project.bookpli.library.service;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.book.repository.BookRepository;
import com.project.bookpli.common.exception.BaseException;
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
    public Long addWishlist(Long userId, BookDTO request) {
        String isbn13 = request.getIsbn13();

        Book book = bookRepository.findById(isbn13)
                .orElseGet(() -> {
                    // Book 엔티티 생성 및 저장
                    Book newBook = BookDTO.toEntity(request);
                    return bookRepository.save(newBook);
                });

        // Library 저장
        Library library = Library.builder()
                .userId(userId)
                .book(book)
                .status("wished") // 기본값 설정
                .build();

        Library savedLibrary = libraryRepository.save(library);
        return savedLibrary.getLibraryId();
    }

    @Transactional
    public Long addBookLike(Long userId, BookDTO request) {
        // 이미 좋아요한 기록이 있는지 확인
        Optional<BookLike> existingLike = bookLikeRepository.findByUserIdAndIsbn13(userId, request.getIsbn13());
        if (existingLike.isPresent()) {
            // 중복된 요청이 있을 경우 기존 bookLikeId를 반환
            return existingLike.get().getBookLikeId();
        }

        // 책이 존재하는지 확인 후 없으면 저장
        Book book = bookRepository.findById(request.getIsbn13())
                .orElseGet(() -> {
                    Book newBook = BookDTO.toEntity(request);
                    return bookRepository.saveAndFlush(newBook); // 즉시 저장 및 플러시
                });

        // BookLike 엔티티 생성 및 저장
        BookLike bookLike = BookLike.builder()
                .userId(userId)
                .isbn13(book.getIsbn13())
                .build();

        return bookLikeRepository.save(bookLike).getBookLikeId();
    }

    @Transactional
    public void deleteBookLike(Long bookLikeId) {
        bookLikeRepository.findById(bookLikeId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.BOOK_LIKE_ID_NOT_FOUND));
        bookLikeRepository.deleteById(bookLikeId);
    }

    public List<BookLikeDTO> getUserBookLike(Long userId) {
        bookLikeRepository.findById(userId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));

        return bookLikeRepository.findAllByUserIdWithBookInfo(userId);
    }

    public Long getBookLike(Long userId, String isbn13) {
        Optional<BookLike> response = bookLikeRepository.findByUserIdAndIsbn13(userId, isbn13);
        return response.map(BookLike::getBookLikeId).orElse(null);
    }
}
