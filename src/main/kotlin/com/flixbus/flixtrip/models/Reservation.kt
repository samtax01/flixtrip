package com.flixbus.flixtrip.models
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "reservations")
data class Reservation(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long = 0,

        val tripId: Long = 0,
        val totalSpot: Int = 0,
        val customerName: String = "",
        val createdAt: Date? = null
    )

