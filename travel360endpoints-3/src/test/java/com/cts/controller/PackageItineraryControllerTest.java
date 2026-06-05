package com.cts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.config.JWTUtil;
import com.cts.dto.PackageItineraryResponceDTO;
import com.cts.entity.PackageItinerary;
import com.cts.service.PackageItineraryService;

@WebMvcTest(PackageItineraryController.class)
@AutoConfigureMockMvc(addFilters = false)
class PackageItineraryControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockBean
    private PackageItineraryService service;

    @Autowired
    private MockMvc mockMvc;

    // ✅ CREATE
    @Test
    void testCreate() throws Exception {

        when(service.save(any())).thenReturn(new PackageItinerary());

        String body = """
        {
          "packageId":1,
          "start_date":"2030-01-01",
          "end_date":"2030-01-03",
          "status":"ACTIVE",
          "notes":"test"
        }
        """;

        mockMvc.perform(post("/package/itineraries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ GET ALL
    @Test
    void testGetAll() throws Exception {

        when(service.getAll())
                .thenReturn(List.of(new PackageItinerary()));

        mockMvc.perform(get("/package/itineraries/all"))
                .andExpect(status().isOk());
    }

    // ✅ GET BY ID
    @Test
    void testGetById() throws Exception {

        when(service.getItineraryById(1L))
                .thenReturn(new PackageItineraryResponceDTO());

        mockMvc.perform(get("/package/itineraries/1"))
                .andExpect(status().isOk());
    }

    // ✅ DELETE
    @Test
    void testDelete() throws Exception {

        mockMvc.perform(delete("/package/itineraries/1"))
                .andExpect(status().isOk());
    }
}