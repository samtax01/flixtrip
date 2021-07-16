package com.flixbus.flixtrip.repositories.interfaces

import com.flixbus.flixtrip.models.Reservation
import org.springframework.data.repository.CrudRepository

/**
 * For Supported keywords
 *  https://docs.spring.io/spring-data/jpa/docs/1.5.0.RELEASE/reference/html/jpa.repositories.html
 */
interface IReservationRepository: CrudRepository<Reservation, Long>