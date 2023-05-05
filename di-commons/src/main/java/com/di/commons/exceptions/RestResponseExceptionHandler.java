package com.di.commons.exceptions;

import java.util.Date;

import javax.persistence.EntityNotFoundException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {
	
	 public RestResponseExceptionHandler() {
	        super();
	    }

	 // Error Code 500

	    @ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class })
	    /*500*/public ResponseEntity<Object> handleInternal(final Exception ex, final WebRequest request) {
	        logger.error("500 Status Code", ex);
	        ErrorMessages error= new ErrorMessages();
	        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
	        error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        error.setErrorMsg(ex.getLocalizedMessage());
	        error.setDateTime(java.time.LocalDateTime.now());
	        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	    }
	    
	    
	    // Error Code  404

	    @ExceptionHandler(value = { EntityNotFoundException.class, ResourceNotFoundException.class })
	    protected ResponseEntity<Object> handleNotFound(final RuntimeException ex, final WebRequest request) {
	    	 ErrorMessages error= new ErrorMessages();
		        error.setStatus(HttpStatus.NOT_FOUND);
		        error.setErrorCode(HttpStatus.NOT_FOUND.value());
		        error.setErrorMsg(ex.getMessage());
		        error.setDateTime(java.time.LocalDateTime.now());
	        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	    }
	    
	    // Error Code 400

	    @ExceptionHandler({ ConstraintViolationException.class })
	    public ResponseEntity<Object> handleBadRequest(final ConstraintViolationException ex, final WebRequest request) {
	    	 ErrorMessages error= new ErrorMessages();
		        error.setStatus(HttpStatus.BAD_REQUEST);
		        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
		        error.setErrorMsg(ex.getLocalizedMessage());
		        error.setDateTime(java.time.LocalDateTime.now());
	        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	    }

	    @ExceptionHandler({ DataIntegrityViolationException.class })
	    public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
	    	 ErrorMessages error= new ErrorMessages();
		        error.setStatus(HttpStatus.BAD_REQUEST);
		        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
		        error.setErrorMsg(ex.getLocalizedMessage());
		        error.setDateTime(java.time.LocalDateTime.now());
	        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	    }

	    @Override
	    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
	    	 ErrorMessages error= new ErrorMessages();
		        error.setStatus(HttpStatus.BAD_REQUEST);
		        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
		        error.setErrorMsg(ex.getLocalizedMessage());
		        error.setDateTime(java.time.LocalDateTime.now());
	        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
	    }

	    @Override
	    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
	    	 ErrorMessages error= new ErrorMessages();
		        error.setStatus(HttpStatus.BAD_REQUEST);
		        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
		        error.setErrorMsg(ex.getLocalizedMessage());
		        error.setDateTime(java.time.LocalDateTime.now());
	        return handleExceptionInternal(ex, error, headers, HttpStatus.BAD_REQUEST, request);
	    }


	 // Error Code 409

	    @ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class })
	    protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
	    	 ErrorMessages error= new ErrorMessages();
		        error.setStatus(HttpStatus.CONFLICT);
		        error.setErrorCode(HttpStatus.CONFLICT.value());
		        error.setErrorMsg(ex.getLocalizedMessage());
		        error.setDateTime(java.time.LocalDateTime.now());
	        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.CONFLICT, request);
	    }

}
