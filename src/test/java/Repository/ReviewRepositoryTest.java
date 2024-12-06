package Repository;

import com.project.bookpli.BookpliApplication;
import com.project.bookpli.bookclub.dto.ReviewDTO;
import com.project.bookpli.bookclub.repository.ReviewRepository;
import com.project.bookpli.entity.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;


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
        dto = new ReviewDTO();
        dto.setReviewId(2L);
        dto.setUserId(3L);
        dto.setIsbn13("9781234567890");
        dto.setReviewContent("유익한 내용");
        dto.setRating(4);

    }

    @Test
    void findByUserId () {
        List<Review> rv = repo.findByUserId(3);
        rv.stream().forEach(System.out::println);
    }

    @Test
    void findByIsbn () {
        List<Review> rv = repo.findByIsbn("9781234567890");
        rv.stream().forEach(System.out::println);
    }

    @Transactional
    @Test
    void insert (){
        // DTO -> Entity 변환
        Review review = dto.toEntity();

        // insert
        Review saved = repo.save(review);

        // insert 검증
        List<Review> rev = repo.findByUserId(saved.getUserId());
        System.out.println(rev);
        assertThat(rev).isNotEmpty(); // 리스트가 비어있는지 확인 true or false
        assertThat(rev.get(0).getReviewContent()).isEqualTo("유익한 내용"); //첫번째 리뷰 값과 비교

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
