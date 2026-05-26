package com.cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Transport;
import com.cts.enums.TransportStatus;

public interface TransportRepository extends JpaRepository<Transport, Long> {

    List<Transport> findBySourceAndDestination(String source, String destination);

    List<Transport> findByTransportStatus(TransportStatus status);
}
