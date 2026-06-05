package com.cts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cts.config.JWTUtil;
import com.cts.service.SearchService;

@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerTest {

    @MockitoBean
    private JWTUtil jwtUtil;   // ✅ fixes JWT dependency

    @MockBean
    private SearchService service;

    @Autowired
    private MockMvc mockMvc;

    // ✅ SEARCH SUCCESS (LIST RESULT)
    @Test
    void testSearch_listResult() throws Exception {

        when(service.search(
                any(), any(), any(), any(),
                any(), any(), any(), any(),
                anyInt(), anyInt()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/search")
                .param("type", "flight")
                .param("source", "Chennai")
                .param("destination", "Delhi"))
                .andExpect(status().isOk());
    }

    // ✅ ✅ SEARCH NON-LIST (COVERS ELSE BLOCK)
    @Test
    void testSearch_nonListResult() throws Exception {

        when(service.search(
                any(), any(), any(), any(),
                any(), any(), any(), any(),
                anyInt(), anyInt()))
                .thenReturn("SUCCESS"); // ✅ triggers ELSE branch

        mockMvc.perform(get("/api/v1/search")
                .param("type", "package"))
                .andExpect(status().isOk());
    }

    // ✅ VALIDATION FAIL (page < 0)
    @Test
    void testSearch_validationFail_page() throws Exception {

        mockMvc.perform(get("/api/v1/search")
                .param("type", "flight")
                .param("page", "-1"))
                .andExpect(status().is4xxClientError());
    }

    // ✅ VALIDATION FAIL (size > 100)
    @Test
    void testSearch_validationFail_size() throws Exception {

        mockMvc.perform(get("/api/v1/search")
                .param("type", "flight")
                .param("size", "200"))
                .andExpect(status().is4xxClientError());
    }
}
