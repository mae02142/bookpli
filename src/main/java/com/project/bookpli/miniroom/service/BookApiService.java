package com.project.bookpli.miniroom.service;

import com.project.bookpli.entity.Book;
import com.project.bookpli.entity.Library;
import com.project.bookpli.miniroom.dto.BookDTO;
import com.project.bookpli.miniroom.dto.BookResponseDTO;
import com.project.bookpli.miniroom.repository.BookRepository;
import com.project.bookpli.miniroom.repository.LibraryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BookApiService {

    private final String TTB_KEY = "ttbsumini0911820002";
    private final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx";

    @Autowired
    private BookRepository bookrep;

    public void searchBook(String itemId) {
        RestTemplate restTemplate = new RestTemplate();

        String url = BASE_URL + "?ttbkey=" + TTB_KEY +
                "&itemIdType=ISBN13&ItemId=" + itemId +
                "&output=js&Version=20131101";

        Map<String, Object> response;
        try {
            response = restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch data from API. URL: " + url, e);
        }

        // 응답 검증
        if (response == null || !response.containsKey("item") || response.get("item") == null) {
            throw new IllegalArgumentException("API 응답에 'item' 키가 없거나 데이터가 비어 있습니다.");
        }

        // 도서 리스트 추출
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("item");

        if (items.isEmpty()) {
            throw new IllegalArgumentException("검색된 도서 정보가 존재하지 않습니다.");
        }

        Map<String, Object> item = items.get(0);

        String isbn13 = item.get("isbn13") != null ? item.get("isbn13").toString() : null;

        // 중복 데이터 방지 및 null 체크
        if (isbn13 == null || bookrep.existsById(isbn13)) {
            System.out.println("이미 존재하는 도서이거나 ISBN이 없습니다 :" + isbn13);
            return;
        }






        // DTO 생성 (빌더 패턴 사용)
        BookDTO dto = BookDTO.builder()
                .isbn13(isbn13)
                .title(item.get("title") != null ? item.get("title").toString() : "제목 없음")
                .author(item.get("author") != null ? item.get("author").toString() : "저자 없음")
                .description(item.get("description") != null ? item.get("description").toString() : "내용 없음")
                .pubdate(parsePubDate(item.get("pubDate") != null ? item.get("pubDate").toString() : null))
                .publisher(item.get("publisher") != null ? item.get("publisher").toString() : "출판사 없음")
                .cover(item.get("cover") != null ? item.get("cover").toString() : "이미지 없음")
                .startindex(parseStartIndex(item.get("subInfo")))
                .genre(item.get("categoryName") != null ? item.get("categoryName").toString() : "장르 없음")
                .build();

        // Entity 변환 및 저장
        Book book = dto.toEntity();
        bookrep.save(book);
    }

    // Helper 메서드: pubDate 파싱
    private Date parsePubDate(String pubDateStr) {
        if (pubDateStr == null) {
            return new java.sql.Date(System.currentTimeMillis()); // 기본값
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return new java.sql.Date(dateFormat.parse(pubDateStr).getTime());
        } catch (ParseException e) {
            System.err.println("Failed to parse pubDate: " + pubDateStr + " - Setting default date.");
            return new java.sql.Date(System.currentTimeMillis());
        }
    }

    // Helper 메서드: startIndex 파싱
    private int parseStartIndex(Object subInfoObj) {
        if (subInfoObj instanceof Map) {
            Map<String, Object> subInfo = (Map<String, Object>) subInfoObj;
            if (subInfo.containsKey("itemPage")) {
                try {
                    return Integer.parseInt(subInfo.get("itemPage").toString());
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse itemPage: " + subInfo.get("itemPage"));
                }
            }
        }
        return 0; // 기본값
    }

    //미니룸 - 도서리스트 조회
    public List<BookResponseDTO> getBookList(Long userId, String status) {
        if ("reading".equalsIgnoreCase(status)) {
            return bookrep.readBookList(userId);
        } else if ("wished".equalsIgnoreCase(status)) {
            return bookrep.addBookList(userId);
        } else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }
    
//    @Transactional
//    public void addBookAndLibrary(Book book, Long userId){
//        if(!bookrep.existsById((book.getIsbn13()))){
//            bookrep.save(book);
//        }
//
////        Library library= new Library();
//
//    }

}
