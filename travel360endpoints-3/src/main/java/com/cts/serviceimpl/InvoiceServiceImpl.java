package com.cts.serviceimpl;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.InvoiceDTO;
import com.cts.dto.InvoiceResponseDTO;
import com.cts.entity.Booking;
import com.cts.entity.Invoice;
import com.cts.entity.User;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.enums.PaymentStatus;
import com.cts.exception.InvoiceNotFoundException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.BookingRepository;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.UserRepository;
import com.cts.service.AuditLogService;
import com.cts.service.InvoiceService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    @Override
    public InvoiceResponseDTO createInvoice(InvoiceDTO dto) {

        log.info("Creating invoice for bookingId: {}", dto.getBookingId());

        Booking booking = bookingRepo.findById(dto.getBookingId())
                .orElseThrow(() -> {
                    log.error("Booking not found with ID: {}", dto.getBookingId());
                    return new ResourceNotFoundException("Booking not found");
                });

        User user = booking.getUser();

        Invoice invoice = Invoice.builder()
                .invoiceDate(LocalDateTime.now())
                .amount(booking.getAmount())
                .status(PaymentStatus.PENDING)
                .booking(booking)
                .build();

        invoice = invoiceRepo.save(invoice);
        auditLogService.logAction(AuditActions.CREATE_INVOICE, AuditEntity.INVOICE, invoice.getInvoiceId(), user, LogType.INFO);

        log.info("Invoice created successfully with ID: {}", invoice.getInvoiceId());

        return mapToDTO(invoice);
    }

    @Override
    public List<InvoiceResponseDTO> getInvoicesByBooking(Long bookingId) {

        log.info("Fetching invoices for bookingId: {}", bookingId);

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Booking not found with ID: {}", bookingId);
                    return new ResourceNotFoundException("Booking not found");
                });

        authUser.assertCanActAs(booking.getUser().getUserId());

        List<Invoice> invoices = invoiceRepo.findByBookingBookingId(bookingId);

        log.info("Found {} invoices for bookingId: {}", invoices.size(), bookingId);

        return invoices.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public List<InvoiceResponseDTO> getAllInvoices() {

        log.info("Fetching all invoices");

        List<Invoice> invoices = invoiceRepo.findAll();

        log.info("Total invoices fetched: {}", invoices.size());

        return invoices.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public InvoiceResponseDTO getInvoiceById(Long id) {

        log.info("Fetching invoice with ID: {}", id);

        Invoice invoice = invoiceRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Invoice not found with ID: {}", id);
                    return new InvoiceNotFoundException("Invoice not found");
                });

        authUser.assertCanActAs(invoice.getBooking().getUser().getUserId());

        log.info("Invoice fetched successfully with ID: {}", id);

        return mapToDTO(invoice);
    }

    private InvoiceResponseDTO mapToDTO(Invoice invoice) {

        log.debug("Mapping invoice to DTO for invoiceId: {}", invoice.getInvoiceId());

        Booking booking = invoice.getBooking();
        User user = booking != null ? booking.getUser() : null;

        return InvoiceResponseDTO.builder()
                .invoiceId(invoice.getInvoiceId())
                .amount(invoice.getAmount())
                .status(invoice.getStatus())
                .bookingId(booking != null ? booking.getBookingId() : null)
                .userId(user != null ? user.getUserId() : null)
                .email(user != null ? user.getEmail() : null)
                .build();
    }
}