package com.cts.service;


import java.util.List;

import org.springframework.data.domain.Page;

import com.cts.dto.TransportDTO;
import com.cts.dto.TransportResponseDTO;
import com.cts.enums.TransportStatus;

public interface TransportService {

    TransportResponseDTO addTransport(TransportDTO dto);

    List<TransportResponseDTO> getAllTransports(int page,int size);

    List<TransportResponseDTO> findByRoute(String source, String destination,int page,int size);

    List<TransportResponseDTO> findByStatus(TransportStatus status, int page,int size);
}