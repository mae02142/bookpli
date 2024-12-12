package Repository;


import com.project.bookpli.BookpliApplication;
import com.project.bookpli.comment.dto.CommentDTO;
import com.project.bookpli.comment.repository.CommentRepository;
import com.project.bookpli.entity.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = BookpliApplication.class)
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void pre(){
        System.out.println("################################################");
    }

    @Test
    void findAll(){
        List<Object[]>list = commentRepository.findByPost(1L);
        List<CommentDTO>dto =list.stream().map(row -> {
            Comment comment = (Comment) row[0];
            String userNickname = (String) row[1];
            String profilepath = (String) row[2];
            return CommentDTO.fromEntity( comment,userNickname,profilepath);
                }).collect(Collectors.toList());

        dto.stream().forEach(System.out::println);
    }

    @Test
    void findMine(){
        List<Object[]>list = commentRepository.findByUserId(2L, 1L);
        List<CommentDTO>dto =list.stream().map(row -> {
            Comment comment = (Comment) row[0];
            String userNickname = (String) row[1];
            String profilePath = (String) row[2];
            Long bookclubId = (Long) row[3];
            return CommentDTO.fromEntityBookClub(comment,userNickname,profilePath,bookclubId);
                }).collect(Collectors.toList());

        dto.stream().forEach(System.out::println);
    }
}
