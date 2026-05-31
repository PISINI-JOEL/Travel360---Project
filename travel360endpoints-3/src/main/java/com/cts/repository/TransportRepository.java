package com.cts.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Partner;
import com.cts.entity.Transport;
import com.cts.enums.TransportStatus;

public interface TransportRepository extends JpaRepository<Transport, Long> {

    List<Transport> findByPartner(Partner partner);

    Page<Transport> findBySourceAndDestination(String source, String destination, Pageable pageable);

    Page<Transport> findByTransportStatus(TransportStatus status, Pageable pageable);
    Page<Transport> findAll(Pageable pageable);
}
