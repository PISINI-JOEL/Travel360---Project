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
import com.cts.dto.PaymentResponseDTO;
import com.cts.enums.PaymentStatus;
import com.cts.service.AuditLogService;
import com.cts.service.PaymentService;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockBean
    private PaymentService service;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private AuthenticatedUserProvider authUser;

    @Autowired
    private MockMvc mockMvc;

    // ✅ MAKE PAYMENT
    @Test
    void testMakePayment() throws Exception {

        PaymentResponseDTO res = PaymentResponseDTO.builder()
                .paymentId(1L)
                .amount(1000)
                .status(PaymentStatus.SUCCESS)
                .build();

        when(service.makePayment(any())).thenReturn(res);

        String body = """
        {
          "invoiceId":1,
          "amount":1000,
          "paymentMethod":"UPI"
        }
        """;

        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ GET BY INVOICE (LIST)
    @Test
    void testGetByInvoice_list() throws Exception {

        when(service.getPaymentsByInvoice(1L))
                .thenReturn(List.of(new PaymentResponseDTO()));

        mockMvc.perform(get("/api/v1/payments/invoice/1"))
                .andExpect(status().isOk());
    }

    // ✅ GET BY INVOICE (EMPTY → covers if block)
    @Test
    void testGetByInvoice_empty() throws Exception {

        when(service.getPaymentsByInvoice(1L))
                .thenReturn(List.of()); // ✅ triggers "No payments found"

        mockMvc.perform(get("/api/v1/payments/invoice/1"))
                .andExpect(status().isOk());
    }

    // ✅ GET ALL
    @Test
    void testGetAll() throws Exception {

        when(service.getAllPayments())
                .thenReturn(List.of(new PaymentResponseDTO()));

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk());
    }

    // ✅ GET BY ID
    @Test
    void testGetById() throws Exception {

        when(service.getPaymentById(1L))
                .thenReturn(new PaymentResponseDTO());

        mockMvc.perform(get("/api/v1/payments/1"))
                .andExpect(status().isOk());
    }

    // ✅ VALIDATION FAIL
    @Test
    void testValidationFail() throws Exception {

        String body = "{}";

        mockMvc.perform(post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().is4xxClientError());
    }
}