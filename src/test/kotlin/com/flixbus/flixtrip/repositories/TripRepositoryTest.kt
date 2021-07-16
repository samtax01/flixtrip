package com.flixbus.flixtrip.repositories

import com.flixbus.flixtrip.Seeder
import com.flixbus.flixtrip.helpers.ApiException
import com.flixbus.flixtrip.repositories.interfaces.IReservationRepository
import com.flixbus.flixtrip.repositories.interfaces.ITripRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.http.HttpStatus
import java.time.temporal.ChronoUnit
import java.util.*


@DataJpaTest
internal class TripRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var iReservationRepository: IReservationRepository

    @Autowired
    lateinit var iTripRepository: ITripRepository

    private lateinit var reservationRepository: ReservationRepository;

    private lateinit var tripRepository: TripRepository;


    @BeforeEach
    fun setUp() {
        this.reservationRepository = ReservationRepository(iReservationRepository, iTripRepository)
        this.tripRepository = TripRepository(iReservationRepository, iTripRepository)
    }



    @Test
    fun `Can get all trip`() {
        // Arrange
        val availableSpots = 10;

        // Act
        val request = Seeder.getTripRequest(availableSpots);
        this.tripRepository.adminUpdateOrCreateTrip(request);
        this.tripRepository.adminUpdateOrCreateTrip(request);
        this.tripRepository.adminUpdateOrCreateTrip(request);
        val allTrips = this.tripRepository.adminGetTrips();

        // Assert
        assertEquals(3, allTrips.count())
    }

    @Test
    fun `Can get single trip`() {
        // Arrange
        val availableSpots = 10;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        val theTrip = this.tripRepository.adminGetTrip(trip.id);

        // Assert
        assertNotNull(trip)
        assertNotNull(theTrip)
        assertTrue(theTrip.id > 0)
        assertEquals(trip.id, theTrip.id)
    }



    @Test
    fun `adminCan update or createTrip`() {
        // Arrange
        val availableSpots = 10;
        val newAvailableSpots = 15;

        // Act
        val request = Seeder.getTripRequest(availableSpots);
        val trip = this.tripRepository.adminUpdateOrCreateTrip(request);
        assertNotNull(trip)
        request.availableSpot = newAvailableSpots;
        val updatedTrip = this.tripRepository.adminUpdateOrCreateTrip(request, trip.id);

        // Assert
        assertNotNull(updatedTrip)
        assertEquals(newAvailableSpots, updatedTrip.availableSpot)
    }


    @Test
    fun `adminCan deleteTrip`() {
        // Arrange
        val availableSpots = 10;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        assertNotNull(trip)
        this.tripRepository.adminDeleteTrip(trip.id);
        val exception = org.junit.jupiter.api.assertThrows<ApiException> {
            this.tripRepository.adminGetTrip(trip.id);
        }

        // Assert
        assertNotNull(trip)
        assertEquals("Trip is not available", exception.errorMessage)
        assertEquals(HttpStatus.NOT_FOUND, exception.httpStatus)
    }

    @Test
    fun `can getAvailableTrips`() {
        // Arrange
        val availableSpots = 10;

        // Act
        val expiredTrip = Seeder.getTripRequest(availableSpots);
        expiredTrip.startAt = Date.from(Date().toInstant().minus(5, ChronoUnit.DAYS));
        this.tripRepository.adminUpdateOrCreateTrip(expiredTrip);

        val activeTrip = Seeder.getTripRequest(availableSpots);
        activeTrip.startAt = Date.from(Date().toInstant().plus(5, ChronoUnit.DAYS));
        this.tripRepository.adminUpdateOrCreateTrip(activeTrip);

        val allTrips = this.tripRepository.getAvailableTrips();

        // Assert
        assertEquals(1, allTrips.count())
    }
}