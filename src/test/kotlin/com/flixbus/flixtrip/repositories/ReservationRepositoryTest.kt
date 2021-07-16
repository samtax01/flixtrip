package com.flixbus.flixtrip.repositories

import com.flixbus.flixtrip.Seeder
import com.flixbus.flixtrip.helpers.ApiException
import com.flixbus.flixtrip.repositories.interfaces.IReservationRepository
import com.flixbus.flixtrip.repositories.interfaces.ITripRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.http.HttpStatus


@DataJpaTest
internal class ReservationRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var iReservationRepository: IReservationRepository

    @Autowired
    private lateinit var iTripRepository: ITripRepository

    private lateinit var reservationRepository: ReservationRepository;

    private lateinit var tripRepository: TripRepository;


    @BeforeEach
    fun setUp() {
        this.reservationRepository = ReservationRepository(iReservationRepository, iTripRepository)
        this.tripRepository = TripRepository(iReservationRepository, iTripRepository)
    }

    @Test
    fun `can get reservation`() {
        // Arrange
        val availableSpots = 10;
        val bookedSpots = 2;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        val reservation = this.reservationRepository.create(Seeder.getReservationRequest(trip.id, bookedSpots));

        // Assert
        assertNotNull(trip)
        assertNotNull(reservation)
        assertTrue(reservation.id > 0)
    }

    @Test
    fun `can create reservation`() {
        // Arrange
        val availableSpots = 10;
        val bookedSpots = 2;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        val reservation = this.reservationRepository.create(Seeder.getReservationRequest(trip.id, bookedSpots));

        // Assert
        assertNotNull(trip)
        assertNotNull(reservation)
        assertEquals(trip.id, reservation.tripId)
        assertEquals(bookedSpots, reservation.totalSpot)
    }


    @Test
    fun `onCreateReservation throwError if notEnoughSpotsOnTrip`() {
        // Arrange
        val availableSpots = 2;
        val bookedSpots = 5;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        val exception = assertThrows<ApiException>{
            this.reservationRepository.create(Seeder.getReservationRequest(trip.id, bookedSpots));
        }

        // Assert
        assertNotNull(trip)
        assertEquals("Not enough spots", exception.errorMessage)
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.httpStatus)
    }



    @Test
    fun `onCreateReservation throwError if tripSpotsIsSoldOut`() {
        // Arrange
        val availableSpots = 0;
        val bookedSpots = 5;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        val exception = assertThrows<ApiException>{
            this.reservationRepository.create(Seeder.getReservationRequest(trip.id, bookedSpots));
        }

        // Assert
        assertNotNull(trip)
        assertEquals("Sold out", exception.errorMessage)
        assertEquals(HttpStatus.NOT_ACCEPTABLE, exception.httpStatus)
    }

    @Test
    fun `onCreateReservation throwError if reservationRequest notValid`() {
        // Arrange
        val availableSpots = 2;
        val bookedSpots = 5;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        val request = Seeder.getReservationRequest(trip.id, bookedSpots);
        val exception1 = assertThrows<ApiException>{
            request.totalSpot = 0
            this.reservationRepository.create(request);
        }

        // Assert
        assertNotNull(trip)
        assertEquals("Spot value must be greater than 0", exception1.errorMessage)
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception1.httpStatus)
    }


    @Test
    fun `onCreateReservation reduce trip availableSpot`() {
        // Arrange
        val availableSpots = 10;
        val bookedSpots = 2;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        val reservation = this.reservationRepository.create(Seeder.getReservationRequest(trip.id, bookedSpots));
        val reducedTrip = this.tripRepository.adminGetTrip(trip.id);

        // Assert
        assertNotNull(trip)
        assertNotNull(reservation)
        assertEquals((availableSpots - bookedSpots), reducedTrip.availableSpot)
    }


    @Test
    fun `onUpdateReservation withLargerSpots validate for theAdditionalValue`() {
        // Arrange
        val availableSpots = 5;
        val initialBookedSpots = 2;
        // availableSpots remain 3
        val newBookedSpots = 4;
        // availableSpots remain 1
        val expectedAvailableSpots = 1;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        val reservation = this.reservationRepository.create(Seeder.getReservationRequest(trip.id, initialBookedSpots));

        // Update
        this.reservationRepository.update(Seeder.getReservationRequest(trip.id, newBookedSpots), reservation.id);
        val reducedTrip = this.tripRepository.adminGetTrip(trip.id);

        // Assert
        assertNotNull(trip)
        assertNotNull(reservation)
        assertEquals(expectedAvailableSpots, reducedTrip.availableSpot)
    }

    @Test
    fun `onUpdateReservation withLowerSpots restoreTripAvailabilitySpot`() {
        // Arrange
        val availableSpots = 5;
        val initialBookedSpots = 4;
        // availableSpots remain 1
        val newBookedSpots = 2;
        // availableSpots remain 3
        val expectedAvailableSpots = 3;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        val reservation = this.reservationRepository.create(Seeder.getReservationRequest(trip.id, initialBookedSpots));

        // Update
        this.reservationRepository.update(Seeder.getReservationRequest(trip.id, newBookedSpots), reservation.id);
        val reducedTrip = this.tripRepository.adminGetTrip(trip.id);

        // Assert
        assertNotNull(trip)
        assertNotNull(reservation)
        assertEquals(expectedAvailableSpots, reducedTrip.availableSpot)
    }


    @Test
    fun `onDeleteReservation increase trip availableSpot`() {
        // Arrange
        val availableSpots = 10;
        val bookedSpots = 2;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));

        // Assert total booked
        val reservation = this.reservationRepository.create(Seeder.getReservationRequest(trip.id, bookedSpots));
        assertEquals(2, reservation.totalSpot)

        // Assert reduced
        val reducedTrip = this.tripRepository.adminGetTrip(trip.id);
        assertEquals(8, reducedTrip.availableSpot)

        // Delete value
        this.reservationRepository.delete(reservation.id);
        val increasedTrip = this.tripRepository.adminGetTrip(trip.id);


        // Assert
        assertNotNull(trip)
        assertNotNull(reservation)
        assertEquals(trip.totalSpot, increasedTrip.availableSpot)
    }




    @Test
    fun `can update reservationProperties`() {
        // Arrange
        val availableSpots = 10;
        val bookedSpots = 2;
        val newCustomerName = "John Wick";

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots))
        val request = Seeder.getReservationRequest(trip.id, bookedSpots);
        val reservation = this.reservationRepository.create(request)
        request.customerName = newCustomerName;
        val updatedReservation = this.reservationRepository.update(request, reservation.id)

        // Assert
        assertNotNull(trip)
        assertEquals(reservation.id, updatedReservation.id)
        assertEquals(reservation.tripId, updatedReservation.tripId)
        assertEquals(newCustomerName, updatedReservation.customerName)
    }

    @Test
    fun `delete Reservation`() {
        // Arrange
        val availableSpots = 10;
        val bookedSpots = 5;

        // Act
        val trip = this.tripRepository.adminUpdateOrCreateTrip(Seeder.getTripRequest(availableSpots));
        val reservation = this.reservationRepository.create(Seeder.getReservationRequest(trip.id, bookedSpots));
        this.reservationRepository.delete(reservation.id);
        assertNotNull(reservation)

        val exception = assertThrows<ApiException>{
            this.reservationRepository.get(reservation.id);
        }

        // Assert
        assertNotNull(trip)
        assertEquals("Reservation is not available", exception.errorMessage)
        assertEquals(HttpStatus.NOT_FOUND, exception.httpStatus)
    }
}