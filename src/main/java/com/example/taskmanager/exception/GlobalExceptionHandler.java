package com.example.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.taskmanager.dto.FieldErrorItemDTO;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(TaskNotFoundException.class)
        public ResponseEntity<Object> handleTaskNotFound(TaskNotFoundException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(),
                                HttpStatus.NOT_FOUND.getReasonPhrase(),
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""),
                                null);

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationsExceptions(
                        org.springframework.web.bind.MethodArgumentNotValidException ex,
                        WebRequest request) {
                // Convert each field error into FieldErrorItem
                List<FieldErrorItemDTO> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> new FieldErrorItemDTO(error.getField(), error.getDefaultMessage()))
                                .toList();

                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "Validation failed for one or more fields",
                                request.getDescription(false).replace("uri=", ""),
                                fieldErrors);

                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalExceptions(Exception ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                "An unexpected error occured: " + ex.getMessage() + ". Please contact support",
                                request.getDescription(false).replace("uri=", ""),
                                null);

                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex,
                        WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                HttpStatus.CONFLICT.getReasonPhrase(),
                                ex.getMessage(),
                                request.getDescription(false).replace("uri=", ""),
                                null);

                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleInvalidCredentials(BadCredentialsException ex,
                        WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                LocalDateTime.now(),
                                HttpStatus.UNAUTHORIZED.value(),
                                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                "Invalid username or password",
                                request.getDescription(false).replace("uri=", ""),
                                null);

                return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
}
