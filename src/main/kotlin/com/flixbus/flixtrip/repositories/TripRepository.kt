package com.flixbus.flixtrip.repositories


import com.flixbus.flixtrip.helpers.ApiException
import com.flixbus.flixtrip.models.Reservation
import com.flixbus.flixtrip.models.Trip
import com.flixbus.flixtrip.models.requests.ReservationRequest
import com.flixbus.flixtrip.models.requests.TripRequest
import com.flixbus.flixtrip.repositories.interfaces.IReservationRepository
import com.flixbus.flixtrip.repositories.interfaces.ITripRepository
import org.hibernate.StaleObjectStateException
import org.springframework.http.HttpStatus
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Repository
import org.springframework.util.ObjectUtils
import java.util.*
import javax.persistence.OptimisticLockException
import javax.persistence.TypedQuery
import javax.transaction.Transactional

@Repository
class TripRepository(val reservationTable: IReservationRepository, val tripTable: ITripRepository) {


    /**
     *  Get trips where startDate is in future
     */
    fun getAvailableTrips(): List<Trip> {
        return tripTable.findByStartAtAfter(Date()).sortedByDescending { trip -> trip.id  };
    }



    /**
     *  Admin: Get trips
     */
    fun adminGetTrips(): List<Trip> {
        return tripTable.findAll().sortedByDescending { trip -> trip.id  }
    }


    /**
     * Admin: Get Trip
     */
    fun adminGetTrip(id: Long): Trip {
        val result = tripTable.findById(id);
        if(!result.isPresent)
            throw ApiException("Trip is not available", HttpStatus.NOT_FOUND)
        return result.get();
    }


    /**
     *  Admin: Create or Update Trip
     */
    fun adminUpdateOrCreateTrip(tripRequest: TripRequest, id: Long = 0): Trip {
        // validate request
        validateRequest(tripRequest);

        // validate trip exists for update.
        if(id>0 && !tripTable.existsById(id))
            throw ApiException("Trip is not available", HttpStatus.NOT_FOUND)

        val trip: Trip = (if(id>0) tripTable.findById(id).get() else Trip(createdAt = Date())).copy(
            id = id,
            fromCity = tripRequest.fromCity,
            toCity = tripRequest.toCity,
            startAt = tripRequest.startAt,
            totalSpot = tripRequest.availableSpot,
            availableSpot = tripRequest.availableSpot,
        )

        // Save
        return tripTable.save(trip)
    }


    /**
     *  Admin: Delete Trip
     */
    fun adminDeleteTrip(id: Long = 0) {
        tripTable.delete(adminGetTrip(id))
    }

    /**
     * Validate request
     */
    private fun validateRequest(request: TripRequest){
        var errorMessage = "";
        if(ObjectUtils.isEmpty(request))
            errorMessage = "Request cannot be empty"
        else if(request.startAt.time < Date().time)
            errorMessage = "Trip start date must be a future date"
        if(errorMessage.isNotEmpty())
            throw ApiException(errorMessage, HttpStatus.UNPROCESSABLE_ENTITY)
    }
}