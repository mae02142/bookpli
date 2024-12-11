package com.project.bookpli.post.service;

import com.project.bookpli.entity.Post;
import com.project.bookpli.post.dto.PostDTO;
import com.project.bookpli.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    // 해당 북클럽의 전체 게시글 조회
    public List<PostDTO> readClubPosts (Long bookclubId){

        List<Object[]> listAll = postRepository.findByBookClubId(bookclubId);

        if(listAll.isEmpty()){
            System.out.println("리스트가 비어있습니다");
            return new ArrayList<>();
        }
        // Object[]를 PostDTO로 변환
        return listAll.stream()
                .map(row -> {
                    Post post = (Post) row[0];
                    String nickname = (String) row[1];
                    String profilePath = (String) row[2];
                    return PostDTO.fromEntity(post, nickname, profilePath);
                })
                .collect(Collectors.toList());
    }


    // 유저의 해당 북클럽 전체 게시글 조회
    public List<PostDTO> readUserPosts (Long userId, Long bookclubId){

        List<Object[]> listAll = postRepository.findByUserIdAndBookClubId(userId, bookclubId);

        if(listAll.isEmpty()){
            System.out.println("작성한 글이 없습니다");
            return new ArrayList<>();
        }

        return listAll.stream()
                .map(row -> {
                    Post post = (Post) row[0];
                    String nickname = (String) row[1];
                    String profilePath = (String) row[2];
                    return PostDTO.fromEntity(post, nickname, profilePath);
                })
                .collect(Collectors.toList());
    }

    // 수정할 때 특정 게시글 조회 하기 위함
    public PostDTO readOne(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow();
        PostDTO postOne = PostDTO.fromEntityForOne(post);
        if(postOne == null){
            throw new NoSuchElementException("해당하는 게시글이 없습니다.");
        }
        return postOne;
    }

    // 게시글 등록
    @Transactional
    public boolean save(PostDTO postDTO){

        try {
            Post post = postDTO.toEntity();
            if(post == null){
                throw new NoSuchElementException("데이터가 없습니다.");
            }

            postRepository.save(post);
            System.out.println("서비스단: 게시글 등록 성공");

            return true;
        }catch (Exception e){
            System.out.println("서비스단: 오류 발생");
            e.printStackTrace();
            return false;
        }
    }

    //게시글 수정
    @Transactional
    public boolean update (PostDTO postDTO){
        try {
            Post post = postDTO.toEntity();
            if(post ==null){
                throw new NoSuchElementException("데이터가 존재하지 않습니다");
            }
            postRepository.save(post);
            return true;
        }catch (Exception e){
            System.out.println("서비스단 : 오류발생");
            e.printStackTrace();
            return false;
        }
    }

    // 게시글 삭제
    @Transactional
    public boolean delete (Long postId){
        try {
            Optional<Post> list = postRepository.findById(postId);
            if(list.isEmpty()){
               throw new NoSuchElementException("게시글이 존재하지 않습니다");
            }
            postRepository.deleteById(postId);

            Optional<Post> check = postRepository.findById(postId);
            return check.isEmpty();
        }catch (Exception e){
            System.out.println("서비스단: 오류 발생");
            e.printStackTrace();
            return false;
        }
    }
}
