package com.jonathan.picpay.ExceptionHandler;

import com.jonathan.picpay.Exceptions.*;
import com.jonathan.picpay.Exceptions.ExceptionDetail.ExceptionsDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionsDetails> handlerNotFoundException(NotFoundException not) {
        return new ResponseEntity<>(ExceptionsDetails.builder()
                .title("Not Found Exception")
                .message(not.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now()).build(), HttpStatus.NOT_FOUND);


    }

    @ExceptionHandler(NoBalanceException.class)
    public ResponseEntity<ExceptionsDetails> handlerNoBalanceException(NoBalanceException no) {
        return new ResponseEntity<>(ExceptionsDetails.builder()
                .title("No Balance Exception")
                .message(no.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now()).build(), HttpStatus.BAD_REQUEST);


    }

    @ExceptionHandler(SelfTransactionException.class)
    public ResponseEntity<ExceptionsDetails> handlerSelfTransactionException(SelfTransactionException self) {
        return new ResponseEntity<>(ExceptionsDetails.builder()
                .title("Self Transaction Exception")
                .message(self.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now()).build(), HttpStatus.BAD_REQUEST);


    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionsDetails> handlerUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ExceptionsDetails.builder()
                .title("User already exists Exception")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now()).build(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(TypeNotSupportedForTransaction.class)
    public ResponseEntity<ExceptionsDetails> TypeNotSupportedForTransactionException(TypeNotSupportedForTransaction type) {
        return new ResponseEntity<>(ExceptionsDetails.builder()
                .title("UserType Not Supported for this Transaction ")
                .message(type.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now()).build(), HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

}


