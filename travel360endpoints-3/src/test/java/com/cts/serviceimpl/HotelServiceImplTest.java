package com.cts.serviceimpl;

import com.cts.dto.HotelDTO;
import com.cts.dto.HotelResponseDTO;
import com.cts.entity.Hotel;
import com.cts.entity.Partner;
import com.cts.enums.HotelStatus;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.exception.HotelNotFoundException;
import com.cts.exception.InvalidPartnerException;
import com.cts.exception.PartnerNotFoundException;
import com.cts.config.AuthenticatedUserProvider;
import com.cts.repository.HotelRepository;
import com.cts.repository.PartnerRepository;
import com.cts.service.AuditLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private AuthenticatedUserProvider authUser;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private HotelDTO dto;
    private Partner partner;

    @BeforeEach
    void setUp() {

        dto = new HotelDTO();
        dto.setHotelName("Test Hotel");
        dto.setCity("Chennai");
        dto.setPrice(2000.0);
        dto.setRatings(4);
        dto.setContactNo("9876543210");
        dto.setEmailId("test@mail.com");
        dto.setTotalRooms(10);
        dto.setStatus(HotelStatus.AVAILABLE);
        dto.setPartnerId(1L);

        partner = new Partner();
        partner.setPartnerId(1L);
        partner.setType(PartnerType.HOTEL);
        partner.setStatus(PartnerStatus.ACTIVE);
    }

    // ✅ ADD HOTEL

    @Test
    void addHotel_success() {

        when(partnerRepository.findById(1L)).thenReturn(Optional.of(partner));
        when(hotelRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Hotel result = hotelService.addHotel(dto);

        assertNotNull(result);
        assertEquals("Test Hotel", result.getHotelName());
    }

    @Test
    void addHotel_partnerNotFound() {

        when(partnerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> hotelService.addHotel(dto));
    }

    @Test
    void addHotel_invalidPartnerType() {

        partner.setType(PartnerType.FLIGHT);

        when(partnerRepository.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> hotelService.addHotel(dto));
    }

    @Test
    void addHotel_inactivePartner() {

        partner.setStatus(PartnerStatus.INACTIVE);

        when(partnerRepository.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> hotelService.addHotel(dto));
    }

    // ✅ UPDATE HOTEL

    @Test
    void updateHotel_success() {

        Hotel hotel = new Hotel();
        hotel.setHotelId(1L);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(partner));
        when(hotelRepository.save(any())).thenReturn(hotel);

        Hotel result = hotelService.updateHotel(1L, dto);

        assertEquals(1L, result.getHotelId());
    }

    @Test
    void updateHotel_notFound() {

        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class,
                () -> hotelService.updateHotel(1L, dto));
    }

    @Test
    void updateHotel_partnerNotFound() {

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(new Hotel()));
        when(partnerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PartnerNotFoundException.class,
                () -> hotelService.updateHotel(1L, dto));
    }

    // ✅ ✅ MISSING BRANCHES FIXED

    @Test
    void updateHotel_invalidPartnerType() {

        Hotel hotel = new Hotel();
        hotel.setHotelId(1L);

        partner.setType(PartnerType.FLIGHT);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> hotelService.updateHotel(1L, dto));
    }

    @Test
    void updateHotel_inactivePartner() {

        Hotel hotel = new Hotel();
        hotel.setHotelId(1L);

        partner.setStatus(PartnerStatus.INACTIVE);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(partnerRepository.findById(1L)).thenReturn(Optional.of(partner));

        assertThrows(InvalidPartnerException.class,
                () -> hotelService.updateHotel(1L, dto));
    }

    // ✅ FILTER HOTELS

    @Test
    void getFilteredHotels_success() {

        Page<Hotel> page = new PageImpl<>(List.of(new Hotel()));

        when(hotelRepository.filterHotels(any(), any(), any(), any(), any()))
                .thenReturn(page);

        List<HotelResponseDTO> result = hotelService.getFilteredHotels(
                "Chennai", 4, 1000.0, 5000.0, 0, 5);

        assertFalse(result.isEmpty());
    }

    // ✅ ✅ FULL MAPPING COVERAGE

    @Test
    void getFilteredHotels_mappingCoverage() {

        Hotel hotel = new Hotel();
        hotel.setHotelId(1L);
        hotel.setHotelName("Test Hotel");
        hotel.setCity("Chennai");
        hotel.setPrice(1500.0);
        hotel.setRatings(4);
        hotel.setContactNo("9876543210");
        hotel.setEmailId("test@mail.com");
        hotel.setTotalRooms(5);
        hotel.setStatus(HotelStatus.AVAILABLE);
        hotel.setPartner(partner);

        Page<Hotel> page = new PageImpl<>(List.of(hotel));

        when(hotelRepository.filterHotels(any(), any(), any(), any(), any()))
                .thenReturn(page);

        List<HotelResponseDTO> result = hotelService.getFilteredHotels(
                "Chennai", 4, 1000.0, 2000.0, 0, 5);

        HotelResponseDTO mapped = result.get(0);

        assertEquals("Test Hotel", mapped.getHotelName());
        assertEquals("Chennai", mapped.getCity());
        assertEquals(1500.0, mapped.getPrice());
        assertEquals(4, mapped.getRatings());
        assertEquals("9876543210", mapped.getContactNo());
        assertEquals("test@mail.com", mapped.getEmailId());
        assertEquals(5, mapped.getTotalRooms());
        assertEquals(HotelStatus.AVAILABLE, mapped.getStatus());
    }

    @Test
    void getFilteredHotels_emptyResult() {

        Page<Hotel> emptyPage = new PageImpl<>(Collections.emptyList());

        when(hotelRepository.filterHotels(any(), any(), any(), any(), any()))
                .thenReturn(emptyPage);

        List<HotelResponseDTO> result = hotelService.getFilteredHotels(
                null, null, null, null, 0, 5);

        assertTrue(result.isEmpty());
    }

    // ✅ FIND BY LOCATION

    @Test
    void findByLocation_success() {

        Page<Hotel> page = new PageImpl<>(List.of(new Hotel()));

        when(hotelRepository.findByCity(eq("Chennai"), any()))
                .thenReturn(page);

        List<HotelResponseDTO> result = hotelService.findByLocation("Chennai", 0, 5);

        assertEquals(1, result.size());
    }

    @Test
    void findByLocation_empty() {

        when(hotelRepository.findByCity(eq("Chennai"), any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        List<HotelResponseDTO> result = hotelService.findByLocation("Chennai", 0, 5);

        assertTrue(result.isEmpty());
    }
}