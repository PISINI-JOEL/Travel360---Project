package com.cts.serviceimpl;

import com.cts.enums.TravelPackageCategory;
import com.cts.service.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock private FlightService flightService;
    @Mock private HotelService hotelService;
    @Mock private TravelPackageService packageService;
    @Mock private TransportService transportService;

    @InjectMocks
    private SearchServiceImpl service;

    // ✅ FLIGHT SUCCESS
    @Test
    void search_flight() {

        when(flightService.filterFlights(anyString(), anyString(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of());

        Object result = service.search("flight", "Chennai", "Delhi", null,
                1000.0, 5000.0, null, null, 0, 5);

        assertTrue(result instanceof List);
    }

    // ✅ HOTEL SUCCESS
    @Test
    void search_hotel() {

        when(hotelService.getFilteredHotels(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(List.of());

        Object result = service.search("hotel", null, null, "Chennai",
                1000.0, 5000.0, 4, null, 0, 5);

        assertTrue(result instanceof List);
    }

    // ✅ PACKAGE WITH CATEGORY
    @Test
    void search_package_withCategory() {

        when(packageService.searchByCategory(any(), anyInt(), anyInt()))
                .thenReturn(List.of());

        Object result = service.search("package", null, null, null,
                null, null, null, TravelPackageCategory.FAMILY, 0, 5);

        assertTrue(result instanceof List);
    }

    // ✅ PACKAGE WITHOUT CATEGORY
    @Test
    void search_package_withoutCategory() {

        when(packageService.getAllPackages(anyInt(), anyInt()))
                .thenReturn(List.of());

        Object result = service.search("package", null, null, null,
                null, null, null, null, 0, 5);

        assertTrue(result instanceof List);
    }

    // ✅ TRANSPORT
    @Test
    void search_transport() {

        when(transportService.findByRoute(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        Object result = service.search("transport", "Chennai", "Delhi", null,
                null, null, null, null, 0, 5);

        assertTrue(result instanceof List);
    }

    // ✅ INVALID TYPE
    @Test
    void search_invalidType() {

        assertThrows(IllegalArgumentException.class,
                () -> service.search("invalid", null, null, null,
                        null, null, null, null, 0, 5));
    }

    // ✅ VALIDATION FAIL (FLIGHT)
    @Test
    void search_flight_validationFail() {

        assertThrows(IllegalArgumentException.class,
                () -> service.search("flight", null, null, null,
                        null, null, null, null, 0, 5));
    }

    // ✅ VALIDATION FAIL (HOTEL)
    @Test
    void search_hotel_validationFail() {

        assertThrows(IllegalArgumentException.class,
                () -> service.search("hotel", null, null, null,
                        null, null, null, null, 0, 5));
    }

    // ✅ VALIDATION FAIL (TRANSPORT)
    @Test
    void search_transport_validationFail() {

        assertThrows(IllegalArgumentException.class,
                () -> service.search("transport", null, null, null,
                        null, null, null, null, 0, 5));
    }
}