package com.flixbus.flixtrip.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.flixbus.flixtrip.Seeder
import com.flixbus.flixtrip.helpers.ApiResponse
import com.flixbus.flixtrip.models.Trip
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.patchForObject
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class TripControllerIntegrationTest(@Autowired val restTemplate: TestRestTemplate) {

    val objectMapper: ObjectMapper = ObjectMapper();

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



    @Test
    fun `can create trip`() {
        // Arrange
        val tripUrl = "/api/admin/trips";

        // Act
        val tripResponse = restTemplate.postForEntity<ApiResponse>(tripUrl, Seeder.getTripRequest())
        assertNotNull(tripResponse)
        val tripApiResponse = tripResponse.body
        // Convert value to Trip
        val trip: Trip = objectMapper.readValue(objectMapper.writeValueAsString(tripApiResponse?.data));

        // Assert
        assertEquals(HttpStatus.CREATED, tripResponse.statusCode)
        assertEquals(true, tripApiResponse?.status)
        assertNotNull(trip)
    }



    @Test
    fun `can update trip`() {
        // Arrange
        val tripUrl = "/api/admin/trips";

        // Act
        val tripResponse = restTemplate.postForEntity<ApiResponse>(tripUrl, Seeder.getTripRequest())
        assertNotNull(tripResponse)
        val tripApiResponse = tripResponse.body
        // Convert value to Trip
        val trip: Trip = objectMapper.readValue(objectMapper.writeValueAsString(tripApiResponse?.data));

        // Assert
        assertEquals(HttpStatus.CREATED, tripResponse.statusCode)
        assertEquals(true, tripApiResponse?.status)
        assertNotNull(trip)


        //////////////// Update Trip

        // Arrange
        val tripUpdateUrl = "/api/admin/trips/${trip.id}";

        // Act
        val tripUpdateResponse = restTemplate.patchForObject<ApiResponse>(tripUpdateUrl, Seeder.getTripRequest())
        assertNotNull(tripUpdateResponse)
        val tripUpdateApiResponse = tripUpdateResponse?.copy();
        // Convert value to Trip
        val tripUpdate: Trip = objectMapper.readValue(objectMapper.writeValueAsString(tripUpdateApiResponse?.data));

        // Assert
        assertEquals(true, tripUpdateApiResponse?.status)
        assertNotNull(tripUpdate)
    }


}