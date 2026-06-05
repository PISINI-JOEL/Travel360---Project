package com.cts.serviceimpl;

import com.cts.dto.PackageItineraryRequestDTO;
import com.cts.entity.PackageItinerary;
import com.cts.entity.TravelPackage;
import com.cts.exception.PackageItineraryNotFound;
import com.cts.exception.PackageNotFoundException;
import com.cts.repository.PackageItineraryRepository;
import com.cts.repository.TravelPackageRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class PackageItineraryServiceImplTest {

    @Mock private PackageItineraryRepository itineraryRepo;
    @Mock private TravelPackageRepository packageRepo;

    @InjectMocks
    private PackageItineraryServiceImpl service;

    private PackageItineraryRequestDTO dto;
    private TravelPackage pkg;

    @BeforeEach
    void setup() {

        dto = new PackageItineraryRequestDTO();
        dto.setPackageId(1L);
        dto.setStart_date(LocalDate.now());
        dto.setEnd_date(LocalDate.now().plusDays(2));
        dto.setStatus("ACTIVE");
        dto.setNotes("Test");

        pkg = new TravelPackage();
        pkg.setPackageId(1L);
    }

    // ✅ SAVE SUCCESS
    @Test
    void save_success() {

        when(packageRepo.findById(1L)).thenReturn(Optional.of(pkg));
        when(itineraryRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        assertNotNull(service.save(dto));
    }

    // ✅ SAVE FAIL (PACKAGE NOT FOUND)
    @Test
    void save_packageNotFound() {

        when(packageRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PackageNotFoundException.class,
                () -> service.save(dto));
    }

    // ✅ GET ALL
    @Test
    void getAll() {

        when(itineraryRepo.findAll())
                .thenReturn(List.of(new PackageItinerary()));

        assertFalse(service.getAll().isEmpty());
    }

    // ✅ GET BY ID SUCCESS
    @Test
    void getById_success() {

        PackageItinerary itinerary = new PackageItinerary();
        itinerary.setPackageItineraryId(1L);
        itinerary.setTravelPackage(pkg);

        when(itineraryRepo.findById(1L))
                .thenReturn(Optional.of(itinerary));

        assertNotNull(service.getItineraryById(1L));
    }

    // ✅ GET BY ID FAIL
    @Test
    void getById_notFound() {

        when(itineraryRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(PackageItineraryNotFound.class,
                () -> service.getItineraryById(1L));
    }

    // ✅ DELETE SUCCESS
    @Test
    void delete_success() {

        when(itineraryRepo.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(itineraryRepo).deleteById(1L);
    }

    // ✅ DELETE FAIL
    @Test
    void delete_notFound() {

        when(itineraryRepo.existsById(1L)).thenReturn(false);

        assertThrows(PackageItineraryNotFound.class,
                () -> service.delete(1L));
    }
}