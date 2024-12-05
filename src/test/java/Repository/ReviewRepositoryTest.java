package Repository;

import com.project.bookpli.auth.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class ReviewRepositoryTest {

    private ReviewRepository repo;

    @BeforeEach
    void pre() {
        System.out.println("######################################################");
    }



}
