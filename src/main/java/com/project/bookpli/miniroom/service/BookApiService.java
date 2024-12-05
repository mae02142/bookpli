package com.project.bookpli.miniroom.service;

import com.project.bookpli.entity.Book;
import com.project.bookpli.miniroom.dto.MiniroomDTO;
import com.project.bookpli.miniroom.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class BookApiService {

    @Autowired
    private BookRepository bookrep;

    private final String TTB_KEY = "ttbsumini0911820002";
    private final String BASE_URL = "http://www.aladin.co.kr/ttb/api/ItemSearch.aspx";

    public void saveBookList(String keyword, int maxData) {
        RestTemplate restTemplate = new RestTemplate();

//        String url = null;
//
//        for (String isbn : isbnList) {
//            url = BASE_URL + "?ttbkey=" + TTB_KEY +
//                    "&QueryType=ItemLookUp&itemIdType=ISBN13&ItemId="+isbn+"=MaxResults=10&start=1&SearchTarget=Book&output=js&Version=20131101";
//        }

        int maxResults = 100;
        int totalData = 0;
        int start = 1;


        while (totalData < maxData) {
           String url = BASE_URL + "?ttbkey=" + TTB_KEY +
                    "&Query=" + keyword +
                    "&QueryType=Keyword&MaxResults=" + maxResults +
                    "&Start=" + start +
                    "&SearchTarget=Book&output=js&Version=20131101";


        //API 호출
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        //도서 리스트 추출
        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("item");

        for (Map<String, Object> item : items) {
            String isbn13 = item.get("isbn13").toString();

            // 중복 데이터 방지
            if (isbn13 == null || bookrep.existsById(isbn13)) {
                continue;
            }

            MiniroomDTO dto = new MiniroomDTO();

            dto.setIsbn13(item.get("isbn13").toString());
            dto.setTitle(item.get("title").toString());
            dto.setAuthor(item.get("author").toString());
            dto.setDescription(item.get("description").toString());
            dto.setPubdate(java.sql.Date.valueOf(item.get("pubdate").toString()));
            dto.setPublisher(item.get("publisher").toString());
            dto.setCover(item.get("cover").toString());
            dto.setGenre(item.get("genre").toString());

            Book book = dto.toEntity();
            bookrep.save(book);

            totalData++;
            if (totalData >= maxData) {
                break;
            }
        }

        // 다음 페이지로 이동
        start += maxResults;
        }
    }
}
