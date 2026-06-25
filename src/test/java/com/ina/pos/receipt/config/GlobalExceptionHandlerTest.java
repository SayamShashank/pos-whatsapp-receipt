package com.ina.pos.receipt.config;

import com.ina.pos.receipt.dto.ApiResponse;
import com.ina.pos.receipt.exception.CommonValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testHandleMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException methodArgumentNotValidException = new MethodArgumentNotValidException(methodParameter,bindingResult);
        ResponseEntity<ApiResponse> responseEntity = globalExceptionHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void testHandleRuntimeException(){
        RuntimeException runtimeException = new RuntimeException("error");
        ResponseEntity<ApiResponse> responseEntity = globalExceptionHandler.handleRuntimeException(runtimeException);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void testHandleException(){
        Exception exception = new Exception("error-message");
        ResponseEntity<ApiResponse> responseEntity = globalExceptionHandler.handleException(exception);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void testHandleCommonValidationException(){
        CommonValidationException commonValidationException = new CommonValidationException("error-message", 400);
        ResponseEntity<ApiResponse> responseEntity = globalExceptionHandler.handleCommonValidationException(commonValidationException);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

}
