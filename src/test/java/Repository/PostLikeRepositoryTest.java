package Repository;

import com.project.bookpli.BookpliApplication;
import com.project.bookpli.entity.PostLike;
import com.project.bookpli.post.repository.PostLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = BookpliApplication.class)
public class PostLikeRepositoryTest {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @BeforeEach
    void  pre() {
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }

    @Test
    void getCounting(){
        int likes = postLikeRepository.countByPostId(5L);
        System.out.println(likes);
    }

    @Test
    void findUserLikes(){
        Optional<PostLike>likes = postLikeRepository.findByUserIdAndPostId(2L,1L);
        System.out.println(likes);
    }
}
