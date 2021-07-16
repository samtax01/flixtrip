package com.flixbus.flixtrip.helpers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Use as a wrapper for response data
 *  usage:
 *      return ApiResponse.create(repo.save(request), true, "Created successfully", HttpStatus.CREATED)
 */
data class ApiResponse(
        val data: Any? = null,
        val status: Boolean = true,
        val message: String = "Success"){

    companion object {
        fun create(data: Any? = null, status: Boolean = true, message: String = "Success", httpStatus: HttpStatus = HttpStatus.OK): ResponseEntity<ApiResponse> {
            return ResponseEntity(ApiResponse(data, status, message), httpStatus)
        }

        fun errorMessage(message: String, httpStatus: HttpStatus): ResponseEntity<ApiResponse> {
            return create(null, false, message, httpStatus);
        }
    }
}