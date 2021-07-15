package com.flixbus.flixtrip

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FlixtripApplication

fun main(args: Array<String>) {
	runApplication<FlixtripApplication>(*args)
}
