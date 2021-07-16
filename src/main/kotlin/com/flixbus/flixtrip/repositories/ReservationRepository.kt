package com.flixbus.flixtrip.repositories


import com.flixbus.flixtrip.enums.TripAvailability
import com.flixbus.flixtrip.helpers.ApiException
import com.flixbus.flixtrip.models.Reservation
import com.flixbus.flixtrip.models.Trip
import com.flixbus.flixtrip.models.requests.ReservationRequest
import com.flixbus.flixtrip.repositories.interfaces.IReservationRepository
import com.flixbus.flixtrip.repositories.interfaces.ITripRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils
import java.util.*

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
    fun create(request: ReservationRequest): Reservation {
        // Get trip
        val trip = tripTable.findById(request.tripId)

        // Validate request
        validateRequest(request)
        validateAvailability(trip.get().availableSpot, request.totalSpot)

        // Remove from Trip Availability
        updateTripAvailableSpot(trip, request.totalSpot, TripAvailability.Remove)

        // Save Changes
        return saveReservation(request, 0);
    }


    /**
     *  Update reservation
     */
    fun update(newReservation: ReservationRequest, id: Long): Reservation {
        // Get trip
        val trip = tripTable.findById(newReservation.tripId)

        // Validate request
        validateRequest(newReservation)

        // Get existing reservation
        val oldReservation = get(id)

        // Calculate differences in current and previous reservation
        if(newReservation.totalSpot > oldReservation.totalSpot){
            // Validate new request additional spot
            val additionalSpot = (newReservation.totalSpot - oldReservation.totalSpot)

            // Remove from Trip Availability
            validateAvailability(trip.get().availableSpot, additionalSpot);
            updateTripAvailableSpot(trip, additionalSpot, TripAvailability.Remove)

        }else if(newReservation.totalSpot < oldReservation.totalSpot){
            // Restore Trip Availability
            val leftOverSpot = (oldReservation.totalSpot - newReservation.totalSpot)
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
                reservation.totalSpot,
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

        // Add to spot
        if(tripAvailabilityAction == TripAvailability.Add)
            tripTable.save(trip.get().copy(availableSpot = (trip.get().availableSpot + spot)));

        // Remove from spot
        else if(tripAvailabilityAction == TripAvailability.Remove)
            tripTable.save(trip.get().copy(availableSpot = (trip.get().availableSpot - spot)));
    }

    /**
     *  Save Request
     */
    private fun saveReservation(reservationRequest: ReservationRequest, id: Long): Reservation{
        return reservationTable.save(Reservation(
                id = id,
                tripId = reservationRequest.tripId,
                totalSpot = reservationRequest.totalSpot,
                customerName = reservationRequest.customerName,
                createdAt = Date()
            )
        )
    }



    /**
     * Validate request and confirm spot availability
     */
    private fun validateRequest(request: ReservationRequest){
        // Validate Bad Request
        var errorMessage = "";
        if(ObjectUtils.isEmpty(request))
            errorMessage = "Request cannot be empty"
        else if(request.totalSpot <= 0)
            errorMessage = "Spot value must be greater than 0"
        if(errorMessage.isNotEmpty())
            throw ApiException(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY)
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