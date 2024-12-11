package Repository;


import com.project.bookpli.BookpliApplication;
import com.project.bookpli.entity.Post;
import com.project.bookpli.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = BookpliApplication.class)
public class PostRepositoryTest {

    @Autowired
    private PostRepository repo;

    @BeforeEach
    void pre(){
        System.out.println("*********************************************************");
    }

    @Test
    void findPostAll(){
        Long club = 1L;
        List<Object[]> list = repo.findByBookClubId(club);

        if( list.isEmpty()){
            System.out.println("북클럽에 게시글이 존재하지 않습니다");
        }
        list.stream().forEach(System.out::println);
    }

    @Test
    void findUserPostAll (){
        Long user = 3L;
        Long club = 1L;
        List<Object[]> list = repo.findByUserIdAndBookClubId(user,club);

        if (list.isEmpty()){
            System.out.println("리스트가 비어있습니다");
        }
        list.stream().forEach(System.out::println);
    }

}
