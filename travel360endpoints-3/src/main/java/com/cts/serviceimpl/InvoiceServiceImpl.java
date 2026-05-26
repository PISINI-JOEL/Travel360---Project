package com.cts.serviceimpl;

import com.cts.dto.InvoiceDTO;
import com.cts.dto.InvoiceResponseDTO;
import com.cts.entity.Booking;
import com.cts.entity.Invoice;
import com.cts.entity.User;
import com.cts.enums.PaymentStatus;
import com.cts.exception.InvoiceNotFoundException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.exception.UserNotFoundException;
import com.cts.repository.BookingRepository;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.UserRepository;
import com.cts.service.InvoiceService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

	private final InvoiceRepository invoiceRepo;
	private final UserRepository userRepo;
	private final BookingRepository bookingRepo;


@Override
public InvoiceResponseDTO createInvoice(InvoiceDTO dto) {

    Booking booking = bookingRepo.findById(dto.getBookingId())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

    
    User user = booking.getUser();

    Invoice invoice = Invoice.builder()
            .invoiceDate(LocalDateTime.now())
            .amount(booking.getAmount())
            .status(PaymentStatus.PENDING)
            .booking(booking)
            .build();

    invoice = invoiceRepo.save(invoice);

    return mapToDTO(invoice);
}


	
@Override
public List<InvoiceResponseDTO> getInvoicesByBooking(Long bookingId) {

    List<Invoice> invoices = invoiceRepo.findByBookingBookingId(bookingId);

    return invoices.stream()
            .map(this::mapToDTO)
            .toList();
}
	
	@Override
	public List<InvoiceResponseDTO> getAllInvoices() {

		List<Invoice> invoices = invoiceRepo.findAll();

		return invoices.stream().map(this::mapToDTO).toList();
	}

	
	@Override
	public InvoiceResponseDTO getInvoiceById(Long id) {

		Invoice invoice = invoiceRepo.findById(id).orElseThrow(()-> new InvoiceNotFoundException("Invoice not found"));

		

		return mapToDTO(invoice);
	}

	

private InvoiceResponseDTO mapToDTO(Invoice invoice) {

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
