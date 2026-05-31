package com.cts.serviceimpl;

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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepo;
    private final InvoiceRepository invoiceRepo;
    private final BookingRepository bookingRepo;
    private final NotificationService notificationService;
    

   
    @Override
    @Transactional
    public PaymentResponseDTO makePayment(PaymentDTO dto) {

        Invoice invoice = invoiceRepo.findById(dto.getInvoiceId())
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found"));

        // Only an unpaid invoice can be paid. This blocks paying the same invoice
        // twice (retries / double-clicks) and paying a REFUNDED invoice.
        if (invoice.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidBookingException("Invoice is not payable (status: " + invoice.getStatus() + ")");
        }

        if (dto.getAmount() != invoice.getAmount()) {
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
     // 1. Update Connected Booking Status
        Booking booking = invoice.getBooking();
        if (booking != null) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepo.save(booking);
        }

        // 2. Update Invoice Status
        invoice.setStatus(PaymentStatus.SUCCESS);
        invoiceRepo.save(invoice);

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

        List<Payment> list = paymentRepo.findByInvoiceInvoiceId(invoiceId);

        return list.stream().map(this::mapToDTO).toList();
    }

   
    @Override
    public PaymentResponseDTO getPaymentById(Long id) {

        Payment payment = paymentRepo.findById(id).orElse(null);

        if (payment == null) {
        	throw new PaymentNotFoundException("No Payment with that ID");
        }

        return mapToDTO(payment);
    }

  
    @Override
    public List<PaymentResponseDTO> getAllPayments() {

        List<Payment> list = paymentRepo.findAll();

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
