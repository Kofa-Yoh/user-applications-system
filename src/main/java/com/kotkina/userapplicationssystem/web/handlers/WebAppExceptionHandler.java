package com.kotkina.userapplicationssystem.web.handlers;

import com.kotkina.userapplicationssystem.exceptions.*;
import com.kotkina.userapplicationssystem.web.models.response.ErrorResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class WebAppExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> handleNoEntityFoundException(DataNotFoundException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.NOT_FOUND, ex, webRequest);
    }

    @ExceptionHandler({RequestNotAvailableException.class, CurrentUserNotDefinedException.class})
    public ResponseEntity<ErrorResponseBody> handleRequestNotAvailable(RuntimeException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex, webRequest);
    }

    @ExceptionHandler({PhoneNotVerifiedException.class, AlreadyExistsException.class,
            IllegalArgumentException.class, HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponseBody> handleBadRequestException(RuntimeException ex, WebRequest webRequest) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex, webRequest);
    }

    private ResponseEntity<ErrorResponseBody> buildResponse(HttpStatusCode httpStatus, Exception ex, WebRequest webRequest) {
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponseBody(ex.getMessage(), webRequest.getDescription(false)));
    }
}
