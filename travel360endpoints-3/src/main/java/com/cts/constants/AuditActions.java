package com.cts.constants;

public final class AuditActions {

    private AuditActions() {}

    public static final String CREATE_FLIGHT    = "CREATE_FLIGHT";
    public static final String UPDATE_FLIGHT    = "UPDATE_FLIGHT";

    public static final String CREATE_HOTEL     = "CREATE_HOTEL";
    public static final String UPDATE_HOTEL     = "UPDATE_HOTEL";

    public static final String CREATE_TRANSPORT = "CREATE_TRANSPORT";
    public static final String UPDATE_TRANSPORT = "UPDATE_TRANSPORT";

    public static final String CREATE_PACKAGE   = "CREATE_PACKAGE";
    public static final String UPDATE_PACKAGE   = "UPDATE_PACKAGE";

    public static final String CREATE_PARTNER   = "CREATE_PARTNER";
    public static final String UPDATE_PARTNER   = "UPDATE_PARTNER";
    public static final String DELETE_PARTNER   = "DELETE_PARTNER";

    public static final String CREATE_BOOKING   = "CREATE_BOOKING";
    public static final String CANCEL_BOOKING   = "CANCEL_BOOKING";
    public static final String CANCEL_PASSENGER = "CANCEL_PASSENGER";
    public static final String BOOKING_FAILED   = "BOOKING_FAILED";

    public static final String CREATE_INVOICE   = "CREATE_INVOICE";

    public static final String MAKE_PAYMENT     = "MAKE_PAYMENT";

    public static final String REGISTER_USER    = "REGISTER_USER";
    public static final String LOGIN_USER       = "LOGIN_USER";

    public static final String CREATE_ITINERARY            = "CREATE_ITINERARY";
    public static final String UPDATE_ITINERARY            = "UPDATE_ITINERARY";
    public static final String DELETE_ITINERARY            = "DELETE_ITINERARY";
    public static final String ADD_BOOKING_TO_ITINERARY    = "ADD_BOOKING_TO_ITINERARY";
    public static final String REMOVE_BOOKING_FROM_ITINERARY = "REMOVE_BOOKING_FROM_ITINERARY";

    public static final String CREATE_PACKAGE_ITINERARY = "CREATE_PACKAGE_ITINERARY";
    public static final String DELETE_PACKAGE_ITINERARY = "DELETE_PACKAGE_ITINERARY";
}
