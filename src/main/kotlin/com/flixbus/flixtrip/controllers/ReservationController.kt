package com.flixbus.flixtrip.controllers

import com.flixbus.flixtrip.models.requests.ReservationRequest
import com.flixbus.flixtrip.helpers.ApiResponse
import com.flixbus.flixtrip.repositories.ReservationRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController()
@RequestMapping("/api/reservations")
class ReservationController(val reservationRepository: ReservationRepository) {

    // Create a new reservation
    @PostMapping
    fun reserveSpot(@RequestBody request: ReservationRequest): ResponseEntity<ApiResponse> =
        ApiResponse.create(reservationRepository.create(request), true, "Created successfully", HttpStatus.CREATED)


    // Update reservation
    @PutMapping(path = ["{id}"])
    fun updateReservedSpot(@PathVariable id:Long, @RequestBody request: ReservationRequest): ResponseEntity<ApiResponse> =
        ApiResponse.create(reservationRepository.update(request, id), true, "Updated successfully", HttpStatus.ACCEPTED)

    // Get reservation
    @GetMapping("{id}")
    fun getReservation(@PathVariable id:Long): ResponseEntity<ApiResponse> =
        ApiResponse.create(reservationRepository.get(id), true, "Success", HttpStatus.OK)


    // Delete reservation
    @DeleteMapping(path = ["{id}"])
    fun deleteReservedSpot(@PathVariable id:Long): ResponseEntity<ApiResponse> =
        ApiResponse.create(reservationRepository.delete(id), true, "Deleted successfully", HttpStatus.OK)

}