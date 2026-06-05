package com.cts.serviceimpl;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.dto.InvoiceDTO;
import com.cts.dto.InvoiceResponseDTO;
import com.cts.entity.Booking;
import com.cts.entity.Invoice;
import com.cts.entity.User;
import com.cts.enums.PaymentStatus;
import com.cts.exception.InvoiceNotFoundException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.BookingRepository;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.UserRepository;
import com.cts.service.AuditLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock private InvoiceRepository invoiceRepo;
    @Mock private UserRepository userRepo;
    @Mock private BookingRepository bookingRepo;
    @Mock private AuthenticatedUserProvider authUser;
    @Mock private AuditLogService auditLogService;

    @InjectMocks
    private InvoiceServiceImpl service;

    private User user;
    private Booking booking;
    private Invoice invoice;

    @BeforeEach
    void setup() {

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@mail.com");

        booking = new Booking();
        booking.setBookingId(10L);
        booking.setUser(user);
        booking.setAmount(1000.0);

        invoice = Invoice.builder()
                .invoiceId(100L)
                .amount(1000.0)
                .status(PaymentStatus.PENDING)
                .booking(booking)
                .build();
    }

    // ✅ CREATE
    @Test
    void createInvoice_success() {

        InvoiceDTO dto = new InvoiceDTO();
        dto.setBookingId(10L);

        when(bookingRepo.findById(10L)).thenReturn(Optional.of(booking));
        when(invoiceRepo.save(any())).thenReturn(invoice);

        InvoiceResponseDTO response = service.createInvoice(dto);

        assertNotNull(response);
        assertEquals(100L, response.getInvoiceId());
        verify(auditLogService).logAction(any(), any(), any(), any(), any());
    }

    @Test
    void createInvoice_bookingNotFound() {

        InvoiceDTO dto = new InvoiceDTO();
        dto.setBookingId(99L);

        when(bookingRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.createInvoice(dto));
    }

    // ✅ GET BY BOOKING
    @Test
    void getInvoicesByBooking_success() {

        when(bookingRepo.findById(10L)).thenReturn(Optional.of(booking));
        when(invoiceRepo.findByBookingBookingId(10L)).thenReturn(List.of(invoice));

        List<InvoiceResponseDTO> list = service.getInvoicesByBooking(10L);

        assertEquals(1, list.size());
        verify(authUser).assertCanActAs(1L);
    }

    @Test
    void getInvoicesByBooking_bookingNotFound() {

        when(bookingRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getInvoicesByBooking(99L));
    }

    // ✅ GET ALL
    @Test
    void getAllInvoices_success() {

        when(invoiceRepo.findAll()).thenReturn(List.of(invoice));

        List<InvoiceResponseDTO> list = service.getAllInvoices();

        assertEquals(1, list.size());
        assertEquals(100L, list.get(0).getInvoiceId());
    }

    // ✅ GET BY ID
    @Test
    void getInvoiceById_success() {

        when(invoiceRepo.findById(100L)).thenReturn(Optional.of(invoice));

        InvoiceResponseDTO response = service.getInvoiceById(100L);

        assertNotNull(response);
        assertEquals(100L, response.getInvoiceId());
        verify(authUser).assertCanActAs(1L);
    }

    @Test
    void getInvoiceById_notFound() {

        when(invoiceRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class,
                () -> service.getInvoiceById(99L));
    }
}
