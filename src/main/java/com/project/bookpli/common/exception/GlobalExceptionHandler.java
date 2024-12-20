package com.project.bookpli.common.exception;

import com.project.bookpli.common.response.BaseResponse;
import com.project.bookpli.common.response.BaseResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<BaseResponse<Void>> handleBaseException(BaseException ex) {
    BaseResponse<Void> response = new BaseResponse<>(ex.getStatus());
    return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatus().getCode()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<Void>> handleException(Exception ex) {
    BaseResponse<Void> response = new BaseResponse<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
