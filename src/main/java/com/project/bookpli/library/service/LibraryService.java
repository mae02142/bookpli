package com.project.bookpli.library.service;

import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.book.repository.BookRepository;
import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.Library;
import com.project.bookpli.library.dto.LibraryResponseDTO;
import com.project.bookpli.library.repository.LibraryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class LibraryService {
    private final LibraryRepository libraryRepository;
    private final BookRepository bookRepository;

    public LibraryService(LibraryRepository libraryRepository, BookRepository bookRepository) {
        this.libraryRepository = libraryRepository;
        this.bookRepository = bookRepository;
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

}
