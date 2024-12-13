package com.project.bookpli.book.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookpli.book.dto.BookDTO;
import com.project.bookpli.book.repository.BookRepository;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public BookService(BookRepository bookRepository, WebClient.Builder builder, ObjectMapper objectMapper) {
        this.bookRepository = bookRepository;
        this.webClient = builder.build();
        this.objectMapper = objectMapper;
    }

    public BookDTO getBookDetail(String isbn13) {
        // 단독으로 URI 처리
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("www.aladin.co.kr")
                        .path("/ttb/api/ItemLookUp.aspx")
                        .queryParam("ttbkey", "ttbsumini0911820002")
                        .queryParam("itemIdType", "ISBN")
                        .queryParam("ItemId", isbn13)
                        .queryParam("output", "js")
                        .queryParam("Version", "20131101")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseBookDetail(response);
    }

    private BookDTO parseBookDetail(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode itemNode = rootNode.path("item").get(0); // 첫 번째 item 가져오기

            // 필요한 정보만 추출
            String isbn13 = itemNode.path("isbn13").asText();
            // 제목: " - " 또는 "(" 이후 제거
            String title = itemNode.path("title").asText().split(" - | \\(")[0];
            // 작가: "(지은이)" 이후 제거
            String author = itemNode.path("author").asText().split(" \\(")[0];
            String description = itemNode.path("description").asText();
            // pubDate를 yyyy-MM-dd 형식으로 변환
            LocalDate pubDate = parsePubDate(itemNode);
            String publisher = itemNode.path("publisher").asText();
            String cover = itemNode.path("cover").asText();
            int startindex = itemNode.path("subInfo").path("itemPage").asInt();
            String genre = itemNode.path("categoryName").asText();

            // BookDTO 객체 생성 및 반환
            return new BookDTO(
                    isbn13,
                    title,
                    author,
                    description,
                    pubDate,
                    publisher,
                    cover,
                    startindex,
                    genre
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse book detail response", e);
        }
    }

    private static LocalDate parsePubDate(JsonNode itemNode) {
        String pubdateString = itemNode.path("pubDate").asText();
        return LocalDate.parse(pubdateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
