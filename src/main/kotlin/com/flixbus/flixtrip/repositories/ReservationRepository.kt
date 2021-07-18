package com.flixbus.flixtrip.repositories


import com.flixbus.flixtrip.enums.TripAvailability
import com.flixbus.flixtrip.helpers.ApiException
import com.flixbus.flixtrip.models.Reservation
import com.flixbus.flixtrip.models.Trip
import com.flixbus.flixtrip.models.requests.ReservationRequest
import com.flixbus.flixtrip.repositories.interfaces.IReservationRepository
import com.flixbus.flixtrip.repositories.interfaces.ITripRepository
import org.hibernate.StaleObjectStateException
import org.springframework.http.HttpStatus
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils
import java.util.*
import javax.persistence.OptimisticLockException
import javax.transaction.Transactional

@Repository
class ReservationRepository(val reservationTable: IReservationRepository, val tripTable: ITripRepository) {

    /**
     * Get reservation
     */
    fun get(id: Long): Reservation {
        val result = reservationTable.findById(id);
        if(!result.isPresent)
            throw ApiException("Reservation is not available", HttpStatus.NOT_FOUND)
        return result.get();
    }


    /**
     *  Create reservation if selected spot is greater than 0 and space is available
     */
    @Transactional
    @Retryable(
        value = [OptimisticLockException::class, StaleObjectStateException::class], // If an OptimisticLockException exception rises in this method, It will be called again in a new transaction to repeat the operation.
        maxAttempts = 5
    )
    fun create(request: ReservationRequest): Reservation {
        // Get trip
        val trip = getAndValidateTrip(request.tripId)

        // Validate request
        validateRequest(request)
        validateAvailability(trip.get().availableSpots, request.totalSpots)

        // Remove from Trip Availability
        updateTripAvailableSpot(trip, request.totalSpots, TripAvailability.Remove)

        // Save Changes
        return saveReservation(request, 0);
    }


    /**
     *  Update reservation
     */
    @Transactional
    @Retryable(
        value = [OptimisticLockException::class, StaleObjectStateException::class],
        maxAttempts = 5
    )
    fun update(newReservation: ReservationRequest, id: Long): Reservation {
        // Get trip
        val trip = getAndValidateTrip(newReservation.tripId)

        // Validate request
        validateRequest(newReservation)

        // Get existing reservation
        val oldReservation = get(id)

        // Calculate differences in current and previous reservation
        if(newReservation.totalSpots > oldReservation.totalSpots){
            // Validate new request additional spot
            val additionalSpot = (newReservation.totalSpots - oldReservation.totalSpots)

            // Remove from Trip Availability
            validateAvailability(trip.get().availableSpots, additionalSpot);
            updateTripAvailableSpot(trip, additionalSpot, TripAvailability.Remove)

        }else if(newReservation.totalSpots < oldReservation.totalSpots){
            // Restore Trip Availability
            val leftOverSpot = (oldReservation.totalSpots - newReservation.totalSpots)
            updateTripAvailableSpot(trip, leftOverSpot, TripAvailability.Add)
        }

        // Save changes
        return saveReservation(newReservation, id);
    }





    /**
     * Delete reservation
     */
    fun delete(id: Long) {
        // Get reservation
        val reservation = get(id)

        // Restore Trip Availability
        updateTripAvailableSpot(
                tripTable.findById(reservation.tripId),
                reservation.totalSpots,
                TripAvailability.Add
        )

        // Delete content
        return reservationTable.delete(reservation)
    }


    /**
     * Manage trip availability
     */
    private fun updateTripAvailableSpot(trip: Optional<Trip>, spot: Int, tripAvailabilityAction: TripAvailability){
        // Validate Trip id
        if(!trip.isPresent)
            throw ApiException("Trip is not available", HttpStatus.NOT_FOUND)


        when (tripAvailabilityAction){
            // Add to spot
            TripAvailability.Add ->
                tripTable.save(trip.get().copy(availableSpots = (trip.get().availableSpots + spot)))

            // Remove from spot
            TripAvailability.Remove ->
                tripTable.save(trip.get().copy(availableSpots = (trip.get().availableSpots - spot)))
        }
    }


    /**
     *  Save Request
     */
    private fun saveReservation(reservationRequest: ReservationRequest, id: Long): Reservation{
        return reservationTable.save(Reservation(
                id = id,
                tripId = reservationRequest.tripId,
                totalSpots = reservationRequest.totalSpots,
                customerName = reservationRequest.customerName,
                createdAt = Date()
            )
        )
    }



    /**
     * Validate request
     */
    private fun validateRequest(request: ReservationRequest){
        var errorMessage = "";
        if(ObjectUtils.isEmpty(request))
            errorMessage = "Request cannot be empty"
        else if(request.totalSpots <= 0)
            errorMessage = "Spot value must be greater than 0"
        if(errorMessage.isNotEmpty())
            throw ApiException(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY)
    }



    /**
     * Get Trip as Optional
     */
    private fun getAndValidateTrip(id: Long): Optional<Trip> {
        val result = tripTable.findById(id)
        if(!result.isPresent)
            throw ApiException("Trip is not available", HttpStatus.NOT_FOUND)
        return result;
    }


    /**
     * Compare trip availability with requested spot(s)
     */
    private fun validateAvailability(tripAvailability: Int, newRequestAvailability: Int){
        if(tripAvailability <= 0)
            throw ApiException("Sold out", HttpStatus.NOT_ACCEPTABLE)
        else if((tripAvailability - newRequestAvailability) < 0)
            throw ApiException("Not enough spots", HttpStatus.NOT_ACCEPTABLE)
    }




}