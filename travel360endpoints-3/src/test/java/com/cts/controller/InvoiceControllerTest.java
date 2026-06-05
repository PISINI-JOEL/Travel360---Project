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

import com.cts.config.AuthenticatedUserProvider;
import com.cts.config.JWTUtil;
import com.cts.dto.InvoiceResponseDTO;
import com.cts.service.AuditLogService;
import com.cts.service.InvoiceService;

@WebMvcTest(InvoiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class InvoiceControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockBean
    private InvoiceService service;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private AuthenticatedUserProvider authUser;

    @Autowired
    private MockMvc mockMvc;

    // ✅ CREATE
    @Test
    void testCreateInvoice() throws Exception {

        when(service.createInvoice(any()))
                .thenReturn(InvoiceResponseDTO.builder().invoiceId(100L).build());

        String body = """
        {
          "bookingId":10
        }
        """;

        mockMvc.perform(post("/api/v1/invoices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    // ✅ GET ALL
    @Test
    void testGetAll() throws Exception {

        when(service.getAllInvoices())
                .thenReturn(List.of(InvoiceResponseDTO.builder().invoiceId(100L).build()));

        mockMvc.perform(get("/api/v1/invoices"))
                .andExpect(status().isOk());
    }

    // ✅ GET BY BOOKING
    @Test
    void testGetByBooking() throws Exception {

        when(service.getInvoicesByBooking(10L))
                .thenReturn(List.of(InvoiceResponseDTO.builder().invoiceId(100L).build()));

        mockMvc.perform(get("/api/v1/invoices/booking/10"))
                .andExpect(status().isOk());
    }

    // ✅ GET BY ID
    @Test
    void testGetById() throws Exception {

        when(service.getInvoiceById(100L))
                .thenReturn(InvoiceResponseDTO.builder().invoiceId(100L).build());

        mockMvc.perform(get("/api/v1/invoices/100"))
                .andExpect(status().isOk());
    }
}
