package com.cts.serviceimpl;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.dto.PaymentDTO;
import com.cts.dto.PaymentResponseDTO;
import com.cts.entity.Booking;
import com.cts.entity.Invoice;
import com.cts.entity.Payment;
import com.cts.enums.BookingStatus;
import com.cts.enums.NotificationCategory;
import com.cts.enums.PaymentStatus;
import com.cts.exception.InvalidBookingException;
import com.cts.exception.InvoiceNotFoundException;
import com.cts.exception.PaymentNotFoundException;
import com.cts.repository.BookingRepository;
import com.cts.repository.InvoiceRepository;
import com.cts.repository.PaymentRepository;
import com.cts.service.NotificationService;
import com.cts.service.PaymentService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final InvoiceRepository invoiceRepo;
    private final BookingRepository bookingRepo;
    private final NotificationService notificationService;
    private final AuthenticatedUserProvider authUser;
    

   
    @Override
    @Transactional
    public PaymentResponseDTO makePayment(PaymentDTO dto) {

        log.info("Processing payment for invoiceId: {} with amount: {}", dto.getInvoiceId(), dto.getAmount());

        Invoice invoice = invoiceRepo.findById(dto.getInvoiceId())
                .orElseThrow(() -> {
                    log.error("Invoice not found with id {}", dto.getInvoiceId());
                    return new InvoiceNotFoundException("Invoice not found");
                });

        authUser.assertCanActAs(invoice.getBooking().getUser().getUserId());

        // Only an unpaid invoice can be paid. This blocks paying the same invoice
        // twice (retries / double-clicks) and paying a REFUNDED invoice.
        if (invoice.getStatus() != PaymentStatus.PENDING) {
            log.error("Invoice {} is not payable, current status: {}", invoice.getInvoiceId(), invoice.getStatus());
            throw new InvalidBookingException("Invoice is not payable (status: " + invoice.getStatus() + ")");
        }

        if (dto.getAmount() != invoice.getAmount()) {
            log.error("Partial payment attempt for invoice {}: paid {}, expected {}",
                    invoice.getInvoiceId(), dto.getAmount(), invoice.getAmount());
            throw new PaymentNotFoundException("Partial payment not allowed");
        }

        Payment payment = Payment.builder()
                .paymentDate(LocalDateTime.now())
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .status(PaymentStatus.SUCCESS) 
                .invoice(invoice)
                .build();

        payment = paymentRepo.save(payment);

        log.info("Payment created successfully with ID: {} and status: {}",
                payment.getPaymentId(), payment.getStatus());
     // 1. Update Connected Booking Status
        Booking booking = invoice.getBooking();
        if (booking != null) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepo.save(booking);
            log.debug("Booking {} status updated to CONFIRMED", booking.getBookingId());
        }

        // 2. Update Invoice Status
        invoice.setStatus(PaymentStatus.SUCCESS);
        invoiceRepo.save(invoice);
        log.debug("Invoice {} status updated to SUCCESS", invoice.getInvoiceId());

        // 3. Notify the user that payment succeeded
        if (booking != null && booking.getUser() != null) {
            notificationService.sendNotification(booking.getUser(),
                    "Payment of " + payment.getAmount() + " received. Booking ID: "
                            + booking.getBookingId() + " is now confirmed.",
                    NotificationCategory.PAYMENT);
        }

        return mapToDTO(payment);
    }

  
    @Override
    public List<PaymentResponseDTO> getPaymentsByInvoice(Long invoiceId) {

        log.info("Fetching payments for invoiceId: {}", invoiceId);

        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> {
                    log.error("Invoice not found with id {}", invoiceId);
                    return new InvoiceNotFoundException("Invoice not found");
                });

        authUser.assertCanActAs(invoice.getBooking().getUser().getUserId());

        List<Payment> list = paymentRepo.findByInvoiceInvoiceId(invoiceId);

        log.info("Found {} payments for invoiceId: {}", list.size(), invoiceId);

        return list.stream().map(this::mapToDTO).toList();
    }

   
    @Override
    public PaymentResponseDTO getPaymentById(Long id) {

        log.info("Fetching payment with ID: {}", id);

        Payment payment = paymentRepo.findById(id).orElse(null);

        if (payment == null) {
        	log.error("Payment not found with id {}", id);
        	throw new PaymentNotFoundException("No Payment with that ID");
        }

        authUser.assertCanActAs(payment.getInvoice().getBooking().getUser().getUserId());

        return mapToDTO(payment);
    }

  
    @Override
    public List<PaymentResponseDTO> getAllPayments() {

        log.info("Fetching all payments");

        List<Payment> list = paymentRepo.findAll();

        log.info("Total payments fetched: {}", list.size());

        return list.stream().map(this::mapToDTO).toList();
    }

    
    private PaymentResponseDTO mapToDTO(Payment payment) {

        return PaymentResponseDTO.builder()
                .paymentId(payment.getPaymentId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .build();
    }
}
