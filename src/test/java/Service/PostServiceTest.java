package Service;

import com.project.bookpli.BookpliApplication;
import com.project.bookpli.post.dto.PostDTO;
import com.project.bookpli.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = BookpliApplication.class)
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @BeforeEach
    void pre(){
        System.out.println("#######################################");
    }

    @Transactional
    @Test
    void insert() {
        PostDTO dto = PostDTO.builder()
                .userId(4L)
                .bookClubId(9L)
                .postContent("이 책을 보고 많은 생각이 들었다. 점점 방어적인 사회가 되어가는 지금 그 어떠한 때보다 필요한 말들이 아닌가 싶다.")
                .build();
        postService.save(dto);

        List<PostDTO>list = postService.readUserPosts(4L,9L);
        list.stream().forEach(System.out::println);
    }
}
