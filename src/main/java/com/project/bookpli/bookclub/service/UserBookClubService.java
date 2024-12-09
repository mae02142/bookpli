package com.project.bookpli.bookclub.service;

import com.project.bookpli.bookclub.dto.BookClubDTO;
import com.project.bookpli.bookclub.dto.UserBookClubDTO;
import com.project.bookpli.bookclub.repository.UserBookClubRepository;
import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.BookClub;
import com.project.bookpli.entity.UserBookClub;
import com.project.bookpli.miniroom.repository.BookRepository;
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
public class UserBookClubService {

    private final UserBookClubRepository userBookClubRepository;
    private final BookClubService bookClubService;
    private final BookApiService bookApiService;
    private final BookRepository bookRepository;


            // 유저가 추가한 북클럽 조회
    public List<UserBookClubDTO> readMyBookClub(Long userId){
        List<UserBookClub> myClubs = userBookClubRepository.myBookClub(userId);

        if(myClubs.isEmpty()){
            throw new NoSuchElementException("해당 유저의 북클럽이 없습니다.");
        }

        return myClubs.stream()
                .map(UserBookClubDTO::fromEntity)
                .collect(Collectors.toList());
    }


                //  나의 북클럽 추가
    @Transactional
    public boolean addBookClub (Long userId , String isbn13){
        // 1. 북클럽을 이미 추가 했는지 확인
        boolean isAlreadyMem = userBookClubRepository.existsByUserIdAndBookClub_Book_Isbn13(userId,isbn13);

        if(isAlreadyMem){
            System.out.println("이미 추가한 북클럽입니다");
            return false;
        }

        // 2. db에 도서 존재 여부 확인
        boolean checkBook = bookRepository.existsById(isbn13);

        if(!checkBook){ // 없으면 생성
            bookApiService.searchBook(isbn13);
        }

        // 3. 북클럽 존재 여부 확인
        BookClubDTO bookClubDTO = bookClubService.findBookClub(isbn13);

        if(bookClubDTO == null) { //없으면 생성
            bookClubService.createBookClub(isbn13);
            bookClubDTO = bookClubService.findBookClub(isbn13);
            System.out.println("북클럽 생성 "+ bookClubDTO);
        }

        Optional<Book> getBook = bookRepository.findById(isbn13);

        Book book = getBook.get();
        BookClub bookClub = bookClubDTO.toEntity(book);
        // 4. 유저의 북클럽 추가
        try {
            System.out.println("유저의 북클럽 리스트에 추가하겠습니다");
            UserBookClub addClub = UserBookClub.builder()
                    .userId(userId)
                    .bookClub(bookClub)
                    .bookClubId(bookClub.getBookClubId())
                    .build();

            if(addClub != null) {
                userBookClubRepository.save(addClub);
                System.out.println("북클럽을 추가하였습니다.");
            }
            return true;
        }catch (Exception e){
            System.out.println("서비스단 : 나의 북클럽에 등록 중 오류 발생");
            e.printStackTrace();
            return false;
        }
    }


    // 북클럽 삭제
    @Transactional
    public void deleteClub(Long userClubId){
        try {
            System.out.println("삭제할 id : " + userClubId);
            userBookClubRepository.deleteByUserClubId(userClubId);
        }catch (Exception e){
            System.out.println("서비스단 : 북클럽 삭제 중 오류 발생");
            e.printStackTrace();
        }
    }
}
