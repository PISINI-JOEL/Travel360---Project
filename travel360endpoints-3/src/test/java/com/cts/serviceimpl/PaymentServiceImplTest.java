package com.cts.serviceimpl;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.dto.PaymentDTO;
import com.cts.entity.*;
import com.cts.enums.*;
import com.cts.exception.*;

import com.cts.repository.*;
import com.cts.service.NotificationService;

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
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepo;
    @Mock private InvoiceRepository invoiceRepo;
    @Mock private BookingRepository bookingRepo;
    @Mock private NotificationService notificationService;
    @Mock private AuthenticatedUserProvider authUser;

    @InjectMocks
    private PaymentServiceImpl service;

    private PaymentDTO dto;
    private Invoice invoice;
    private Booking booking;
    private User user;

    @BeforeEach
    void setup() {

        dto = new PaymentDTO();
        dto.setInvoiceId(1L);
        dto.setAmount(1000.0);
        dto.setPaymentMethod("UPI");

        user = new User();
        user.setUserId(1L);

        booking = new Booking();
        booking.setBookingId(10L);
        booking.setUser(user);

        invoice = new Invoice();
        invoice.setInvoiceId(1L);
        invoice.setAmount(1000.0);
        invoice.setStatus(PaymentStatus.PENDING);
        invoice.setBooking(booking);
    }

    // ✅ MAKE PAYMENT SUCCESS
    @Test
    void makePayment_success() {

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
        when(paymentRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        doNothing().when(authUser).assertCanActAs(anyLong());

        assertNotNull(service.makePayment(dto));

        verify(notificationService).sendNotification(any(), any(), any());
    }

    // ✅ INVOICE NOT FOUND
    @Test
    void makePayment_invoiceNotFound() {

        when(invoiceRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class,
                () -> service.makePayment(dto));
    }

    // ✅ INVALID STATUS
    @Test
    void makePayment_invalidStatus() {

        invoice.setStatus(PaymentStatus.SUCCESS);

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
        doNothing().when(authUser).assertCanActAs(anyLong());

        assertThrows(InvalidBookingException.class,
                () -> service.makePayment(dto));
    }

    // ✅ PARTIAL PAYMENT
    @Test
    void makePayment_partialPayment() {

        dto.setAmount(500.0);

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
        doNothing().when(authUser).assertCanActAs(anyLong());

        assertThrows(PaymentNotFoundException.class,
                () -> service.makePayment(dto));
    }

    // ✅ GET BY INVOICE
    @Test
    void getPaymentsByInvoice() {

        when(invoiceRepo.findById(1L)).thenReturn(Optional.of(invoice));
        when(paymentRepo.findByInvoiceInvoiceId(1L))
                .thenReturn(List.of(new Payment()));

        doNothing().when(authUser).assertCanActAs(anyLong());

        assertFalse(service.getPaymentsByInvoice(1L).isEmpty());
    }

    // ✅ GET PAYMENT BY ID
    @Test
    void getPaymentById_success() {

        Payment p = new Payment();
        p.setInvoice(invoice);

        when(paymentRepo.findById(1L)).thenReturn(Optional.of(p));
        doNothing().when(authUser).assertCanActAs(anyLong());

        assertNotNull(service.getPaymentById(1L));
    }

    // ✅ PAYMENT NOT FOUND
    @Test
    void getPaymentById_notFound() {

        when(paymentRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class,
                () -> service.getPaymentById(1L));
    }
    
    @Test
    void getPaymentsByInvoice_invoiceNotFound() {

        when(invoiceRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class,
                () -> service.getPaymentsByInvoice(1L));
    }
    

    // ✅ GET ALL
    @Test
    void getAllPayments() {

        when(paymentRepo.findAll()).thenReturn(List.of(new Payment()));

        assertFalse(service.getAllPayments().isEmpty());
    }
}