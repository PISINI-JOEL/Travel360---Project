package com.cts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.cts.dto.PartnerResponseDTO;
import com.cts.enums.PartnerType;
import com.cts.service.AuditLogService;
import com.cts.service.PartnerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PartnerController.class)
@AutoConfigureMockMvc(addFilters = false)
class PartnerControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;   // ✅ fixes security dependency

    @MockBean
    private PartnerService service;

    @MockBean
    private AuditLogService auditLogService;

    @MockBean
    private AuthenticatedUserProvider authUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    // ✅ CREATE
    @Test
    void testCreatePartner() throws Exception {

        when(service.createPartner(any()))
                .thenReturn(new PartnerResponseDTO());

        String body = """
        {
          "name":"Test Partner",
          "type":"FLIGHT",
          "status":"ACTIVE"
        }
        """;

        mockMvc.perform(post("/api/partners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ UPDATE
    @Test
    void testUpdatePartner() throws Exception {

        when(service.updatePartner(eq(1L), any()))
                .thenReturn(new PartnerResponseDTO());

        String body = """
        {
          "name":"Updated Partner",
          "type":"HOTEL",
          "status":"ACTIVE"
        }
        """;

        mockMvc.perform(put("/api/partners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    // ✅ DELETE
    @Test
    void testDeletePartner() throws Exception {

        mockMvc.perform(delete("/api/partners/1"))
                .andExpect(status().isOk());
    }

    // ✅ GET BY CATEGORY
    @Test
    void testGetPartnerByCategory() throws Exception {

        when(service.getPartnerByCategory(PartnerType.FLIGHT))
                .thenReturn(List.of(new PartnerResponseDTO()));

        mockMvc.perform(get("/api/partners/category/FLIGHT"))
                .andExpect(status().isOk());
    }

    // ✅ GET BY ID
    @Test
    void testGetPartnerById() throws Exception {

        when(service.getPartnerById(1L))
                .thenReturn(new PartnerResponseDTO());

        mockMvc.perform(get("/api/partners/1"))
                .andExpect(status().isOk());
    }

    // ✅ VALIDATION / BAD REQUEST (optional coverage boost)
    @Test
    void testCreatePartner_validationFail() throws Exception {

        String body = "{}";

        mockMvc.perform(post("/api/partners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().is4xxClientError());
    }
}
