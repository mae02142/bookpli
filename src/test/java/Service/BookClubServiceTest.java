package Service;


import com.project.bookpli.BookpliApplication;
import com.project.bookpli.bookclub.dto.BookClubDTO;
import com.project.bookpli.bookclub.repository.BookClubRepository;
import com.project.bookpli.bookclub.service.BookClubService;
import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.BookClub;
import com.project.bookpli.miniroom.repository.BookRepository;
import com.project.bookpli.miniroom.service.BookApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = BookpliApplication.class)
public class BookClubServiceTest {

    @Autowired
    private BookClubService bookClubService;

    @Autowired
    private BookApiService bookApiService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookClubRepository bookClubRepository;

    @BeforeEach
    void pre(){
        System.out.println("###########################################");
    }


    @Transactional
    @Test
    void createBookclub() {
        String isbn = "9788983925558";

        // 우선 db에 도서가 존재하는지 확인
        Optional<Book> book = bookRepository.findById(isbn);

        if(!book.isPresent()){
            bookApiService.searchBook(isbn); //없으면 api로 찾아와서 저장
            book = bookRepository.findById(isbn);
        }
        assertNotNull(book); // 도서 정보 존재하는지 확인

        bookClubService.createBookClub(isbn); //북클럽 생성

        BookClub bookClub = bookClubRepository.findBookClubByIsbn(isbn);
        System.out.println(bookClub);
        // 생성 확인
    }

    @Test
    void find () {
        String isbn = "9788983927620";

        //해당 북클럽이 존재하는지 확인
        BookClubDTO bookClubDTO = bookClubService.findBookClub(isbn);

        System.out.println(bookClubDTO);

    }

    @Test
    void findList (){
        String key  = "해리";

        List<BookClubDTO> list = bookClubService.findBookClubList(key);

        list.stream().forEach(System.out::println);
    }
}
