package com.flixbus.flixtrip.helpers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Use for custom exception with status code.
 */
data class ApiException(val errorMessage: String, val httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR): Exception(errorMessage);



/**
 *  This class is annotated with @ControllerAdvice which means that this class will be
 *  able to handle the exceptions occurred from any of the Controllers
 */
@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(Exception::class)
    fun handleExceptions(exception: Exception, webRequest: WebRequest?): ResponseEntity<ApiResponse> {
        return ApiResponse.errorMessage(exception.message.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApiException::class)
    fun handleExceptions(exception: ApiException, webRequest: WebRequest?): ResponseEntity<ApiResponse> {
        return ApiResponse.errorMessage(exception.errorMessage, exception.httpStatus);
    }

}