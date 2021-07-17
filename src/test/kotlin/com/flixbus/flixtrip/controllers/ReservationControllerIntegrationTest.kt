package com.flixbus.flixtrip.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.flixbus.flixtrip.Seeder
import com.flixbus.flixtrip.helpers.ApiResponse
import com.flixbus.flixtrip.models.Reservation
import com.flixbus.flixtrip.models.Trip
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ReservationControllerIntegrationTest(@Autowired val restTemplate: TestRestTemplate) {

    val objectMapper: ObjectMapper = ObjectMapper();

    @Test
    fun `can create reservation`() {

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


        //////////////// Create Reservation


        // Arrange
        val reservationUrl = "/api/reservations";

        // Act
        val reservationResponse =
            restTemplate.postForEntity<ApiResponse>(reservationUrl, Seeder.getReservationRequest(trip.id))
        assertNotNull(reservationResponse)
        val reservationApiResponse = reservationResponse.body
        // Convert value to Trip
        val reservation: Reservation =
            objectMapper.readValue(objectMapper.writeValueAsString(reservationApiResponse?.data));

        // Assert
        assertEquals(HttpStatus.CREATED, reservationResponse.statusCode)
        assertEquals(true, reservationApiResponse?.status)
        assertNotNull(reservation)

    }


    @Test
    fun `can create reservation on theSameTrip in parallelForConcurrencyTest`() {

        // Arrange
        val tripUrl = "/api/admin/trips";

        // Act
        val tripResponse = restTemplate.postForEntity<ApiResponse>(tripUrl, Seeder.getTripRequest(50))
        assertNotNull(tripResponse)
        val tripApiResponse = tripResponse.body
        // Convert value to Trip
        val trip: Trip = objectMapper.readValue(objectMapper.writeValueAsString(tripApiResponse?.data));

        // Assert
        assertEquals(HttpStatus.CREATED, tripResponse.statusCode)
        assertEquals(true, tripApiResponse?.status)
        assertNotNull(trip)



        //////////////// Create Reservations in Parallel



        // Arrange
        val reservationUrl = "/api/reservations";

        (1..5).toList().parallelStream().forEach{

            // Act
            val reservationResponse = restTemplate.postForEntity<ApiResponse>(reservationUrl, Seeder.getReservationRequest(trip.id))
            assertNotNull(reservationResponse)
            val reservationApiResponse = reservationResponse.body
            // Convert value to Trip
            val reservation: Reservation = objectMapper.readValue(objectMapper.writeValueAsString(reservationApiResponse?.data));

            // Assert
            assertEquals("Created successfully", reservationApiResponse?.message)
            assertEquals(HttpStatus.CREATED, reservationResponse.statusCode)
            assertEquals(true, reservationApiResponse?.status)
            assertNotNull(reservation)

            println("Reservation Id " +  reservation.id)
        }
    }


}