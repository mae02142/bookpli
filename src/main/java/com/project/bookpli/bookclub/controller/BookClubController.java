package com.project.bookpli.bookclub.controller;

import com.project.bookpli.bookclub.dto.BookClubDTO;
import com.project.bookpli.bookclub.service.BookClubService;
import com.project.bookpli.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookclub")
public class BookClubController {

    private final BookClubService bookClubService;

    private final String TTB_KEY = "ttbsumini0911820002";
    private final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";



//    //북적북적 검색바
//    @GetMapping("/search/{key}")
//    public BaseResponse<List<BookClubDTO>> searchList(@PathVariable String key){
//        try {
//            List<BookClubDTO>bookClubDTOList = bookClubService.findBookClubList(key);
//            return new BaseResponse<>(bookClubDTOList);
//        }catch (Exception e){
//            log.error("컨트롤러: 도서리스트를 가져오는데 오류가 발생하였습니다."+ e.getMessage());
//            return new BaseResponse<>(new ArrayList<>());
//        }
//    }

            //북클럽 생성
    @PostMapping("/create")
    public BaseResponse<Void> createBookClub(@RequestParam String isbn){
        try {
            bookClubService.createBookClub(isbn);
            return new BaseResponse<>();
        }catch (Exception e){
            log.error("컨트롤러 : 북클럽 생성 중 오류 발생 " + e.getMessage());
            return new BaseResponse<>();
        }
    }

            //북클럽 검색
    @GetMapping("/search")
    public List<Map<String, Object>>searchBookclub(@RequestParam String keyword){

        RestTemplate restTemplate = new RestTemplate();

        String url = BASE_URL + "?ttbkey=" + TTB_KEY +
                "&Query=" + keyword +
                "&QueryType=Title" +  // 제목 기준 검색
                "&MaxResults=10&start=1" +
                "&SearchTarget=Book&output=js&Version=20131101";

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if(response!=null && response.containsKey("item")){
                //  도서 리스트 조회
                List<Map<String, Object>>list = (List<Map<String, Object>>) response.get("item");

                return  list.stream().map(items-> Map.of(
                        "isbn13",items.get("isbn13"),
                        "title",items.get("title"),
                        "author",items.get("author"),
                        "cover" ,items.get("cover")
                        )).collect(Collectors.toList());
            }
        }catch (Exception e){
            throw new RuntimeException("도서 호출 응답 실패" + e.getMessage());
        }
        return List.of(); // 검색결과 없으면 빈 리스트 출력
    }
}
