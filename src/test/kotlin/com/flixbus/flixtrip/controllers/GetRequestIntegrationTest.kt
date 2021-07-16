package com.flixbus.flixtrip.controllers

import com.flixbus.flixtrip.helpers.ApiResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class GetRequestIntegrationTest(@Autowired val restTemplate: TestRestTemplate) {


    @Test
    fun `Can get available trip`() {
        // Arrange
        val url = "/api/trips";

        // Act
        val response = restTemplate.getForEntity<ApiResponse>(url)
        assertNotNull(response)
        val apiResponse = response.body

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(true, apiResponse?.status)
    }


    @Test
    fun `Can get all trip`() {
        // Arrange
        val url = "/api/admin/trips";

        // Act
        val response = restTemplate.getForEntity<ApiResponse>(url)
        assertNotNull(response)
        val apiResponse = response.body

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(true, apiResponse?.status)
    }


}