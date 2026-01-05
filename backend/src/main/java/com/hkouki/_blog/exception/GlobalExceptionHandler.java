package com.hkouki._blog.exception;

import com.hkouki._blog.dto.ApiResponse;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    // Handles case when a user tries to register with an email that already exists
    public ResponseEntity<ApiResponse<Void>> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<Void>("error", null, ex.getMessage()));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    // Handles case when a user tries to register with a username that already
    // exists
    public ResponseEntity<ApiResponse<Void>> handleUsernameExists(UsernameAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<Void>("error", null, ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    // Handles invalid login attempts (wrong username or password)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<Void>("error", null, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    // Handles validation errors from @Valid in request body (e.g., missing or
    // invalid fields)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> response = new ApiResponse<>("error", errors, "Validation failed");

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UnauthenticatedException.class)
    // Handles cases where the user is not logged in or authentication failed
    public ResponseEntity<ApiResponse<Void>> handleUnauthenticated(UnauthenticatedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>("error", null, ex.getMessage()));
    }

    @ExceptionHandler(InvalidPrincipalException.class)
    // Handles invalid authentication principal (unexpected security object)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPrincipal(InvalidPrincipalException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>("error", null, ex.getMessage()));
    }

    @ExceptionHandler(MissingPathVariableException.class)
    // Handles missing path variables in URL (e.g., /users//delete)
    public ResponseEntity<ApiResponse<Void>> handleMissingPathVar(MissingPathVariableException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("error", null, "Missing path variable: " + ex.getVariableName()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    // Handles HTTP method not allowed errors (e.g., using POST on a GET-only
    // endpoint)
    public ResponseEntity<ApiResponse<Void>> handleHttpMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ApiResponse<>("error", null, "HTTP method not allowed: " + ex.getMethod()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    // Handles illegal arguments passed to methods
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("error", null, ex.getMessage()));
    }

    //this handler deals with resource not found exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND) // 404
                .body(new ApiResponse<>("error", null, ex.getMessage()));
    }

    // this handler deals with no handler found for a specific path
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>("error", null, "Path not found: " + ex.getRequestURL()));
    }

    // @ExceptionHandler(Exception.class)
    // // Catch-all for unhandled exceptions, returns HTTP 500 Internal Server Error
    // public ResponseEntity<ApiResponse<Void>> handleOtherExceptions(Exception ex)
    // {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body(new ApiResponse<Void>("error", null, "Internal server error"));
    // }

}