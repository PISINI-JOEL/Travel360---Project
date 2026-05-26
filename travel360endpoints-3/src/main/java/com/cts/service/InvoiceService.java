package com.cts.service;

import com.cts.dto.InvoiceDTO;
import com.cts.dto.InvoiceResponseDTO;
import com.cts.entity.Invoice;

import java.util.List;

public interface InvoiceService {

    InvoiceResponseDTO createInvoice(InvoiceDTO dto);

    List<InvoiceResponseDTO> getInvoicesByBooking(Long bookingId);

    List<InvoiceResponseDTO> getAllInvoices();

    InvoiceResponseDTO getInvoiceById(Long id);
}
