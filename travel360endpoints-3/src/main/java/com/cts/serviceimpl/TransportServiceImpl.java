package com.cts.serviceimpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.TransportDTO;
import com.cts.dto.TransportResponseDTO;
import com.cts.entity.Partner;
import com.cts.entity.Transport;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.enums.TransportStatus;
import com.cts.exception.InvalidPartnerException;
import com.cts.exception.PartnerNotFoundException;
import com.cts.exception.TransportNotFoundException;
import com.cts.repository.PartnerRepository;
import com.cts.repository.TransportRepository;
import com.cts.service.AuditLogService;
import com.cts.service.TransportService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class TransportServiceImpl implements TransportService {

    private final TransportRepository transportRepo;
    private final PartnerRepository partnerRepo;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    @Override
    public TransportResponseDTO addTransport(TransportDTO dto) {

        log.info("Adding new transport with partnerId: {}", dto.getPartnerId());

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> {
                    log.error("Partner not found with id {}", dto.getPartnerId());
                    return new PartnerNotFoundException(
                            "Partner not found with id " + dto.getPartnerId());
                });

        if (partner.getType() != PartnerType.BUS) {
            log.error("Invalid partner type for partnerId: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a BUS partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            log.error("Inactive partner: {}", partner.getPartnerId());
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
        auditLogService.logAction(AuditActions.CREATE_TRANSPORT, AuditEntity.TRANSPORT, transport.getTransportId(), authUser.currentOrNull(), LogType.INFO);

        log.info("Transport created successfully with ID: {}", transport.getTransportId());

        return mapToDTO(transport);
    }

    @Override
    @Transactional
    public TransportResponseDTO updateTransport(Long id, TransportDTO dto) {

        log.info("Updating transport with ID: {}", id);

        Transport transport = transportRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Transport not found with id {}", id);
                    return new TransportNotFoundException("Transport not found");
                });

        Partner partner = partnerRepo.findById(dto.getPartnerId())
                .orElseThrow(() -> {
                    log.error("Partner not found with id {}", dto.getPartnerId());
                    return new PartnerNotFoundException(
                            "Partner not found with id " + dto.getPartnerId());
                });

        if (partner.getType() != PartnerType.BUS) {
            log.error("Invalid partner type for partnerId: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not a BUS partner");
        }
        if (partner.getStatus() != PartnerStatus.ACTIVE) {
            log.error("Inactive partner: {}", partner.getPartnerId());
            throw new InvalidPartnerException(
                    "Partner " + partner.getPartnerId() + " is not active");
        }

        log.debug("Updating transport details for ID: {}", id);

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
        auditLogService.logAction(AuditActions.UPDATE_TRANSPORT, AuditEntity.TRANSPORT, transport.getTransportId(), authUser.currentOrNull(), LogType.INFO);

        log.info("Transport updated successfully with ID: {}", id);

        return mapToDTO(transport);
    }

    @Override
    public List<TransportResponseDTO> getAllTransports(int page,int size) {

        log.info("Fetching all transports (page={}, size={})", page, size);

    	Pageable pageable=PageRequest.of(page, size);
    	List<TransportResponseDTO> transports = transportRepo.findAll(pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();

        log.info("Total transports fetched: {}", transports.size());

        return transports;
    }

    @Override
    public List<TransportResponseDTO> findByRoute(String source, String destination,int page,int size) {

        log.info("Finding transports from '{}' to '{}' (page={}, size={})", source, destination, page, size);

    	Pageable pageable=PageRequest.of(page, size);

        List<TransportResponseDTO> transports = transportRepo.findBySourceAndDestination(source, destination,pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();

        log.info("Route search returned {} transports", transports.size());

        return transports;
    }

    @Override
    public List<TransportResponseDTO> findByStatus(TransportStatus status,int page,int size) {

        log.info("Finding transports with status: {} (page={}, size={})", status, page, size);

    	Pageable pageable=PageRequest.of(page, size);
        List<TransportResponseDTO> transports = transportRepo.findByTransportStatus(status,pageable)
                .stream()
                .map(this::mapToDTO)
                .toList();

        log.info("Status search returned {} transports", transports.size());

        return transports;
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
