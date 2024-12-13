package Repository;

import com.project.bookpli.BookpliApplication;
import com.project.bookpli.review.dto.ReviewDTO;
import com.project.bookpli.review.repository.ReviewRepository;
import com.project.bookpli.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;


import java.util.Arrays;
import java.util.List;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = BookpliApplication.class)
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository repo;

    private ReviewDTO dto;

    @BeforeEach
    void pre() {
        System.out.println("######################################################");
    }

    @BeforeEach
    void setup () {
        // 테스트를 위한 dto 객체 생성
        dto = ReviewDTO.builder()
                .reviewId(4L)
                .userId(4L)
                .isbn13("123456789")
                .reviewContent("흥미로운 내용이 많습니다")
                .rating(4)
                .build();
    }

    @Test
    void findByUserId() {
        List<Object[]> result = repo.findByUserId(1L); // 테스트할 userId 입력
        result.forEach(row -> System.out.println(Arrays.toString(row)));
    }

    @Transactional
    @Test
    void insert (){
        // DTO -> Entity 변환
        Review review = dto.toEntity();

        // insert
        Review saved = repo.save(review);

        // insert 검증
        List<Object[]> rev = repo.findByUserId(saved.getUserId());
        System.out.println(rev);
        assertThat(rev).isNotEmpty(); // 리스트가 비어있는지 확인 true or false
    }

    @Transactional
    @Test
    void  delete () {
          repo.deleteById(2L);
          List<Review> rev = repo.findAll();
          assertThat(rev).isEmpty();
          rev.stream().forEach(System.out::println);
    }
}
