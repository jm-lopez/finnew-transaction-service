package com.juanmatiaslopez.transaction_service.Exception;

import com.juanmatiaslopez.transaction_service.DTO.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler{

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequestException(BadRequestException ex){
        log.error("Bad Request Exception: {}", ex.getMessage());
        ApiResponse<?> errorResponse= new ApiResponse<>(HttpStatus.SC_BAD_REQUEST, ex.getMessage(), null );
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(HttpStatus.SC_BAD_REQUEST));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFoundException(BadRequestException ex){
        log.error("Not Found Exception: {}", ex.getMessage());
        ApiResponse<?> errorResponse= new ApiResponse<>(HttpStatus.SC_NOT_FOUND, ex.getMessage(), null );
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(HttpStatus.SC_NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOtherException(BadRequestException ex){
        log.error("Exception: {}", ex.getMessage());
        ApiResponse<?> errorResponse= new ApiResponse<>(HttpStatus.SC_INTERNAL_SERVER_ERROR, ex.getMessage(), null );
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(HttpStatus.SC_INTERNAL_SERVER_ERROR));
    }
}
