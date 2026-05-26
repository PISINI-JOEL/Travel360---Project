package com.cts.enums;

public enum BookingStatus {

	PENDING,      // booking created, payment not done
    CONFIRMED,    // payment successful, ticket booked
    CANCELLED,    // user/admin cancelled booking
    FAILED,       // payment failed
    EXPIRED       // booking timeout (no payment)

}
