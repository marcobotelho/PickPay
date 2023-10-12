package com.marco.pickpay.infra;

import java.time.LocalDateTime;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.marco.pickpay.records.ErroRecord;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class, Throwable.class, RuntimeException.class})
    public ResponseEntity<ErroRecord> handleException(Exception ex, HttpServletRequest request) {
        ErroRecord erro =
                new ErroRecord(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        ex.getClass().getSimpleName(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroRecord> HttpMessageNotReadableException(Exception ex,
            HttpServletRequest request) {
        ErroRecord erro = new ErroRecord(LocalDateTime.now(), HttpStatus.BAD_REQUEST.toString(),
                ex.getClass().getSimpleName(), ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErroRecord> handleNullPointerException(NullPointerException ex,
            HttpServletRequest request) {
        ErroRecord erro = new ErroRecord(LocalDateTime.now(), HttpStatus.BAD_REQUEST.toString(),
                HttpStatus.BAD_REQUEST.toString(), "Ocorreu um erro interno no servidor.",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroRecord> handleIllegalArgumentException(IllegalArgumentException ex,
            HttpServletRequest request) {
        ErroRecord erro = new ErroRecord(LocalDateTime.now(), HttpStatus.BAD_REQUEST.toString(),
                HttpStatus.BAD_REQUEST.toString(), "Ocorreu um erro interno no servidor.",
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroRecord> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
            String errorMessage = violation.getMessage();
            sb.append(fieldName).append(": ").append(errorMessage).append("; ");
        }
        String errorMessage = sb.toString().trim();

        ErroRecord erro = new ErroRecord(LocalDateTime.now(), HttpStatus.BAD_REQUEST.toString(),
                ex.getClass().getSimpleName(), errorMessage, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

}
