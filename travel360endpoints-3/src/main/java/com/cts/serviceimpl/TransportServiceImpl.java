package com.cts.serviceimpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.dto.TransportDTO;
import com.cts.dto.TransportResponseDTO;
import com.cts.entity.Partner;
import com.cts.entity.Transport;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.enums.TransportStatus;
import com.cts.exception.InvalidPartnerException;
import com.cts.exception.PartnerNotFoundException;
import com.cts.exception.TransportNotFoundException;
import com.cts.repository.PartnerRepository;
import com.cts.repository.TransportRepository;
import com.cts.service.TransportService;

import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TransportServiceImpl implements TransportService {

    private final TransportRepository transportRepo;
    private final PartnerRepository partnerRepo;

    @Override
    public TransportResponseDTO addTransport(TransportDTO dto) {

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner not found with id " + dto.getPartnerId()));

        if (partner.getType() != PartnerType.BUS) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a BUS partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

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
                .partner(partner)
                .build();

        transport = transportRepo.save(transport);

        return mapToDTO(transport);
    }

    @Override
    @Transactional
    public TransportResponseDTO updateTransport(Long id, TransportDTO dto) {

        Transport transport = transportRepo.findById(id)
                .orElseThrow(() -> new TransportNotFoundException("Transport not found"));

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> new PartnerNotFoundException(
                        "Partner not found with id " + dto.getPartnerId()));

        if (partner.getType() != PartnerType.BUS) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a BUS partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        transport.setTransportNumber(dto.getTransportNumber());
        transport.setSource(dto.getSource());
        transport.setDestination(dto.getDestination());
        transport.setTransportType(dto.getTransportType());
        transport.setDepartureTime(dto.getDepartureTime());
        transport.setArrivalTime(dto.getArrivalTime());
        transport.setTransportTotalSeats(dto.getTransportTotalSeats());
        transport.setPrice(dto.getPrice());
        transport.setTransportStatus(dto.getTransportStatus());
        transport.setPartner(partner);

        transport = transportRepo.save(transport);

        return mapToDTO(transport);
    }

    @Override
    public List<TransportResponseDTO> getAllTransports(int page,int size) {

    	Pageable pageable=PageRequest.of(page, size);
    	return transportRepo.findAll(pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();
    	
    	
    }

    @Override
    public List<TransportResponseDTO> findByRoute(String source, String destination,int page,int size) {

    	Pageable pageable=PageRequest.of(page, size);
    	
        return transportRepo.findBySourceAndDestination(source, destination,pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<TransportResponseDTO> findByStatus(TransportStatus status,int page,int size) {

    	Pageable pageable=PageRequest.of(page, size);
        return transportRepo.findByTransportStatus(status,pageable)
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
