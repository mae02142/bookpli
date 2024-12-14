package Repository;


import com.project.bookpli.BookpliApplication;
import com.project.bookpli.entity.Post;
import com.project.bookpli.entity.PostImage;
import com.project.bookpli.post.dto.PostDTO;
import com.project.bookpli.post.dto.PostResponseDTO;
import com.project.bookpli.post.repository.PostImageRepository;
import com.project.bookpli.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = BookpliApplication.class)
public class PostRepositoryTest {

    @Autowired
    private PostRepository repo;

    @Autowired
    private PostImageRepository postImageRepository;

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

    @Test
    void setPostImage(){
        List<PostImage> list = postImageRepository.findByUserIdAndBookClubId(2L,1L);
        list.stream().forEach(System.out::println);

    }

}
