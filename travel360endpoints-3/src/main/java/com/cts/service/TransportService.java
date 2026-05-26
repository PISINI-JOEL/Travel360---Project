package com.cts.service;

import java.util.List;

import com.cts.dto.TransportDTO;
import com.cts.dto.TransportResponseDTO;
import com.cts.enums.TransportStatus;

public interface TransportService {

    TransportResponseDTO addTransport(TransportDTO dto);

    List<TransportResponseDTO> getAllTransports();

    List<TransportResponseDTO> findByRoute(String source, String destination);

    List<TransportResponseDTO> findByStatus(TransportStatus status);
}