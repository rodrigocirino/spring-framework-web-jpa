package com.farmio.api.infra;

import com.farmio.api.dto.PharmacistDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorCustomizer {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tryError404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tryError400(MethodArgumentNotValidException ex) {
        var errors = ex.getFieldErrors();
        var listErrors = errors.stream().map(DataErrorCustomizer::new).toList();
        return ResponseEntity.badRequest().body(listErrors);
    }

    private record DataErrorCustomizer(String field, String message) {
        private DataErrorCustomizer(FieldError er) {
            this(er.getField(), er.getDefaultMessage());
        }
    }


}
