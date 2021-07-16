package com.flixbus.flixtrip.repositories.interfaces

import com.flixbus.flixtrip.models.Trip
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * For Supported keywords
 *  https://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html
 */
interface ITripRepository: CrudRepository<Trip, Long> {

    // Get by future date
    fun findByStartAtAfter(startAt: Date): List<Trip>

}