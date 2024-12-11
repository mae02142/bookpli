package com.project.bookpli.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum BaseResponseStatus {
    /* GET, PUT, DELETE */
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공하였습니다."),

    /* POST */
    CREATED(true,HttpStatus.CREATED.value(), "등록에 성공하였습니다"),

    /* 회원 */
    USER_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다."),
    JWT_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "존재하지 않는 토큰입니다."),


   /* 리뷰 */
    REVIEW_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(),"리뷰가 존재하지 않습니다."),

   /* 댓글 */
    COMMENT_NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "댓글이 존재하지 않습니다"),
    INVAILD_COMMENT(false, HttpStatus.BAD_REQUEST.value(),"입력된 댓글 내용이 비어 있거나 올바르지 않습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

}
