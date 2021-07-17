package com.flixbus.flixtrip.models
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "trips")
data class Trip(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long =  0,

        val fromCity: String = "",
        val toCity: String = "",
        val startAt: Date? = null,
        val totalSpot: Int = 0,
        val availableSpot: Int = 0,
        val createdAt: Date? = null,

        @OneToMany(
                mappedBy = "tripId",
                orphanRemoval = true,
                fetch = FetchType.LAZY,
                cascade = [CascadeType.ALL])
        val reservations: List<Reservation> = emptyList(),

        @Version
        private val version: Long = 0,
    )

