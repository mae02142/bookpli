package com.project.bookpli.bookclub.service;

import com.project.bookpli.bookclub.dto.BookClubDTO;
import com.project.bookpli.bookclub.repository.BookClubRepository;
import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.BookClub;
import com.project.bookpli.book.repository.BookRepository;
import com.project.bookpli.miniroom.service.BookApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookClubService {

    private final BookClubRepository bookClubRepository;
    private final BookRepository bookRepository;
    private final BookApiService bookApiService;

    // 북클럽 생성
    @Transactional
    public void createBookClub(String isbn13) {
        try {

            //해당 도서가 있는지 체크
            bookApiService.searchBook(isbn13);
            Optional<Book> checkBook = bookRepository.findById(isbn13);

            if (checkBook.isEmpty()) {
                throw new NoSuchElementException("해당 도서를 찾을 수 없습니다");
            }

            // BookClub 존재 여부 확인
            BookClub existingClub = bookClubRepository.findBookClubByIsbn(isbn13);
            if (existingClub != null) {
                System.out.println("이미 존재하는 북클럽입니다.");
                return;
            }
            // BookClub 생성
            Book book = checkBook.get();
            BookClub bookClub = BookClub.builder()
                    .book(book)
                    .build();

            bookClubRepository.save(bookClub);
            System.out.println("새로운 북클럽 생성!");

        } catch (Exception e) {
            System.out.println("북클럽 생성 실패!!!!");
            e.printStackTrace();
        }
    }


        //  특정 북클럽 찾기
        public BookClubDTO findBookClub (String isbn13){

            //해당 북클럽이 존재하는지 확인
            BookClub getClub = bookClubRepository.findBookClubByIsbn(isbn13);

            BookClubDTO bookClub = null;
            if (getClub != null) {

                bookClub = BookClubDTO.fromEntity(getClub);

            }
            return bookClub;
        }


        // 북클럽 리스트 찾기
        public List<BookClubDTO> findBookClubList (String key){

            List<BookClub> list = bookClubRepository.findBookClubByName(key);
            if (list.isEmpty()) {
                throw new NoSuchElementException("검색어에 해당하는 도서가 없습니다");
            }
            List<BookClubDTO> bookclubs = list.stream()
                    .map(BookClubDTO::fromEntity)
                    .collect(Collectors.toList());

            return bookclubs;
        }
    }