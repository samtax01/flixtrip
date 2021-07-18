package com.flixbus.flixtrip.controllers

import com.flixbus.flixtrip.helpers.ApiResponse
import com.flixbus.flixtrip.models.requests.TripRequest
import com.flixbus.flixtrip.repositories.TripRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController()
@RequestMapping("/api/")
class TripController(val tripRepository: TripRepository) {


    // Get available trips
    @GetMapping("trips")
    fun getAvailableTrips(): ResponseEntity<ApiResponse> =
        ApiResponse.create(tripRepository.getAvailableTrips(), true, tripRepository.getAvailableTrips().count().toString() + " row(s) returned", HttpStatus.OK)


    // Get trip
    @GetMapping("admin/trips/{id}")
    fun getTrip(@PathVariable id:Long): ResponseEntity<ApiResponse> =
            ApiResponse.create(tripRepository.adminGetTrip(id), true, "Success", HttpStatus.OK)


    // Admin: Get trips
    @GetMapping("admin/trips")
    fun getTrips(): ResponseEntity<ApiResponse> =
        ApiResponse.create(tripRepository.adminGetTrips(), true, tripRepository.adminGetTrips().count().toString() + " row(s) returned", HttpStatus.OK)



    // Admin: Create trip
    @PostMapping(path = ["admin/trips"])
    fun createTrip(@RequestBody request: TripRequest): ResponseEntity<ApiResponse> =
        ApiResponse.create(tripRepository.adminUpdateOrCreateTrip(request), true, "Created successfully", HttpStatus.CREATED)



    // Admin: Update trip
    @PutMapping(path = ["admin/trips/{id}"])
    fun updateTrip(@PathVariable id:Long, @RequestBody request: TripRequest): ResponseEntity<ApiResponse> =
        ApiResponse.create(tripRepository.adminUpdateOrCreateTrip(request, id), true, "Updated successfully", HttpStatus.ACCEPTED)


    // Admin: Delete trip
    @DeleteMapping(path = ["admin/trips/{id}"])
    fun deleteTrip(@PathVariable id:Long): ResponseEntity<ApiResponse> =
        ApiResponse.create(tripRepository.adminDeleteTrip(id), true, "Deleted successfully", HttpStatus.OK)
    
}