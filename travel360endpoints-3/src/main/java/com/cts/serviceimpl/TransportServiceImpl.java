package com.cts.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.dto.TransportDTO;
import com.cts.dto.TransportResponseDTO;
import com.cts.entity.Transport;
import com.cts.enums.TransportStatus;
import com.cts.repository.TransportRepository;
import com.cts.service.TransportService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransportServiceImpl implements TransportService {

    private final TransportRepository transportRepo;

    @Override
    public TransportResponseDTO addTransport(TransportDTO dto) {

        Transport transport = Transport.builder()
                .transportNumber(dto.getTransportNumber())
                .source(dto.getSource())
                .destination(dto.getDestination())
                .transportType(dto.getTransportType())
                .departureTime(dto.getDepartureTime())
                .arrivalTime(dto.getArrivalTime())
                .transportTotalSeats(dto.getTransportTotalSeats())
                
                .price(dto.getPrice())
                .transportStatus(dto.getTransportStatus())
                .build();

        transport = transportRepo.save(transport);

        return mapToDTO(transport);
    }

    @Override
    public List<TransportResponseDTO> getAllTransports() {

        return transportRepo.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<TransportResponseDTO> findByRoute(String source, String destination) {

        return transportRepo.findBySourceAndDestination(source, destination)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<TransportResponseDTO> findByStatus(TransportStatus status) {

        return transportRepo.findByTransportStatus(status)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    
    private TransportResponseDTO mapToDTO(Transport t) {

        return TransportResponseDTO.builder()
                .transportId(t.getTransportId())
                .transportNumber(t.getTransportNumber())
                .source(t.getSource())
                .destination(t.getDestination())
                .transportType(t.getTransportType())
                .departureTime(t.getDepartureTime())
                .arrivalTime(t.getArrivalTime())
                
                .transportTotalSeats(t.getTransportTotalSeats())
                .price(t.getPrice())
                .transportStatus(t.getTransportStatus())
                .build();
    }
}
