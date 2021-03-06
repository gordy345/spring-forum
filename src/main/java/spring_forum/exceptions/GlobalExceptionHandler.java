package spring_forum.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.OptimisticLockException;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleNotFoundException(NotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleExistsException(ExistsException exception) {
        log.warn("ExistsException happened, message: " + exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("ValidationException happened, message: " + errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleValidationException(
            ValidationException ex) {
        log.warn("ValidationException happened, message: " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleOptimisticLockException(OptimisticLockException exception) {
        log.warn("OptimisticLockException happened, message: " + exception.getMessage());
        return new ResponseEntity<>("Something went wrong. Try again.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("IllegalArgumentException happened, message: " + exception.getMessage());
        return new ResponseEntity<>("Something went wrong. Try again.", HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleTokenExpiredException(TokenExpiredException exception) {
        log.warn("TokenExpiredException happened");
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }
}
