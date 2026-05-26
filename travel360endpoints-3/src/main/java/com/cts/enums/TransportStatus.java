package com.cts.enums;

public enum TransportStatus {

	 	AVAILABLE,        // seats available for booking
	    FULLY_BOOKED,     // no seats available
	    SCHEDULED,        // trip planned but not started
	    DEPARTED,         // transport has started journey
	    ARRIVED,          // reached destination
	    DELAYED,          // running late
	    CANCELLED,        // trip cancelled
	    OUT_OF_SERVICE    // not operational / maintenance

}
